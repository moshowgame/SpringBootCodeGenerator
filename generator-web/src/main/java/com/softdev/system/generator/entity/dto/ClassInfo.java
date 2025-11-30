package com.softdev.system.generator.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * 类信息
 *
 * @author zhengkai.blog.csdn.net
 */
@Data
public class ClassInfo {

    private String tableName;
    private String originTableName;
    private String className;
    private String classComment;
    private List<FieldInfo> fieldList;

}