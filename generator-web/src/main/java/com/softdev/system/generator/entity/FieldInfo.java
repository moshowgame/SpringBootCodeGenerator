package com.softdev.system.generator.entity;

import lombok.Data;

/**
 * field info
 *
 * @author xuxueli 2018-05-02 20:11:05
 */
@Data
public class FieldInfo {

    private String columnName;
    private String fieldName;
    private String fieldClass;
    private String swaggerClass;
    private String fieldComment;

}
