package com.xcmg.jacocoservice.model;

import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author guojian
 * @since 2023-10-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("xcmg_coverage_overall_data")
public class XcmgCoverageOverallDataDO implements Serializable {


    private String id;

    @TableField("task_id")
    private String taskId;

    /**
     * 未覆盖的指令数
     */
    @TableField("missed_instructions")
    private Integer missedInstructions;

    /**
     * 指令数
     */
    @TableField("all_instructions")
    private Integer allInstructions;

    /**
     * 未覆盖的分支
     */
    @TableField("missed_branches")
    private Integer missedBranches;

    /**
     * 所有分支
     */
    @TableField("all_branches")
    private Integer allBranches;

    /**
     * 未覆盖的圈复杂度
     */
    @TableField("missed_cxty")
    private Integer missedCxty;

    /**
     * 所有圈复杂度
     */
    @TableField("all_cxty")
    private Integer allCxty;

    /**
     * 未覆盖的方法数
     */
    @TableField("missed_methods")
    private Integer missedMethods;

    /**
     * 所有方法数
     */
    @TableField("all_methods")
    private Integer allMethods;

    /**
     * 未覆盖的代码行数
     */
    @TableField("missed_lines")
    private Integer missedLines;

    /**
     * 所有代码行数
     */
    @TableField("all_lines")
    private Integer allLines;

    /**
     * 未覆盖的类数
     */
    @TableField("missed_classes")
    private Integer missedClasses;

    /**
     * 所有类数
     */
    @TableField("all_classes")
    private Integer allClasses;

    /**
     * 项目id
     */
    @TableField("project_id")
    private String projectId;


}
