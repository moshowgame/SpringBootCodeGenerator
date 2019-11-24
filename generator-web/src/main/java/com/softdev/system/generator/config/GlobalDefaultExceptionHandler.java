package com.softdev.system.generator.config;

import javax.servlet.http.HttpServletRequest;

import com.softdev.system.generator.entity.ReturnT;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalDefaultExceptionHandler {
	
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public ReturnT defaultExceptionHandler(HttpServletRequest req,Exception e) {
		e.printStackTrace();
		return new ReturnT<>(ReturnT.FAIL_CODE, e.getMessage());
	}
	
}
