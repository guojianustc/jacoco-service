package com.xcmg.jacocoservice.service.impl;

import com.xcmg.jacocoservice.mapper.XcmgCoverageOverallDataMapper;
import com.xcmg.jacocoservice.model.XcmgCodeProjectDO;
import com.xcmg.jacocoservice.mapper.XcmgCodeProjectMapper;
import com.xcmg.jacocoservice.service.XcmgCodeProjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    @Override
    public List<XcmgCodeProjectDO> getAllProjects(){
        List<XcmgCodeProjectDO> projects = list();
        for (XcmgCodeProjectDO project:projects){
            project.setCoverData(xcmgCoverageOverallDataMapper.selectByProjectId(project.getId()));
        }
        return projects;
    }

}
