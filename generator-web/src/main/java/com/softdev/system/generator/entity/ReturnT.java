package com.softdev.system.generator.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * common returnT:公共返回封装类
 *
 * @author zhengkai.blog.csdn.net
 */
@Data
public class ReturnT implements Serializable {

    public static final long serialVersionUID = 42L;

    public static final int SUCCESS_CODE = 200;
    public static final int FAIL_CODE = 500;
    public static final int PAGE_CODE = 0;
    public static final String OBJECT_NOT_FOUND = "找不到该对象";
    public static final String OPERATION_SUCCESS = "操作成功";

    private int code;
    private String msg;
    private Object data;
    private int count;

    public ReturnT(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ReturnT(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ReturnT(Object data) {
        this.code = SUCCESS_CODE;
        this.data = data;
    }

    public ReturnT(Object data, int count) {
        this.code = PAGE_CODE;
        this.data = data;
        this.count = count;
    }

    public static ReturnT PAGE(Object data, int count) {
        return new ReturnT(data, count);
    }

    public static ReturnT PAGE(Object data, long count) {
        return new ReturnT(data, Integer.parseInt(count + ""));
    }

    public static ReturnT SUCCESS() {
        return new ReturnT(SUCCESS_CODE, OPERATION_SUCCESS);
    }

    public static ReturnT SUCCESS(String msg) {
        return new ReturnT(SUCCESS_CODE, msg);
    }

    public static ReturnT SUCCESS(Object data) {
        return new ReturnT(data);
    }

    public static ReturnT ERROR(String msg) {
        return new ReturnT(FAIL_CODE, msg);
    }

    public static ReturnT ERROR() {
        return new ReturnT(FAIL_CODE, OBJECT_NOT_FOUND);
    }

}
