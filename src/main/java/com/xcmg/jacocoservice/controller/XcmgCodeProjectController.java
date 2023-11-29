package com.xcmg.jacocoservice.controller;


import com.xcmg.jacocoservice.base.ResponseBean;
import com.xcmg.jacocoservice.model.XcmgCodeProjectDO;
import com.xcmg.jacocoservice.service.XcmgCodeProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 项目表-微服务维度 前端控制器
 * </p>
 *
 * @author guojian
 * @since 2023-09-21
 */
@RestController
@RequestMapping("/jacoco")
public class XcmgCodeProjectController {
    @Autowired
    XcmgCodeProjectService xcmgCodeProjectService;
    @GetMapping("/project/list")
    public ResponseBean getlist(){
        return ResponseBean.success(xcmgCodeProjectService.list());
    }
}

