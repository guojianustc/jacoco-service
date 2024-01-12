package com.xcmg.jacocoservice.service;

import com.xcmg.jacocoservice.model.XcmgCodeProjectDO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 项目表-微服务维度 服务类
 * </p>
 *
 * @author guojian
 * @since 2023-09-21
 */
public interface XcmgCodeProjectService extends IService<XcmgCodeProjectDO> {
    List<XcmgCodeProjectDO> getAllProjects();
}
