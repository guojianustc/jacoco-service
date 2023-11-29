package com.xcmg.jacocoservice.service;

import com.xcmg.jacocoservice.base.ResponseBean;
import com.xcmg.jacocoservice.model.XcmgCodeProjectDO;
import com.xcmg.jacocoservice.model.XcmgCoverageTaskDO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 覆盖率收集任务表 服务类
 * </p>
 *
 * @author guojian
 * @since 2023-09-21
 */
public interface XcmgCoverageTaskService extends IService<XcmgCoverageTaskDO> {

    public ResponseBean startdump(XcmgCodeProjectDO xcmgCodeProjectDO) throws IOException;
    public List<XcmgCoverageTaskDO> gettaskByProject(String projectId);

}
