package com.softdev.system.generator.service.impl.parser;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.softdev.system.generator.entity.dto.ClassInfo;
import com.softdev.system.generator.entity.dto.FieldInfo;
import com.softdev.system.generator.entity.dto.ParamInfo;
import com.softdev.system.generator.service.parser.JsonParserService;
import com.softdev.system.generator.util.exception.CodeGenException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JSON解析服务实现类
 *
 * @author zhengkai.blog.csdn.net
 */
@Service
public class JsonParserServiceImpl implements JsonParserService {

    @Override
    public ClassInfo processJsonToClassInfo(ParamInfo paramInfo) {
        ClassInfo codeJavaInfo = new ClassInfo();
        codeJavaInfo.setTableName("JsonDto");
        codeJavaInfo.setClassName("JsonDto");
        codeJavaInfo.setClassComment("JsonDto");

        //support children json if forget to add '{' in front of json
        if (paramInfo.getTableSql().trim().startsWith("\"")) {
            paramInfo.setTableSql("{" + paramInfo.getTableSql());
        }
        try {
            if (paramInfo.getTableSql().trim().startsWith("{")) {
                JSONObject jsonObject = JSONObject.parseObject(paramInfo.getTableSql().trim());
                //parse FieldList by JSONObject
                codeJavaInfo.setFieldList(processJsonObjectToFieldList(jsonObject));
            } else if (paramInfo.getTableSql().trim().startsWith("[")) {
                JSONArray jsonArray = JSONArray.parseArray(paramInfo.getTableSql().trim());
                //parse FieldList by JSONObject
                codeJavaInfo.setFieldList(processJsonObjectToFieldList(jsonArray.getJSONObject(0)));
            }
        } catch (Exception e) {
            // JSON解析失败，抛出自定义异常
            throw new CodeGenException("JSON格式不正确: " + e.getMessage());
        }

        return codeJavaInfo;
    }

    public List<FieldInfo> processJsonObjectToFieldList(JSONObject jsonObject) {
        // field List
        List<FieldInfo> fieldList = new ArrayList<FieldInfo>();
        for (String jsonField : jsonObject.keySet()) {
            FieldInfo fieldInfo = new FieldInfo();
            fieldInfo.setFieldName(jsonField);
            fieldInfo.setColumnName(jsonField);
            fieldInfo.setFieldClass(String.class.getSimpleName());
            fieldInfo.setFieldComment("father:" + jsonField);
            fieldList.add(fieldInfo);
            if (jsonObject.get(jsonField) instanceof JSONArray) {
                JSONArray jsonArray = jsonObject.getJSONArray(jsonField);
                for (Object arrayObject : jsonArray) {
                    FieldInfo fieldInfo2 = new FieldInfo();
                    fieldInfo2.setFieldName(arrayObject.toString());
                    fieldInfo2.setColumnName(arrayObject.toString());
                    fieldInfo2.setFieldClass(String.class.getSimpleName());
                    fieldInfo2.setFieldComment("children:" + arrayObject.toString());
                    fieldList.add(fieldInfo2);
                }
            } else if (jsonObject.get(jsonField) instanceof JSONObject) {
                JSONObject subJsonObject = jsonObject.getJSONObject(jsonField);
                for (String arrayObject : subJsonObject.keySet()) {
                    FieldInfo fieldInfo2 = new FieldInfo();
                    fieldInfo2.setFieldName(arrayObject.toString());
                    fieldInfo2.setColumnName(arrayObject.toString());
                    fieldInfo2.setFieldClass(String.class.getSimpleName());
                    fieldInfo2.setFieldComment("children:" + arrayObject.toString());
                    fieldList.add(fieldInfo2);
                }
            }
        }
        if (fieldList.size() < 1) {
            throw new CodeGenException("JSON解析失败");
        }
        return fieldList;
    }
}