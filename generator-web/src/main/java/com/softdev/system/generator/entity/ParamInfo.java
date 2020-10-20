package com.softdev.system.generator.entity;

import lombok.Data;

/**
 * Post data - ParamInfo
 *
 * @author zhengkai.blog.csdn.net
 */
@Data
public class ParamInfo {

    private String tableSql;
    private String authorName;
    private String packageName;
    private String returnUtil;
    /**命名转换规则*/
    private String nameCaseType;
    /**tinyint转换类型*/
    private String tinyintTransType;
    /**时间转换类型*/
    private String timeTransType;
    /**数据类型：ddl-sql json*/
    private String dataType;
    /**是否启用swagger*/
    private boolean swagger;
    /**是否启用包装类型*/
    private boolean packageType;

    @Data
    public static class NAME_CASE_TYPE {
        public static String CAMEL_CASE = "CamelCase";
        public static String UNDER_SCORE_CASE = "UnderScoreCase";
        public static String UPPER_UNDER_SCORE_CASE = "UpperUnderScoreCase";
    }

}
