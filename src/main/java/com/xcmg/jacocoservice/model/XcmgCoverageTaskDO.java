package com.xcmg.jacocoservice.model;

import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.mapping.FetchType;

/**
 * <p>
 * 覆盖率收集任务表
 * </p>
 *
 * @author guojian
 * @since 2023-09-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("xcmg_coverage_task")
public class XcmgCoverageTaskDO implements Serializable {


    private String id;
    @TableField("project_id")
    private String projectId;

    @TableField("task_status")
    private String taskStatus;

    @TableField("report_url")
    private String reportUrl;

    @TableField("start_time")
    private String startTime;

    @TableField("finish_time")
    private String finishTime;

    @TableField(exist = false)
    private XcmgCoverageOverallDataDO coverData;

}
