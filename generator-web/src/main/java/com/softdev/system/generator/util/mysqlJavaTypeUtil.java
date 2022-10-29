package com.softdev.system.generator.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lvyanpu
 */
public final class mysqlJavaTypeUtil {
    public static final  HashMap<String, String> mysqlJavaTypeMap = new HashMap<String, String>();
    public static final  HashMap<String, String> mysqlSwaggerTypeMap =new HashMap<String, String>();

    static{
        mysqlJavaTypeMap.put("bigint","Long");
        mysqlJavaTypeMap.put("int","Integer");
        mysqlJavaTypeMap.put("tinyint","Integer");
        mysqlJavaTypeMap.put("smallint","Integer");
        mysqlJavaTypeMap.put("mediumint","Integer");
        mysqlJavaTypeMap.put("integer","Integer");
        //小数
        mysqlJavaTypeMap.put("float","Float");
        mysqlJavaTypeMap.put("double","Double");
        mysqlJavaTypeMap.put("decimal","Double");
        //bool
        mysqlJavaTypeMap.put("bit","Boolean");
        //字符串
        mysqlJavaTypeMap.put("char","String");
        mysqlJavaTypeMap.put("varchar","String");
        mysqlJavaTypeMap.put("tinytext","String");
        mysqlJavaTypeMap.put("text","String");
        mysqlJavaTypeMap.put("mediumtext","String");
        mysqlJavaTypeMap.put("longtext","String");
        //日期
        mysqlJavaTypeMap.put("date","Date");
        mysqlJavaTypeMap.put("datetime","Date");
        mysqlJavaTypeMap.put("timestamp","Date");


        mysqlSwaggerTypeMap.put("bigint","integer");
        mysqlSwaggerTypeMap.put("int","integer");
        mysqlSwaggerTypeMap.put("tinyint","integer");
        mysqlSwaggerTypeMap.put("smallint","integer");
        mysqlSwaggerTypeMap.put("mediumint","integer");
        mysqlSwaggerTypeMap.put("integer","integer");
        mysqlSwaggerTypeMap.put("boolean","boolean");
        mysqlSwaggerTypeMap.put("float","number");
        mysqlSwaggerTypeMap.put("double","number");
        mysqlSwaggerTypeMap.put("decimal","Double");
    }

    public static HashMap<String, String> getMysqlJavaTypeMap() {
        return mysqlJavaTypeMap;
    }

    public static HashMap<String, String> getMysqlSwaggerTypeMap() {
        return mysqlSwaggerTypeMap;
    }
}
