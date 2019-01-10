package com.softdev.system.generator.controller;

import com.softdev.system.generator.entity.ClassInfo;
import com.softdev.system.generator.entity.ReturnT;
import com.softdev.system.generator.util.CodeGeneratorTool;
import com.softdev.system.generator.util.FreemarkerTool;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * spring boot code generator
 * @author zhengk/moshow
 */
@Slf4j
@Controller
public class IndexController {

    @Autowired
    private FreemarkerTool freemarkerTool;

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/genCode")
    @ResponseBody
    public ReturnT<Map<String, String>> codeGenerate(String tableSql,
                                                     @RequestParam(required = false, defaultValue = "大狼狗") String authorName,
                                                     @RequestParam(required = false, defaultValue = "com.softdev.system")String packageName) {

        try {

            if (StringUtils.isBlank(tableSql)) {
                return new ReturnT<>(ReturnT.FAIL_CODE, "表结构信息不可为空");
            }

            // parse table
            ClassInfo classInfo = CodeGeneratorTool.processTableIntoClassInfo(tableSql);

            // code genarete
            Map<String, Object> params = new HashMap<>(3);
            params.put("classInfo", classInfo);
            params.put("authorName", authorName);
            params.put("packageName", packageName);

            // result
            Map<String, String> result = new HashMap<>();

            //UI
            result.put("swaggerui", freemarkerTool.processString("xxl-code-generator/swaggerui/swagger-ui.ftl", params));
            result.put("elementui", freemarkerTool.processString("xxl-code-generator/swaggerui/element-ui.ftl", params));
            result.put("bootstrap", freemarkerTool.processString("xxl-code-generator/swaggerui/bootstrap.ftl", params));

            //mybatis old
            result.put("controller", freemarkerTool.processString("xxl-code-generator/mybatis/controller.ftl", params));
            result.put("service", freemarkerTool.processString("xxl-code-generator/mybatis/service.ftl", params));
            result.put("service_impl", freemarkerTool.processString("xxl-code-generator/mybatis/service_impl.ftl", params));
            result.put("dao", freemarkerTool.processString("xxl-code-generator/mybatis/dao.ftl", params));
            result.put("mybatis", freemarkerTool.processString("xxl-code-generator/mybatis/mybatis.ftl", params));
            result.put("model", freemarkerTool.processString("xxl-code-generator/mybatis/model.ftl", params));
            //jpa
            result.put("entity", freemarkerTool.processString("xxl-code-generator/jpa/entity.ftl", params));
            result.put("repository", freemarkerTool.processString("xxl-code-generator/jpa/repository.ftl", params));
            result.put("jpacontroller", freemarkerTool.processString("xxl-code-generator/jpa/jpacontroller.ftl", params));
            //jdbc template
            result.put("jtdao", freemarkerTool.processString("xxl-code-generator/jdbc/jtdao.ftl", params));
            result.put("jtdaoimpl", freemarkerTool.processString("xxl-code-generator/jdbc/jtdaoimpl.ftl", params));
            //beetlsql
            result.put("beetlmd", freemarkerTool.processString("xxl-code-generator/beetlsql/beetlmd.ftl", params));
            result.put("beetlentity", freemarkerTool.processString("xxl-code-generator/beetlsql/beetlentity.ftl", params));
            result.put("beetlcontroller", freemarkerTool.processString("xxl-code-generator/beetlsql/beetlcontroller.ftl", params));
            //mybatis plus
            result.put("pluscontroller", freemarkerTool.processString("xxl-code-generator/mybatis-plus/pluscontroller.ftl", params));
            result.put("plusmapper", freemarkerTool.processString("xxl-code-generator/mybatis-plus/plusmapper.ftl", params));

            // 计算,生成代码行数
            int lineNum = 0;
            for (Map.Entry<String, String> item: result.entrySet()) {
                if (item.getValue() != null) {
                    lineNum += StringUtils.countMatches(item.getValue(), "\n");
                }
            }
            log.info("生成代码行数：{}", lineNum);
            //测试环境可自行开启
            //logger.info("生成代码数据：{}", result);
            return new ReturnT<>(result);
        } catch (IOException | TemplateException e) {
            log.error(e.getMessage(), e);
            return new ReturnT<>(ReturnT.FAIL_CODE, "表结构解析失败" + e.getMessage());
        }

    }

}