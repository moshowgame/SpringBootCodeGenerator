package com.softdev.system.generator.controller;

import com.softdev.system.generator.entity.ClassInfo;
import com.softdev.system.generator.entity.ParamInfo;
import com.softdev.system.generator.entity.ReturnT;
import com.softdev.system.generator.service.GeneratorService;
import com.softdev.system.generator.util.TableParseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * spring boot code generator
 *
 * @author zhengk/moshow
 */
@Controller
@Slf4j
public class IndexController {

    @Autowired
    private GeneratorService generatorService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/genCode")
    @ResponseBody
    public ReturnT codeGenerate(@RequestBody ParamInfo paramInfo) throws Exception {

        if (paramInfo.getTableSql().trim().length() < 1) {
            return ReturnT.ERROR("表结构信息不可为空");
        }

        //1.Parse Table Structure 表结构解析
        ClassInfo classInfo = null;
        switch (paramInfo.getDataType()) {
            //JSON模式：parse field from json string
            case "json":
                classInfo = TableParseUtil.processJsonToClassInfo(paramInfo);
                break;
            //INSERT SQL模式：parse field from insert sql
            case "insert-sql":
                classInfo = TableParseUtil.processInsertSqlToClassInfo(paramInfo);
                break;
            //正则表达式模式（非完善版本）：parse sql by regex
            case "sql-regex":
                classInfo = TableParseUtil.processTableToClassInfoByRegex(paramInfo);
                break;
            //默认模式：default parse sql by java
            default:
                classInfo = TableParseUtil.processTableIntoClassInfo(paramInfo);
                break;
        }

        //2.Set the params 设置表格参数
        Map<String, Object> params = new HashMap<String, Object>(8);
        params.put("classInfo", classInfo);
        params.put("tableName", classInfo == null ? System.currentTimeMillis() : classInfo.getTableName());
        params.put("authorName", paramInfo.getAuthorName());
        params.put("packageName", paramInfo.getPackageName());
        params.put("returnUtil", paramInfo.getReturnUtil());
        params.put("swagger", paramInfo.isSwagger());

        //log the generated table and filed size记录解析了什么表，有多少个字段
        log.info("generated table:" + classInfo.getTableName() + ",field size:" + (classInfo.getFieldList() == null ? "" : classInfo.getFieldList().size()));

        //3.generate the code by freemarker template and param . Freemarker根据参数和模板生成代码
        Map<String, String> result = generatorService.getResultByParams(params);

        return ReturnT.SUCCESS(result);
    }

}
