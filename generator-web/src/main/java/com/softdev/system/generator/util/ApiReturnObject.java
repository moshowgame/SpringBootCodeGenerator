package com.softdev.system.generator.util;

import lombok.Data;

import java.io.Serializable;
@Data
public class ApiReturnObject implements Serializable{

	private static final long serialVersionUID = 1L;

	public ApiReturnObject(String errorCode, Object errorMessage, Object returnObject) {
		super();
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.returnObject = returnObject;
	}
	public ApiReturnObject(Object errorMessage, Object returnObject) {
		super();
		this.errorMessage = errorMessage;
		this.returnObject = returnObject;
	}

	String errorCode="00";
	Object errorMessage;
	Object returnObject;
	String pageNumber;
	String pageSize;
	String totalElements;
	String totalPages;

	public ApiReturnObject(String pageNumber,String pageSize,String totalElements,String totalPages,String errorCode, Object errorMessage, Object returnObject) {
		super();
		this.pageNumber = pageNumber;
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.returnObject = returnObject;
		this.pageSize = pageSize;
		this.totalElements = totalElements;
		this.totalPages = totalPages;
	}

}
