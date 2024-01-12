package com.xcmg.jacocoservice.model;

import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 项目表-微服务维度
 * </p>
 *
 * @author guojian
 * @since 2023-09-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("xcmg_code_project")
public class XcmgCodeProjectDO implements Serializable {


    private String id;

    @TableField("project_name")
    private String projectName;

    @TableField("project_git_repository")
    private String projectGitRepository;

    @TableField("classes_path")
    private String classesPath;

    /**
     * 源代码路径
     */
    @TableField("source_path")
    private String sourcePath;

    /**
     * 代码分支
     */
    @TableField("code_branch")
    private String codeBranch;

    /**
     * agent访问ip
     */
    @TableField("server_ip")
    private String serverIp;

    /**
     * agent访问端口
     */
    @TableField("server_port")
    private String serverPort;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

    @TableField(exist = false)
    private XcmgCoverageOverallDataDO coverData;

}
