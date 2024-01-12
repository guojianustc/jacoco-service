package com.xcmg.jacocoservice.mapper;

import com.xcmg.jacocoservice.model.XcmgCoverageOverallDataDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author guojian
 * @since 2023-10-25
 */
public interface XcmgCoverageOverallDataMapper extends BaseMapper<XcmgCoverageOverallDataDO> {
    @Select("SELECT * FROM xcmg_coverage_overall_data WHERE task_id = #{taskId}")
    XcmgCoverageOverallDataDO selectByTaskId(String taskId);

    @Select("SELECT * FROM xcmg_coverage_overall_data WHERE project_id = #{projectId}")
    XcmgCoverageOverallDataDO selectByProjectId(String projectId);
}
