package com.softdev.system.generator.service;

import com.softdev.system.generator.entity.ClassInfo;
import com.softdev.system.generator.entity.ParamInfo;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * GeneratorService
 *
 * @author zhengkai.blog.csdn.net
 */
public interface GeneratorService {

    String getTemplateConfig() throws IOException;

    Map<String, String> getResultByParams(Map<String, Object> params) throws IOException, TemplateException;
    /**
     * 解析Select-SQL生成类信息(JSQLPraser版本)
     * @auther: zhengkai.blog.csdn.net
     * @param paramInfo
     * @return
     */
    ClassInfo generateSelectSqlBySQLPraser(ParamInfo paramInfo) throws Exception;
    /**
     * 解析DDL-SQL生成类信息
     * @auther: zhengkai.blog.csdn.net
     * @param paramInfo
     * @return
     */
    ClassInfo processTableIntoClassInfo(ParamInfo paramInfo) throws Exception;
    /**
     * 解析JSON生成类信息
     * @auther: zhengkai.blog.csdn.net
     * @param paramInfo
     * @return
     */
    ClassInfo processJsonToClassInfo(ParamInfo paramInfo);
    /**
     * 解析DDL SQL生成类信息-正则表达式版本
     * @auther: zhengkai.blog.csdn.net
     * @param paramInfo
     * @return
     */
    ClassInfo processTableToClassInfoByRegex(ParamInfo paramInfo);
    /**
     * 解析INSERT-SQL生成类信息-正则表达式版本
     * @auther: zhengkai.blog.csdn.net
     * @param paramInfo
     * @return
     */
    ClassInfo processInsertSqlToClassInfo(ParamInfo paramInfo);
}
