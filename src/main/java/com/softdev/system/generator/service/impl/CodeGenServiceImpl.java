package com.softdev.system.generator.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.softdev.system.generator.entity.dto.ClassInfo;
import com.softdev.system.generator.entity.dto.CodeGenResult;
import com.softdev.system.generator.entity.dto.ParamInfo;
import com.softdev.system.generator.entity.enums.ParserTypeEnum;
import com.softdev.system.generator.entity.vo.ResultVo;
import com.softdev.system.generator.service.CodeGenService;
import com.softdev.system.generator.service.TemplateService;
import com.softdev.system.generator.service.parser.JsonParserService;
import com.softdev.system.generator.service.parser.SqlParserService;
import com.softdev.system.generator.util.FreemarkerUtil;
import com.softdev.system.generator.util.HeritageUtil;
import com.softdev.system.generator.util.MapUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
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
            CodeGenResult result = doGenerate(paramInfo);
            Map<String, String> generatedCode = result.getGeneratedCode();
            generatedCode.put("tableName", result.getTableName());
            log.info("table:{} - time:{} ", result.getTableName(), System.currentTimeMillis());
            return ResultVo.ok(generatedCode);
        } catch (Exception e) {
            log.error("代码生成失败", e);
            return ResultVo.error("代码生成失败: " + e.getMessage());
        }
    }

    @Override
    public ClassInfo parseTableStructure(ParamInfo paramInfo) throws Exception {
        if (paramInfo.getTableSql() == null || paramInfo.getTableSql().isEmpty()) {
            throw new IllegalArgumentException("表结构信息为空");
        }
        return parseByType(paramInfo);
    }

    @Override
    public CodeGenResult getResultByParams(Map<String, Object> params) throws Exception {
        CodeGenResult.CodeGenResultBuilder builder = CodeGenResult.builder();

        Map<String, String> generatedCode = new LinkedHashMap<>();
        Map<String, String> fileNameTemplates = new LinkedHashMap<>();
        Map<String, String> groupByTemplate = new LinkedHashMap<>();

        generatedCode.put("tableName", MapUtil.getString(params, "tableName"));

        boolean heritage = HeritageUtil.isEnabled(params);
        JSONArray parentTemplates = templateService.getAllTemplates();
        for (int i = 0; i < parentTemplates.size(); i++) {
            JSONObject parentTemplateObj = parentTemplates.getJSONObject(i);
            String group = parentTemplateObj.getString("group");
            JSONArray childTemplates = parentTemplateObj.getJSONArray("templates");
            if (childTemplates != null) {
                for (int x = 0; x < childTemplates.size(); x++) {
                    JSONObject childTemplate = childTemplates.getJSONObject(x);
                    String templateName = childTemplate.getString("name");
                    String templatePath = group + "/" + templateName + ".ftl";
                    String generatedText = FreemarkerUtil.processString(templatePath, params);
                    if (heritage) {
                        generatedText = HeritageUtil.stamp(childTemplate.getString("fileName"), generatedText);
                    }
                    generatedCode.put(templateName, generatedText);
                    fileNameTemplates.put(templateName, childTemplate.getString("fileName"));
                    groupByTemplate.put(templateName, group);
                }
            }
        }

        Object classInfo = params.get("classInfo");
        String className = null;
        if (classInfo instanceof ClassInfo) {
            className = ((ClassInfo) classInfo).getClassName();
        }
        if (className == null) {
            className = MapUtil.getString(params, "tableName");
        }
        return builder
                .tableName(MapUtil.getString(params, "tableName"))
                .className(className)
                .generatedCode(generatedCode)
                .fileNameTemplates(fileNameTemplates)
                .groupByTemplate(groupByTemplate)
                .build();
    }

    /**
     * 共享的生成逻辑：先解析，再调用 getResultByParams
     */
    private CodeGenResult doGenerate(ParamInfo paramInfo) throws Exception {
        ClassInfo classInfo = parseByType(paramInfo);

        paramInfo.getOptions().put("classInfo", classInfo);
        paramInfo.getOptions().put("tableName", classInfo == null ? System.currentTimeMillis() + "" : classInfo.getTableName());

        return getResultByParams(paramInfo.getOptions());
    }

    /**
     * 根据不同的解析类型解析表结构
     */
    private ClassInfo parseByType(ParamInfo paramInfo) throws Exception {
        String dataType = MapUtil.getString(paramInfo.getOptions(), "dataType");
        ParserTypeEnum parserType = ParserTypeEnum.fromValue(dataType);
        log.debug("解析数据类型: {}, 解析结果: {}", dataType, parserType);

        switch (parserType) {
            case SQL:
                return sqlParserService.processTableIntoClassInfo(paramInfo);
            case JSON:
                return jsonParserService.processJsonToClassInfo(paramInfo);
            case INSERT_SQL:
                return sqlParserService.processInsertSqlToClassInfo(paramInfo);
            case SQL_REGEX:
                return sqlParserService.processTableToClassInfoByRegex(paramInfo);
            case SELECT_SQL:
                return sqlParserService.generateSelectSqlBySQLPraser(paramInfo);
            case CREATE_SQL:
                return sqlParserService.generateCreateSqlBySQLPraser(paramInfo);
            default:
                return sqlParserService.processTableIntoClassInfo(paramInfo);
        }
    }
}
