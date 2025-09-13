package com.softdev.system.generator.controller;

import com.alibaba.fastjson2.JSONArray;
import com.softdev.system.generator.entity.ClassInfo;
import com.softdev.system.generator.entity.ParamInfo;
import com.softdev.system.generator.entity.ReturnT;
import com.softdev.system.generator.service.GeneratorService;
import com.softdev.system.generator.util.MapUtil;
import com.softdev.system.generator.util.TableParseUtil;
import com.softdev.system.generator.util.ValueUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.Map;

/**
 * 代码生成控制器
 * @author zhengkai.blog.csdn.net
 */
@Controller
@Slf4j
public class GeneratorController {
    @Autowired
    private ValueUtil valueUtil;

    @Autowired
    private GeneratorService generatorService;

    @GetMapping("/")
    public ModelAndView defaultPage() {
        return new ModelAndView("newui2").addObject("value",valueUtil);
    }
    @GetMapping("/index")
    public ModelAndView indexPage() {
        return new ModelAndView("newui2").addObject("value",valueUtil);
    }
    @GetMapping("/newui2")
    public ModelAndView newui2() {
        return new ModelAndView("newui2").addObject("value",valueUtil);
    }
    @GetMapping("/main")
    public ModelAndView mainPage() {
        return new ModelAndView("main").addObject("value",valueUtil);
    }

    @RequestMapping("/template/all")
    @ResponseBody
    public ReturnT getAllTemplates() throws Exception {
        String templates = generatorService.getTemplateConfig();
        return ReturnT.ok().put("templates", JSONArray.parseArray(templates));
    }
    @PostMapping("/code/generate")
    @ResponseBody
    public ReturnT generateCode(@RequestBody ParamInfo paramInfo) throws Exception {
        //log.info(JSON.toJSONString(paramInfo.getOptions()));
        if (StringUtils.isEmpty(paramInfo.getTableSql())) {
            return ReturnT.error("表结构信息为空");
        }
        //1.Parse Table Structure 表结构解析
        ClassInfo classInfo = null;
        String dataType = MapUtil.getString(paramInfo.getOptions(),"dataType");
        switch (dataType) {
            case "sql":
                //默认模式：parse DDL table structure from sql
                classInfo = generatorService.processTableIntoClassInfo(paramInfo);
                break;
            case "json":
                //JSON模式：parse field from json string
                classInfo = generatorService.processJsonToClassInfo(paramInfo);
                break;
            case "insert-sql":
                //INSERT SQL模式：parse field from insert sql
                classInfo = generatorService.processInsertSqlToClassInfo(paramInfo);
                break;
            case "sql-regex":
                //正则表达式模式（非完善版本）：parse sql by regex
                classInfo = generatorService.processTableToClassInfoByRegex(paramInfo);
                break;
            case "select-sql":
                //SelectSqlBySQLPraser模式:parse select sql by JSqlParser
                classInfo = generatorService.generateSelectSqlBySQLPraser(paramInfo);
                break;
            case "create-sql":
                //CreateSqlBySQLPraser模式:parse create sql by JSqlParser
                classInfo = generatorService.generateCreateSqlBySQLPraser(paramInfo);
                break;
            default:
                //默认模式：parse DDL table structure from sql
                classInfo = generatorService.processTableIntoClassInfo(paramInfo);
                break;
        }
        //2.Set the params 设置表格参数
        paramInfo.getOptions().put("classInfo", classInfo);
        paramInfo.getOptions().put("tableName", classInfo == null ? System.currentTimeMillis() : classInfo.getTableName());

        //log the generated table and filed size记录解析了什么表，有多少个字段
        //log.info("generated table :{} , size :{}",classInfo.getTableName(),(classInfo.getFieldList() == null ? "" : classInfo.getFieldList().size()));

        //3.generate the code by freemarker templates with parameters . Freemarker根据参数和模板生成代码
        Map<String, String> result = generatorService.getResultByParams(paramInfo.getOptions());
//        log.info("result {}",result);
        log.info("table:{} - time:{} ", MapUtil.getString(result,"tableName"),new Date());
        return ReturnT.ok().put("outputJson",result);
    }

}
