package com.softdev.system.generator.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.softdev.system.generator.entity.dto.ClassInfo;
import com.softdev.system.generator.entity.dto.ParamInfo;
import com.softdev.system.generator.entity.enums.ParserTypeEnum;
import com.softdev.system.generator.entity.vo.ResultVo;
import com.softdev.system.generator.service.CodeGenService;
import com.softdev.system.generator.service.TemplateService;
import com.softdev.system.generator.service.parser.JsonParserService;
import com.softdev.system.generator.service.parser.SqlParserService;
import com.softdev.system.generator.util.FreemarkerUtil;
import com.softdev.system.generator.util.MapUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 代码生成服务实现类
 *
 * @author zhengkai.blog.csdn.net
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CodeGenServiceImpl implements CodeGenService {

    private final TemplateService templateService;
    private final SqlParserService sqlParserService;
    private final JsonParserService jsonParserService;

    @Override
    public ResultVo generateCode(ParamInfo paramInfo) throws Exception {
        if (paramInfo.getTableSql() == null || paramInfo.getTableSql().isEmpty()) {
            return ResultVo.error("表结构信息为空");
        }

        try {
            // 1. Parse Table Structure 表结构解析
            ClassInfo classInfo = parseTableStructure(paramInfo);

            // 2. Set the params 设置表格参数
            paramInfo.getOptions().put("classInfo", classInfo);
            paramInfo.getOptions().put("tableName", classInfo == null ? System.currentTimeMillis() + "" : classInfo.getTableName());

            // 3. generate the code by freemarker templates with parameters .
            // Freemarker根据参数和模板生成代码
            Map<String, String> result = getResultByParams(paramInfo.getOptions());
            log.info("table:{} - time:{} ", MapUtil.getString(result, "tableName"), System.currentTimeMillis());
            return ResultVo.ok(result);
        } catch (Exception e) {
            log.error("代码生成失败", e);
            return ResultVo.error("代码生成失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, String> getResultByParams(Map<String, Object> params) throws Exception {
        Map<String, String> result = new HashMap<>(32);
        result.put("tableName", MapUtil.getString(params, "tableName"));

        // 处理模板生成逻辑
        // 解析模板配置并生成代码
        JSONArray parentTemplates = templateService.getAllTemplates();
        for (int i = 0; i < parentTemplates.size(); i++) {
            JSONObject parentTemplateObj = parentTemplates.getJSONObject(i);
            JSONArray childTemplates = parentTemplateObj.getJSONArray("templates");
            if (childTemplates != null) {
                for (int x = 0; x < childTemplates.size(); x++) {
                    JSONObject childTemplate = childTemplates.getJSONObject(x);
                    String templatePath = parentTemplateObj.getString("group") + "/" + childTemplate.getString("name") + ".ftl";
                    String generatedCode = FreemarkerUtil.processString(templatePath, params);
                    result.put(childTemplate.getString("name"), generatedCode);
                }
            }
        }
        
        return result;
    }

    /**
     * 根据不同的解析类型解析表结构
     *
     * @param paramInfo 参数信息
     * @return 类信息
     * @throws Exception 解析异常
     */
    private ClassInfo parseTableStructure(ParamInfo paramInfo) throws Exception {
        String dataType = MapUtil.getString(paramInfo.getOptions(), "dataType");
        ParserTypeEnum parserType = ParserTypeEnum.fromValue(dataType);
        
        // 添加调试信息
        log.debug("解析数据类型: {}, 解析结果: {}", dataType, parserType);

        switch (parserType) {
            case SQL:
                // 默认模式：parse DDL table structure from sql
                return sqlParserService.processTableIntoClassInfo(paramInfo);
            case JSON:
                // JSON模式：parse field from json string
                return jsonParserService.processJsonToClassInfo(paramInfo);
            case INSERT_SQL:
                // INSERT SQL模式：parse field from insert sql
                return sqlParserService.processInsertSqlToClassInfo(paramInfo);
            case SQL_REGEX:
                // 正则表达式模式（非完善版本）：parse sql by regex
                return sqlParserService.processTableToClassInfoByRegex(paramInfo);
            case SELECT_SQL:
                // SelectSqlBySQLPraser模式:parse select sql by JSqlParser
                return sqlParserService.generateSelectSqlBySQLPraser(paramInfo);
            case CREATE_SQL:
                // CreateSqlBySQLPraser模式:parse create sql by JSqlParser
                return sqlParserService.generateCreateSqlBySQLPraser(paramInfo);
            default:
                // 默认模式：parse DDL table structure from sql
                return sqlParserService.processTableIntoClassInfo(paramInfo);
        }
    }
}