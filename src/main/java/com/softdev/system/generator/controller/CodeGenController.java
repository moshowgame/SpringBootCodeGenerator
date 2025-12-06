package com.softdev.system.generator.controller;

import com.softdev.system.generator.entity.dto.ParamInfo;
import com.softdev.system.generator.entity.vo.ResultVo;
import com.softdev.system.generator.service.CodeGenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * 代码生成控制器
 *
 * @author zhengkai.blog.csdn.net
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/code")
public class CodeGenController {
    
    private final CodeGenService codeGenService;

    @PostMapping("/generate")
    public ResultVo generateCode(@RequestBody ParamInfo paramInfo) throws Exception {
        return codeGenService.generateCode(paramInfo);
    }

}