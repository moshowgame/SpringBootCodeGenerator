package com.softdev.system.generator.config;

import com.softdev.system.generator.entity.vo.ResultVo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author zhengkai.blog.csdn.net
 */
@ControllerAdvice
public class GlobalDefaultExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResultVo defaultExceptionHandler(HttpServletRequest req, Exception e) {
        e.printStackTrace();
        return ResultVo.error("代码生成失败:"+e.getMessage());
    }

}
