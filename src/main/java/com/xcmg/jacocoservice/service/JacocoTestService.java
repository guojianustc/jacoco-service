package com.xcmg.jacocoservice.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xcmg.jacocoservice.model.XcmgCodeProjectDO;
import com.xcmg.jacocoservice.model.XcmgCoverageOverallDataDO;
import com.xcmg.jacocoservice.model.XcmgCoverageTaskDO;
import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.data.*;
import org.jacoco.core.tools.ExecDumpClient;
import org.jacoco.core.tools.ExecFileLoader;
import org.jacoco.report.DirectorySourceFileLocator;
import org.jacoco.report.FileMultiReportOutput;
import org.jacoco.report.IReportVisitor;
import org.jacoco.report.MultiReportVisitor;
import org.jacoco.report.html.HTMLFormatter;
import org.jacoco.report.xml.XMLFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.xcmg.jacocoservice.constants.DumpConstants.*;

@Service
public class JacocoTestService {
    //    final static String JACOCO_SERVER_IP = "10.6.120.62";
//    final static String JACOCO_SERVER_PORT = "8202";
    @Autowired
    XcmgCoverageOverallDataService xcmgCoverageOverallDataService;
    @Autowired
    XcmgCoverageTaskService xcmgCoverageTaskService;

    public void dump(XcmgCodeProjectDO xcmgCodeProjectDO, String exccFilePath) throws IOException {
        ExecDumpClient client = new ExecDumpClient();
        client.setReset(true);
        client.setDump(true);
        File execFile = new File(exccFilePath);
        if (!execFile.exists()) {
            execFile.getParentFile().mkdirs();
        }
        execFile.createNewFile();
        ExecFileLoader execFileLoader = client.dump(xcmgCodeProjectDO.getServerIp(), Integer.parseInt(xcmgCodeProjectDO.getServerPort()));
        execFileLoader.save(execFile, true);
    }

    public void reportGenerator(XcmgCodeProjectDO xcmgCodeProjectDO, String exccFilePath, String reportPahh) throws IOException {
        ExecFileLoader execFileLoader = new ExecFileLoader();
        execFileLoader.load(new File(exccFilePath));
        // 创建一个覆盖率数据存储对象
        ExecutionDataStore executionDataStore = new ExecutionDataStore();

        // 收集覆盖率数据
        execFileLoader.getExecutionDataStore().accept(executionDataStore);

        // 创建一个覆盖率报告生成器
        CoverageBuilder coverageBuilder = new CoverageBuilder();
        Analyzer analyzer = new Analyzer(executionDataStore, coverageBuilder);

        // 指定你的源代码目录和类文件目录
        File sourceDirectory = new File(xcmgCodeProjectDO.getSourcePath());
        File classDirectory = new File(xcmgCodeProjectDO.getClassesPath());

        // 分析覆盖率数据
        analyzer.analyzeAll(classDirectory);

        // 创建报告输出目录
        File reportDir = new File(reportPahh);

        // 创建HTML和XML格式的报告
        List<IReportVisitor> visitors = new ArrayList<>();
        visitors.add(createHTMLReport(reportDir));

        // 合并多个报告
        MultiReportVisitor multiReportVisitor = new MultiReportVisitor(visitors);

        // 生成报告
        multiReportVisitor.visitInfo(execFileLoader.getSessionInfoStore().getInfos(), executionDataStore.getContents());
        multiReportVisitor.visitBundle(coverageBuilder.getBundle("MyBundle"), new DirectorySourceFileLocator(sourceDirectory, "utf-8", 4));
        // 关闭报告
        multiReportVisitor.visitEnd();
    }

