package com.softdev.system.generator.config;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.softdev.system.generator.util.ApiReturnObject;
import com.softdev.system.generator.util.ApiReturnUtil;

@ControllerAdvice
public class GlobalDefaultExceptionHandler {
	
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public ApiReturnObject defaultExceptionHandler(HttpServletRequest req,Exception e) {
		e.printStackTrace();
		//return new ApiReturnObject("01","server error", e.getMessage());
		return ApiReturnUtil.error("服务器异常",e.getMessage());
	}
	
}
