package com.softdev.system.generator.entity;

import lombok.Data;

/**
 * Post data - ParamInfo
 * @author zhengkai.blog.csdn.net
 */
@Data
public class ParamInfo {
    private String tableSql;
    private String authorName;
    private String packageName;
    private String returnUtil;
    private boolean isUnderLineToCamelCase;
    String tinyintTransType;
    String dataType;
}
