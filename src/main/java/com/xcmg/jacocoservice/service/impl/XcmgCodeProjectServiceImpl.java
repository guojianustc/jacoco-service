package com.xcmg.jacocoservice.service.impl;

import com.xcmg.jacocoservice.mapper.XcmgCoverageOverallDataMapper;
import com.xcmg.jacocoservice.model.XcmgCodeProjectDO;
import com.xcmg.jacocoservice.mapper.XcmgCodeProjectMapper;
import com.xcmg.jacocoservice.service.XcmgCodeProjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;

/**
 * <p>
 * 项目表-微服务维度 服务实现类
 * </p>
 *
 * @author guojian
 * @since 2023-09-21
 */
@Service
public class XcmgCodeProjectServiceImpl extends ServiceImpl<XcmgCodeProjectMapper, XcmgCodeProjectDO> implements XcmgCodeProjectService {
    @Autowired
    XcmgCoverageOverallDataMapper xcmgCoverageOverallDataMapper;
    @Autowired
    private Environment environment;
    @Override
    public List<XcmgCodeProjectDO> getAllProjects() throws UnknownHostException, SocketException {
        List<XcmgCodeProjectDO> projects = list();
        for (XcmgCodeProjectDO project:projects){
            project.setCoverData(xcmgCoverageOverallDataMapper.selectByProjectId(project.getId()));

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
            String reportUrl = "http://" + HOST_IP + ":" + environment.getProperty("server.port") + "/report/mergeFile/" + project.getId() + "/report/index.html";
            project.setOverAllReport(reportUrl);
        }
        return projects;
    }

}
