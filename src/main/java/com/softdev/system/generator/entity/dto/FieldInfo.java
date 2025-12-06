package com.softdev.system.generator.entity.dto;

import lombok.Data;

/**
 * 字段信息
 *
 * @author zhengkai.blog.csdn.net
 */
@Data
public class FieldInfo {

    private String columnName;
    private String fieldName;
    private String fieldClass;
    private String swaggerClass;
    private String fieldComment;

}