//package com.xcmg.jacocoservice.controller;
//
//
//import com.xcmg.jacocoservice.jacococlient.ExecutionDataClient;
//import com.xcmg.jacocoservice.jacococlient.MergeDump;
//import com.xcmg.jacocoservice.jacococlient.ReportGenerator;
//import com.test.testmanagement.model.ProjectInfo;
//import com.test.testmanagement.model.TestProject;
//import com.test.testmanagement.service.*;
//import com.xcmg.jacocoservice.jacococlient.DownloadRecursiveFolderFromSFTP;
//import org.apache.tomcat.util.http.fileupload.FileUtils;
//import org.jacoco.core.tools.ExecDumpClient;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.*;
//
//@RestController
//@RequestMapping(value = "/api/jacoco")
//public class JacocoController {
//    private static String LOCAL_DATA_STORE = "/opt/app/mskyprocess/file/jacocoCode/"; // 源码路径
//    private static String LOCAL_REPORT_DIR =  "/opt/app/mskyprocess/file/jacocoReport/"; // 报告路径
//
//    //jenkins机器
//    static String SFTPHOST = "10.5.144.210"; // SFTP Host Name or SFTP Host IP Address
//    static String SFTPPORT = "22"; // SFTP Port Number
//    static String SFTPUSER = "root"; // User Name
//    static String SFTPPASS = "ume@20150916"; // Password
//
//    /*Spring-Mybatis*/
//    @Autowired
//    private TestProjectService service;
//    @Autowired
//    private ProjectInfoService projectInfoService;
//
//    static String testname = "2019-07-29";
//
//    static Element element_project_name = null;
//    static String pom_project_name = "";
//    static Document root_doc = null;
//
//    /**
//     * 清空tomcat服务器的覆盖率文件
//     * @param JACOCO_SERVER_IP tomcat服务器ip
//     * @param JACOCO_SERVER_PORT 8080 or 8044
//     * @return
//     * http://127.0.0.1:8180/api/jacoco/cleantcpserver?ip=10.237.78.108&port=8080
//     */
//    @RequestMapping(value = "/cleantcpserver", method = RequestMethod.GET)
//    public boolean CleanTcpServer(@RequestParam(value = "ip", required = false) String JACOCO_SERVER_IP,
//                                  @RequestParam(value = "port") String JACOCO_SERVER_PORT,
//                                  @RequestParam(value = "projectname") String PROJECT_NAME
//    ) {
//        boolean result = true;
//
//        Map<String, Object> requestParamMap = new HashMap<String, Object>();
//        requestParamMap.put("projectname", PROJECT_NAME);
//        // 取tcpserver的ip 数据库表：projectinfo
//        List<ProjectInfo> list = projectInfoService.findJacocoserverIpbyProjectname(requestParamMap);
////        JACOCO_SERVER_IP = list.get(0).getJacocoserverip();
//        String[] ipInfo = list.get(0).getJacocoserverip().split(",");
//
//        ExecDumpClient client = new ExecDumpClient();
//        client.setReset(true);
//        client.setDump(false);
//        //目标机器的ip和端口，对应着运行程序时javaagent参数里的ip和端口
//        try {
//            for (String ipinfo : ipInfo) {
//                JACOCO_SERVER_IP = ipinfo.split(":")[0];
//                JACOCO_SERVER_PORT = ipinfo.split(":")[1];
//                client.dump(JACOCO_SERVER_IP, Integer.parseInt(JACOCO_SERVER_PORT));
//            }
//        } catch (IOException e) {
//            result = false;
//            e.printStackTrace();
//        }
//        System.out.println("清除就的覆盖率文件");
//        return result;
//    }
//
//    /**
//     * 获取单个工程
//     * 覆盖率、覆盖率报告、解析html存到DB
//     * @param JACOCO_SERVER_IP tomcat服务器ip
//     * @param JACOCO_SERVER_PORT 8080 or 8044
//     * @param PROJECT_NAME 工程名 umemid-airport
//     * @return
//     * http://127.0.0.1:8180/api/jacoco/jacocoReport?ip=10.237.78.108&port=8080&projectname=umemid-airport
//     */
//    @RequestMapping(value = "/jacocoReport", method = RequestMethod.GET)
//    public boolean JacocoReport(@RequestParam(value = "ip", required = false) String JACOCO_SERVER_IP,
//                                @RequestParam(value = "port") String JACOCO_SERVER_PORT,
//                                @RequestParam(value = "projectname") String PROJECT_NAME) {
//        boolean result = true;
//        boolean hasParenetPom = true;
//
//        Map<String, Object> requestParamMap = new HashMap<String, Object>();
//        requestParamMap.put("projectname", PROJECT_NAME);
//        // 取tcpserver的ip 数据库表：projectinfo
//        List<ProjectInfo> list = projectInfoService.findJacocoserverIpbyProjectname(requestParamMap);
//        String[] ipInfo = list.get(0).getJacocoserverip().split(",");
//        List<String> JACOCO_SERVER_IP_LIST = new ArrayList<>();
//        for (String ip : ipInfo) {
//            JACOCO_SERVER_IP_LIST.add(ip);
//        }
//
//        // 判断工程子pom.xml
//        if (PROJECT_NAME.contains("umemid-") ||PROJECT_NAME.contains("umepsr-") ) {
//            System.out.println(PROJECT_NAME +"有子pom");
//        }
//        else {
//            hasParenetPom = false;
//        }
//        try {
//            long for_startTime = System.currentTimeMillis();
//
//            //Step1:创建本地src以及target目录
//            init_Project_Src_Classes(PROJECT_NAME);
//            long for_startTime2 = System.currentTimeMillis();
//            System.out.println("创建本地目录时间：" + (for_startTime2 - for_startTime));
//
//            //Step2:sftp下载相关src及target目录下的文件
//            download_Src_Classes(SFTPHOST,SFTPPORT,SFTPUSER,SFTPPASS,hasParenetPom,PROJECT_NAME);
//            long for_startTime3 = System.currentTimeMillis();
//            System.out.println("下载src和claess文件时间：" + (for_startTime3 - for_startTime2));
//
//            //Step3 socket_client后去集成测试exec文件  ExecutionDataClient.getJacocoExec(JACOCO_SERVER_IP,JACOCO_SERVER_PORT,LOCAL_DATA_STORE+PROJECT_NAME+"/jacoco-it.exec");
//            //2020-04-08 改成多ip支持获取覆盖率，参数为ip+端口
//            String pathdir = ExecutionDataClient.downLoadDump(JACOCO_SERVER_IP_LIST, LOCAL_DATA_STORE + PROJECT_NAME);
//            // 把各个机器拿得exec文件，合并成jacoco.exec
//            MergeDump.executeMerge(LOCAL_DATA_STORE + PROJECT_NAME);
//
//            long for_startTime4 = System.currentTimeMillis();
//            //Step4 生成集成测试覆盖率报告
//            ReportGenerator.generateItCoverageReport(
//                    LOCAL_DATA_STORE+PROJECT_NAME,
//                    LOCAL_REPORT_DIR+PROJECT_NAME,
//                    "jacoco.exec",
//                    "target",
//                    "src/main/java",
//                    "it-coveragereport");
//            long for_startTime5 = System.currentTimeMillis();
//            System.out.println("生成覆盖率报告时间：" + (for_startTime5 - for_startTime4));
//
//            //Step5 存取html解析到db
//            JacocoReportToDB(LOCAL_REPORT_DIR + PROJECT_NAME + "/it-coveragereport/");
//            long for_endTime = System.currentTimeMillis();
//            System.out.println("解析html的时间：" + (for_endTime - for_startTime5));
//
//            long consumetime = for_endTime - for_startTime;
//            System.out.println("覆盖率获取时间：" + consumetime);
//        } catch (Exception e) {
//            result = false;
//            e.printStackTrace();
//        }
//        return result;
//    }
//
//    /**
//     * 初始化删除创建工程的src、classes存储路径
//     * @param projectname
//     */
//    public static void init_Project_Src_Classes(String projectname){
//        try {
//            //如果文件夹不存在则创建
//            File project_file =new File(LOCAL_DATA_STORE + projectname);
//            File project_file_src = new File(LOCAL_DATA_STORE + projectname+"/src");
//            File project_file_classes = new File(LOCAL_DATA_STORE + projectname+"/target/classes");
//            File project_reportfile = new File(LOCAL_REPORT_DIR + projectname);
//
//            if  (!project_file.exists()  && !project_file.isDirectory())
//            {
//                System.out.println(project_file+"不存在");
//                System.out.println("创建多级目录"+project_file_src+";"+project_file_classes);
//                project_file_src.mkdirs();
//                project_file_classes.mkdirs();
//            }else
//            {
//                //删除文件夹
//                System.out.println("删除文件夹"+project_file);
//                FileUtils.deleteDirectory(project_file);
//                System.out.println("创建多级目录"+project_file_src+";"+project_file_classes);
//                project_file_src.mkdirs();
//                project_file_classes.mkdirs();
//            }
//
//            if (!project_reportfile.exists() && !project_reportfile.isDirectory()) {
//                System.out.println(project_reportfile + "不存在");
//                project_reportfile.mkdirs();
//            }else {
//                FileUtils.deleteDirectory(project_reportfile);            }
////        //创建文件夹
////        FileUtils.
//        }catch (Exception e){
//
//        }finally {
//
//        }
//
//    }
//
//    /**
//     * 从打包Jenkins通过SFTF 获取class文件 & 源码
//     * @param ip jenkins地址
//     * @param port 22
//     * @param username 登录用户 root or jboss5
//     * @param password 密码
//     * @param hasParenetPom 是否有子工程 true：umemid-airport  false：没有
//     * @param PROJECT_NAME 工程名
//     */
//    public static void download_Src_Classes(String ip,String port,String username,String password,boolean hasParenetPom,String PROJECT_NAME){
//        /*
//        /opt/app/jenkins/workspace/Pipeline_umemid-airport/umemid-airport/src
//         /opt/app/jenkins/workspace/Pipeline_umemid-airport/umemid-airport/target/classes
//         */
//        String srcDir ="";
//        if (hasParenetPom){
//            srcDir = "/opt/app/jenkins/workspace/PipelineCI_"+PROJECT_NAME+"/"+PROJECT_NAME+"/src";
//            System.out.println("有子pom");
//        }else{
//            srcDir = "/opt/app/jenkins/workspace/PipelineCI_"+PROJECT_NAME+"/"+"src";
//        }
//        String local_srcDir = LOCAL_DATA_STORE+PROJECT_NAME+"/src";
//
//
//        String classesDir = "";
//        if(hasParenetPom){
//            classesDir = "/opt/app/jenkins/workspace/PipelineCI_"+PROJECT_NAME+"/"+PROJECT_NAME+"/target/classes";
//        }else {
//            classesDir = "/opt/app/jenkins/workspace/PipelineCI_"+PROJECT_NAME+"/"+"target/classes";
//        }
//        String local_classesDir = LOCAL_DATA_STORE+PROJECT_NAME+"/target/classes";
//
//
////        String unittest_jacocoexec = "/opt/app/jenkins/workspace/Pipeline_"+PROJECT_NAME+"/"+PROJECT_NAME+"/target/jacoco.exec";
////        String local_jacoco_utexec = LOCAL_DATA_STORE+PROJECT_NAME+"/target/jacoco-ut.exec";
//
//
//
//        DownloadRecursiveFolderFromSFTP.downloadDir(ip,port,username,password,srcDir,local_srcDir);
//        DownloadRecursiveFolderFromSFTP.downloadDir(ip,port,username,password,classesDir,local_classesDir);
////        DownloadRecursiveFolderFromSFTP.downloadDir(ip,port,username,password,unittest_jacocoexec,local_jacoco_utexec);
//
//    }
//
//    /**
//     * 存取html解析到db
//     * @param htmlPath index.html路径
//     * @throws Exception
//     * http://127.0.0.1:8180/api/jacoco/jacocoParseHtmltoDB?htmlPath=/data/Mock/testmanagement/file/umemid-airport/it-coveragereport/
//     */
//    public void JacocoReportToDB(String htmlPath) throws Exception {
//        File root_input = new File(htmlPath + "index.html");
//
//
//        /*开始读取Jacoco覆盖率并存储到数据库*/
//        TestProject testProject = new TestProject();
//        testProject.setTestname(testname);
//        Date d = new Date();
//        testProject.setStarttime(d);//获取starttime
//
//
//        root_doc = Jsoup.parse(root_input, "UTF-8");
//        element_project_name = root_doc.select("H1").first();//查找第一个H1元素
//        pom_project_name = element_project_name.text();
//        testProject.setProjectname(pom_project_name);
//
//        Integer project_id = service.insertTestProject(testProject);//插入项目
//
//        //1)project_name
//        //UmeTest
//        System.out.println(pom_project_name);
//
//        //2)包名
//            /*
//            com.umetrip.unittest.plugins.dbReporter
//            com.umetrip.unittest
//            com.umetrip.unittest.testcase
//            com.umetrip.unittest.plugins.htmlReporter
//             */
//        Elements elements_index_package = root_doc.getElementsByClass("el_package");
//        Integer i = 0;
//
//        Element tfoot = root_doc.getElementsByTag("tfoot").first();
////        Element tr = tfoot.getElementsByTag("tr").first();
//        Elements tds = tfoot.getElementsByTag("td");
//
//        StringBuffer total_coverage = new StringBuffer();
//        total_coverage.append(pom_project_name + "#");
//        for (Element td : tds) {
//            if (td.text().contains("Total")) {
//                continue;
//            }
//            //System.out.println(td.text());
//
//            total_coverage.append(td.text());
//            total_coverage.append("#");
//        }
//        System.out.println("================" + pom_project_name + "项目整体覆盖");
//        System.out.println(total_coverage);
//
//        TestProject someProjectTotalSummary = new TestProject();
//
//        someProjectTotalSummary.setId(project_id);
//
//        someProjectTotalSummary.setMissedinstructions(total_coverage.toString().split("#")[1]);
//        someProjectTotalSummary.setInstructioncoverage(total_coverage.toString().split("#")[2]);
//
//        someProjectTotalSummary.setMissedbranches(total_coverage.toString().split("#")[3]);
//        someProjectTotalSummary.setBranchcoverage(total_coverage.toString().split("#")[4]);
//
//        someProjectTotalSummary.setMissedcxty(total_coverage.toString().split("#")[5]);
//        someProjectTotalSummary.setTotalcxty(total_coverage.toString().split("#")[6]);
//
//        someProjectTotalSummary.setMissedlines(total_coverage.toString().split("#")[7]);
//        someProjectTotalSummary.setTotallines(total_coverage.toString().split("#")[8]);
//
//        someProjectTotalSummary.setMissedmethods(total_coverage.toString().split("#")[9]);
//        someProjectTotalSummary.setTotalmethods(total_coverage.toString().split("#")[10]);
//
//        someProjectTotalSummary.setMissedclasses(total_coverage.toString().split("#")[11]);
//        someProjectTotalSummary.setTotalclasses(total_coverage.toString().split("#")[12]);
//
//        service.updateTestProjectById(someProjectTotalSummary);
//    }
//}
//