    //todo 报告生成V2版本，多个版本dump文件合并， 生成覆盖率报告
    public Boolean mergeDumpFile(String projectId) throws IOException {
        //根据项目id获取所有任务数据
        List<XcmgCoverageTaskDO> tasks = xcmgCoverageTaskService.gettaskByProject(projectId);
        //创建数组存放多个exec文件
        List<File> execFiles = new ArrayList<File>();
        for (XcmgCoverageTaskDO task : tasks) {
            String taskFilePath = HOME_PATH + "/jacoco/" + task.getId() + "/" + EXEC_FILE_PATH;
            execFiles.add(new File(taskFilePath));
        }


        // 创建一个新的ExecutionDataStore和SessionInfoStore来保存合并后的数据
        ExecutionDataStore executionDataStore = new ExecutionDataStore();
        SessionInfoStore sessionInfoStore = new SessionInfoStore();
        execFiles.forEach(execFile -> {
            // 加载exec文件
            ExecFileLoader execFileLoader = new ExecFileLoader();

            try {
                execFileLoader.load(new FileInputStream(execFile));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // 合并ExecutionData
            for (ExecutionData executionData : execFileLoader.getExecutionDataStore().getContents()) {
                executionDataStore.put(executionData);
            }

            // 合并SessionInfo
            for (SessionInfo sessionInfo : execFileLoader.getSessionInfoStore().getInfos()) {
                sessionInfoStore.visitSessionInfo(sessionInfo);
            }
                }
        );
        // 创建一个新的exec文件来保存合并后的数据
        String mergeFilePath = HOME_PATH + "/jacoco/mergeFile/" + projectId + "/" + MERGE_FILE_NAME;
        File mergedExecFile = new File(mergeFilePath);
        mergedExecFile.getParentFile().mkdirs();
        mergedExecFile.createNewFile();

        // 保存合并后的数据到新的exec文件
        FileOutputStream fileOutputStream = new FileOutputStream(mergedExecFile);
        ExecutionDataWriter executionDataWriter = new ExecutionDataWriter(fileOutputStream);
        executionDataStore.accept(executionDataWriter);
        sessionInfoStore.accept(executionDataWriter);
        // 关闭文件流
        fileOutputStream.close();
        return true;
    }



    //解析报数据告保存到数据库
    public Boolean parseReportData(String reportId, String reportPath) throws IOException {
        File reportFile = new File(reportPath + "/index.html");
        Document reportDoc = Jsoup.parse(reportFile, "utf-8");
        XcmgCoverageOverallDataDO xcmgCoverageOverallDataDO = new XcmgCoverageOverallDataDO();

        Elements overallElements = reportDoc.select("tfoot").select("tr").select("td");
        try {
            //解析指令覆盖数据，并赋值给数据库对象
            String instructionsStr = overallElements.get(1).text();
            String[] instructionsData = instructionsStr.split(" of ");
            xcmgCoverageOverallDataDO.setMissedInstructions(Integer.parseInt(instructionsData[0].replaceAll(",", "")));
            xcmgCoverageOverallDataDO.setAllInstructions(Integer.parseInt(instructionsData[1].replaceAll(",", "")));
            //解析分支覆盖数据，并赋值给数据库对象
            String branchStr = overallElements.get(3).text();
            String[] branchData = branchStr.split(" of ");
            xcmgCoverageOverallDataDO.setMissedBranches(Integer.parseInt(branchData[0].replaceAll(",", "")));
            xcmgCoverageOverallDataDO.setAllBranches(Integer.parseInt(branchData[1].replaceAll(",", "")));
            //解析圈复杂度数据，并赋值给数据库对象
            Integer missedCxty = Integer.parseInt(overallElements.get(5).text().replaceAll(",", ""));
            Integer allCxty = Integer.parseInt(overallElements.get(6).text().replaceAll(",", ""));
            xcmgCoverageOverallDataDO.setMissedCxty(missedCxty);
            xcmgCoverageOverallDataDO.setAllCxty(allCxty);
            //解析行覆盖数据，并赋值给数据库对象
            Integer missedLines = Integer.parseInt(overallElements.get(7).text().replaceAll(",", ""));
            Integer allLines = Integer.parseInt(overallElements.get(8).text().replaceAll(",", ""));
            xcmgCoverageOverallDataDO.setMissedLines(missedLines);
            xcmgCoverageOverallDataDO.setAllLines(allLines);
            //解析方法覆盖数据，并赋值给数据库对象
            Integer missedMethods = Integer.parseInt(overallElements.get(9).text().replaceAll(",", ""));
            Integer allMethods = Integer.parseInt(overallElements.get(10).text().replaceAll(",", ""));
            xcmgCoverageOverallDataDO.setMissedMethods(missedMethods);
            xcmgCoverageOverallDataDO.setAllMethods(allMethods);
            //解析类覆盖数据，并赋值给数据库对象
            Integer missedClasses = Integer.parseInt(overallElements.get(11).text().replaceAll(",", ""));
            Integer allClasses = Integer.parseInt(overallElements.get(12).text().replaceAll(",", ""));
            xcmgCoverageOverallDataDO.setMissedClasses(missedClasses);
            xcmgCoverageOverallDataDO.setAllClasses(allClasses);
            //关联到报告
            xcmgCoverageOverallDataDO.setTaskId(reportId);
            xcmgCoverageOverallDataDO.setId(String.valueOf(UUID.randomUUID()));
            xcmgCoverageOverallDataService.saveOrUpdate(xcmgCoverageOverallDataDO);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    public Boolean parseMergeReportData(String projectId, String reportPath) throws IOException {
        File reportFile = new File(reportPath + "/index.html");
        Document reportDoc = Jsoup.parse(reportFile, "utf-8");
        XcmgCoverageOverallDataDO xcmgCoverageOverallDataDO = new XcmgCoverageOverallDataDO();

        Elements overallElements = reportDoc.select("tfoot").select("tr").select("td");
        try {
            //解析指令覆盖数据，并赋值给数据库对象
            String instructionsStr = overallElements.get(1).text();
            String[] instructionsData = instructionsStr.split(" of ");
            xcmgCoverageOverallDataDO.setMissedInstructions(Integer.parseInt(instructionsData[0].replaceAll(",", "")));
            xcmgCoverageOverallDataDO.setAllInstructions(Integer.parseInt(instructionsData[1].replaceAll(",", "")));
            //解析分支覆盖数据，并赋值给数据库对象
            String branchStr = overallElements.get(3).text();
            String[] branchData = branchStr.split(" of ");
            xcmgCoverageOverallDataDO.setMissedBranches(Integer.parseInt(branchData[0].replaceAll(",", "")));
            xcmgCoverageOverallDataDO.setAllBranches(Integer.parseInt(branchData[1].replaceAll(",", "")));
            //解析圈复杂度数据，并赋值给数据库对象
            Integer missedCxty = Integer.parseInt(overallElements.get(5).text().replaceAll(",", ""));
            Integer allCxty = Integer.parseInt(overallElements.get(6).text().replaceAll(",", ""));
            xcmgCoverageOverallDataDO.setMissedCxty(missedCxty);
            xcmgCoverageOverallDataDO.setAllCxty(allCxty);
            //解析行覆盖数据，并赋值给数据库对象
            Integer missedLines = Integer.parseInt(overallElements.get(7).text().replaceAll(",", ""));
            Integer allLines = Integer.parseInt(overallElements.get(8).text().replaceAll(",", ""));
            xcmgCoverageOverallDataDO.setMissedLines(missedLines);
            xcmgCoverageOverallDataDO.setAllLines(allLines);
            //解析方法覆盖数据，并赋值给数据库对象
            Integer missedMethods = Integer.parseInt(overallElements.get(9).text().replaceAll(",", ""));
            Integer allMethods = Integer.parseInt(overallElements.get(10).text().replaceAll(",", ""));
            xcmgCoverageOverallDataDO.setMissedMethods(missedMethods);
            xcmgCoverageOverallDataDO.setAllMethods(allMethods);
            //解析类覆盖数据，并赋值给数据库对象
            Integer missedClasses = Integer.parseInt(overallElements.get(11).text().replaceAll(",", ""));
            Integer allClasses = Integer.parseInt(overallElements.get(12).text().replaceAll(",", ""));
            xcmgCoverageOverallDataDO.setMissedClasses(missedClasses);
            xcmgCoverageOverallDataDO.setAllClasses(allClasses);
            //关联到报告
            xcmgCoverageOverallDataDO.setProjectId(projectId);
            QueryWrapper<XcmgCoverageOverallDataDO> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(!projectId.isEmpty(), XcmgCoverageOverallDataDO::getProjectId, projectId);
            XcmgCoverageOverallDataDO dataExist = xcmgCoverageOverallDataService.getOne(wrapper);
            if (!Objects.isNull(dataExist)) {
                xcmgCoverageOverallDataDO.setId(dataExist.getId());
            }else {
                xcmgCoverageOverallDataDO.setId(String.valueOf(UUID.randomUUID()));
            }
            xcmgCoverageOverallDataService.saveOrUpdate(xcmgCoverageOverallDataDO);
        } catch (Exception e) {
            return false;
        }
        return true;
    }



//    public static void main(String[] args) throws IOException {
//
//        String taskId = "60e7a84c-cb47-4ad6-b8f5-7f9543425bd2";
//        String reportPath = HOME_PATH + "/jacoco/" + taskId + "/" + REPORT_PATH;
//        System.out.println(jacocoTestService.parseReportData(taskId, reportPath));
//    }

    private IReportVisitor createHTMLReport(File reportDir) throws IOException {
        HTMLFormatter htmlFormatter = new HTMLFormatter();
        FileMultiReportOutput output = new FileMultiReportOutput(reportDir);
        return htmlFormatter.createVisitor(output);
    }


}
