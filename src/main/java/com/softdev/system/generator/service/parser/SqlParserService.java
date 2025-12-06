package com.softdev.system.generator.service.parser;

import com.softdev.system.generator.entity.dto.ClassInfo;
import com.softdev.system.generator.entity.dto.ParamInfo;

/**
 * SQL解析服务接口
 *
 * @author zhengkai.blog.csdn.net
 */
public interface SqlParserService {

    /**
     * 解析Select-SQL生成类信息(JSQLPraser版本)
     *
     * @param paramInfo 参数信息
     * @return 类信息
     * @throws Exception 解析异常
     */
    ClassInfo generateSelectSqlBySQLPraser(ParamInfo paramInfo) throws Exception;

    /**
     * 解析Create-SQL生成类信息(JSQLPraser版本)
     *
     * @param paramInfo 参数信息
     * @return 类信息
     * @throws Exception 解析异常
     */
    ClassInfo generateCreateSqlBySQLPraser(ParamInfo paramInfo) throws Exception;

    /**
     * 解析DDL-SQL生成类信息
     *
     * @param paramInfo 参数信息
     * @return 类信息
     * @throws Exception 解析异常
     */
    ClassInfo processTableIntoClassInfo(ParamInfo paramInfo) throws Exception;

    /**
     * 解析DDL SQL生成类信息-正则表达式版本
     *
     * @param paramInfo 参数信息
     * @return 类信息
     */
    ClassInfo processTableToClassInfoByRegex(ParamInfo paramInfo);

    /**
     * 解析INSERT-SQL生成类信息-正则表达式版本
     *
     * @param paramInfo 参数信息
     * @return 类信息
     */
    ClassInfo processInsertSqlToClassInfo(ParamInfo paramInfo);
}