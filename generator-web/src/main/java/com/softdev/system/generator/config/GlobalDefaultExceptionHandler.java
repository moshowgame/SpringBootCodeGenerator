package com.softdev.system.generator.config;

import com.softdev.system.generator.entity.ReturnT;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhengkai.blog.csdn.net
 */
@ControllerAdvice
public class GlobalDefaultExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ReturnT defaultExceptionHandler(HttpServletRequest req, Exception e) {
        e.printStackTrace();
        return ReturnT.error("代码生成失败:"+e.getMessage());
    }

}
