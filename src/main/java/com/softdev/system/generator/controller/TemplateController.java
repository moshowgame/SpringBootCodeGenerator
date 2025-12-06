package com.softdev.system.generator.controller;

import com.alibaba.fastjson2.JSONArray;
import com.softdev.system.generator.entity.vo.ResultVo;
import com.softdev.system.generator.service.TemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模板管理控制器
 *
 * @author zhengkai.blog.csdn.net
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/template")
public class TemplateController {

    private final TemplateService templateService;

    @PostMapping("/all")
    public ResultVo getAllTemplates() throws Exception {
        return ResultVo.ok(templateService.getAllTemplates());
    }

}