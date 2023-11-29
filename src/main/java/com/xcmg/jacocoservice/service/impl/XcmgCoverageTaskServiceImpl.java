package com.xcmg.jacocoservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xcmg.jacocoservice.base.ResponseBean;
import com.xcmg.jacocoservice.mapper.XcmgCoverageOverallDataMapper;
import com.xcmg.jacocoservice.model.XcmgCodeProjectDO;
import com.xcmg.jacocoservice.model.XcmgCoverageTaskDO;
import com.xcmg.jacocoservice.mapper.XcmgCoverageTaskMapper;
import com.xcmg.jacocoservice.service.JacocoTestService;
import com.xcmg.jacocoservice.service.XcmgCoverageTaskService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.xcmg.jacocoservice.constants.DumpConstants.*;

/**
 * <p>
 * 覆盖率收集任务表 服务实现类
 * </p>
 *
 * @author guojian
 * @since 2023-09-21
 */
@Service
public class XcmgCoverageTaskServiceImpl extends ServiceImpl<XcmgCoverageTaskMapper, XcmgCoverageTaskDO> implements XcmgCoverageTaskService {
    @Autowired
    private Environment environment;
    @Autowired
    JacocoTestService jacocoTestService;
    @Autowired
    XcmgCoverageOverallDataMapper xcmgCoverageOverallDataMapper;

    @Override
    public ResponseBean startdump(XcmgCodeProjectDO xcmgCodeProjectDO) throws IOException {
        String startTime = getNowDate();
        String newTaskId = String.valueOf(UUID.randomUUID());

        String taskFilePath = HOME_PATH + "/jacoco/" + newTaskId + "/";
        jacocoTestService.dump(xcmgCodeProjectDO, taskFilePath + EXEC_FILE_PATH);
        jacocoTestService.reportGenerator(xcmgCodeProjectDO, taskFilePath + EXEC_FILE_PATH, taskFilePath + REPORT_PATH);
        String HOST_IP = String.valueOf(InetAddress.getLocalHost().getHostAddress());
        //获取ip
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            // 获取该网络接口上的所有IP地址
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();

                // 过滤出IPv4地址并排除回环地址
                if (!inetAddress.isLoopbackAddress() && inetAddress.isSiteLocalAddress() && inetAddress.getHostAddress().indexOf(":") == -1 && !inetAddress.getHostName().contains("docker")) {
                    HOST_IP = inetAddress.getHostAddress();
                }
            }
        }
        //报告网络路径，及报告信息保存到数据库
        String reportUrl = "http://" + HOST_IP + ":" + environment.getProperty("server.port") + "/report/" + newTaskId + "/report/index.html";
        XcmgCoverageTaskDO xcmgCoverageTaskDO = new XcmgCoverageTaskDO();
        xcmgCoverageTaskDO.setId(newTaskId);
        xcmgCoverageTaskDO.setProjectId(xcmgCodeProjectDO.getId());
        xcmgCoverageTaskDO.setTaskStatus("已完成");
        xcmgCoverageTaskDO.setReportUrl(reportUrl);
        xcmgCoverageTaskDO.setStartTime(startTime);
        xcmgCoverageTaskDO.setFinishTime(getNowDate());
        saveOrUpdate(xcmgCoverageTaskDO);
        //解析报告中的数据存到数据库
        jacocoTestService.parseReportData(newTaskId, taskFilePath + REPORT_PATH);
        return ResponseBean.success(xcmgCoverageTaskDO);
    }


    @Override
    public List<XcmgCoverageTaskDO> gettaskByProject(String projectId) {
        QueryWrapper<XcmgCoverageTaskDO> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(!projectId.isEmpty(), XcmgCoverageTaskDO::getProjectId, projectId);
        wrapper.select().orderByAsc("finish_time");
        List<XcmgCoverageTaskDO> result = list(wrapper);
        result.forEach(xcmgCoverageTaskDO -> {
            xcmgCoverageTaskDO.setCoverData(xcmgCoverageOverallDataMapper.selectByTaskId(xcmgCoverageTaskDO.getId()));
        });
        return result;
    }

    public String getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        String dateString = formatter.format(currentTime);
        return dateString;
    }

}
