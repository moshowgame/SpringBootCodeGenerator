package com.softdev.system.generator.controller;

import com.alibaba.fastjson.JSON;
import com.softdev.system.generator.entity.ClassInfo;
import com.softdev.system.generator.entity.ParamInfo;
import com.softdev.system.generator.entity.ReturnT;
import com.softdev.system.generator.service.GeneratorService;
import com.softdev.system.generator.util.CodeGenerateException;
import com.softdev.system.generator.util.TableParseUtil;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * spring boot code generator
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
    public ReturnT<Map<String, String>> codeGenerate(@RequestBody ParamInfo paramInfo ) {

        try {

            if (StringUtils.isBlank(paramInfo.getTableSql())) {
                return new ReturnT<>(ReturnT.FAIL_CODE, "表结构信息不可为空");
            }

            // parse table
            ClassInfo classInfo = null;
            switch (paramInfo.getDataType()){
                //parse json
                case "json":classInfo = TableParseUtil.processJsonToClassInfo(paramInfo);break;
                //parse sql  by regex
                case "sql-regex":classInfo = TableParseUtil.processTableToClassInfoByRegex(paramInfo);break;
                //default parse sql by java
                default : classInfo = TableParseUtil.processTableIntoClassInfo(paramInfo);break;
            }

            // process the param
            Map<String, Object> params = new HashMap<String, Object>(8);
            params.put("classInfo", classInfo);
            params.put("tableName", classInfo==null?System.currentTimeMillis():classInfo.getTableName());
            params.put("authorName", paramInfo.getAuthorName());
            params.put("packageName", paramInfo.getPackageName());
            params.put("returnUtil", paramInfo.getReturnUtil());
            params.put("swagger", paramInfo.isSwagger());

            //log the params
            //log.info(JSON.toJSONString(paramInfo));

            log.info("generator table:"+(classInfo==null?"":classInfo.getTableName())
                    +",field size:"+((classInfo==null||classInfo.getFieldList()==null)?"":classInfo.getFieldList().size()));

            // generate the code 需要加新的模板请在里面改
            Map<String, String> result = generatorService.getResultByParams(params);

            return new ReturnT<>(result);
        } catch (IOException | TemplateException e) {
            log.error(e.getMessage(), e);
            return new ReturnT<>(ReturnT.FAIL_CODE, e.getMessage());
        } catch (CodeGenerateException e) {
            log.error(e.getMessage(), e);
            return new ReturnT<>(ReturnT.FAIL_CODE, e.getMessage());
        }

    }

}
