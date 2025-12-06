package com.softdev.system.generator.entity.vo;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一返回结果VO
 *
 * @author zhengkai.blog.csdn.net
 */
@Data
public class ResultVo extends HashMap<String, Object> {

    public ResultVo() {
        put("code", 200);
        put("msg", "success");
    }

    public static ResultVo ok() {
        return new ResultVo();
    }

    public static ResultVo ok(Object data) {
        ResultVo resultVo = new ResultVo();
        resultVo.put("data", data);
        return resultVo;
    }

    public static ResultVo error(String msg) {
        ResultVo resultVo = new ResultVo();
        resultVo.put("code", 500);
        resultVo.put("msg", msg);
        return resultVo;
    }

    public static ResultVo error(int code, String msg) {
        ResultVo resultVo = new ResultVo();
        resultVo.put("code", code);
        resultVo.put("msg", msg);
        return resultVo;
    }

    @Override
    public ResultVo put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}