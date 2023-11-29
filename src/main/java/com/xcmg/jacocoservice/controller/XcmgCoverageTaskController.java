package com.xcmg.jacocoservice.controller;


import com.xcmg.jacocoservice.base.ResponseBean;
import com.xcmg.jacocoservice.model.XcmgCodeProjectDO;
import com.xcmg.jacocoservice.model.XcmgCoverageTaskDO;
import com.xcmg.jacocoservice.service.JacocoTestService;
import com.xcmg.jacocoservice.service.XcmgCodeProjectService;
import com.xcmg.jacocoservice.service.XcmgCoverageTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

import static com.xcmg.jacocoservice.constants.DumpConstants.HOME_PATH;
import static com.xcmg.jacocoservice.constants.DumpConstants.REPORT_PATH;

/**
 * <p>
 * 覆盖率收集任务表 前端控制器
 * </p>
 *
 * @author guojian
 * @since 2023-09-21
 */
@RestController
@RequestMapping("/jacoco")
public class XcmgCoverageTaskController {
    @Autowired
    XcmgCoverageTaskService coverageTaskService;
    @Autowired
    JacocoTestService JasocoTestService;
    @PostMapping("/startdump")
    public ResponseBean startdump(@RequestBody XcmgCodeProjectDO xcmgCodeProjectDO) throws IOException {
        return coverageTaskService.startdump(xcmgCodeProjectDO);
    }

    @GetMapping("/task/list")
    public ResponseBean gettaskByProject(@RequestParam String projectId){
        return ResponseBean.success(coverageTaskService.gettaskByProject(projectId));
    }
    @GetMapping("/task/savedata")
    public ResponseBean savedata() throws IOException {
        String taskId = "60e7a84c-cb47-4ad6-b8f5-7f9543425bd2";
        String reportPath = HOME_PATH + "/jacoco/" + taskId + "/" + REPORT_PATH;
        JasocoTestService.parseReportData(taskId,reportPath);
        return ResponseBean.success(coverageTaskService.gettaskByProject(taskId));
    }
}

