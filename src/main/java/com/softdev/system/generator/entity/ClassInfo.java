package com.softdev.system.generator.entity;

import lombok.Data;

import java.util.List;

/**
 * class info
 *
 * @author xuxueli 2018-05-02 20:02:34
 */
@Data
public class ClassInfo {

    private String tableName;
    private String originTableName;
    private String className;
    private String classComment;
    private List<FieldInfo> fieldList;

}
