package com.softdev.system.generator.entity;

import lombok.Data;

import java.util.Map;

/**
 * Post data - ParamInfo
 *
 * @author zhengkai.blog.csdn.net
 */
@Data
public class ParamInfo {

    private String tableSql;
    private Map<String,Object> options;

    @Data
    public static class NAME_CASE_TYPE {
        public static final String CAMEL_CASE = "CamelCase";
        public static final String UNDER_SCORE_CASE = "UnderScoreCase";
        public static final String UPPER_UNDER_SCORE_CASE = "UpperUnderScoreCase";
    }

}
