package com.softdev.system.generator.controller;

import com.softdev.system.generator.entity.ClassInfo;
import com.softdev.system.generator.entity.ReturnT;
import com.softdev.system.generator.util.CodeGeneratorTool;
import com.softdev.system.generator.util.FreemarkerTool;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * spring boot code generator
 * @author zhengk/moshow
 */
@Controller
public class IndexController {
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    private FreemarkerTool freemarkerTool;

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/genCode")
    @ResponseBody
    public ReturnT<Map<String, String>> codeGenerate(String tableSql,String authorName,String packageName) {

        if(StringUtils.isBlank(authorName)) authorName="大狼狗";

        if(StringUtils.isBlank(packageName)) packageName="com.softdev.system";

        try {

            if (StringUtils.isBlank(tableSql)) {
                return new ReturnT<Map<String, String>>(ReturnT.FAIL_CODE, "表结构信息不可为空");
            }

            // parse table
            ClassInfo classInfo = CodeGeneratorTool.processTableIntoClassInfo(tableSql);

            // code genarete
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("classInfo", classInfo);
            params.put("authorName", authorName);
            params.put("packageName", packageName);

            // result
            Map<String, String> result = new HashMap<String, String>();

            result.put("controller", freemarkerTool.processString("xxl-code-generator/controller.ftl", params));
            result.put("service", freemarkerTool.processString("xxl-code-generator/service.ftl", params));
            result.put("service_impl", freemarkerTool.processString("xxl-code-generator/service_impl.ftl", params));
            result.put("dao", freemarkerTool.processString("xxl-code-generator/dao.ftl", params));
            result.put("mybatis", freemarkerTool.processString("xxl-code-generator/mybatis.ftl", params));
            result.put("model", freemarkerTool.processString("xxl-code-generator/model.ftl", params));

            result.put("entity", freemarkerTool.processString("xxl-code-generator/entity.ftl", params));
            result.put("swaggerui", freemarkerTool.processString("xxl-code-generator/swagger-ui.ftl", params));
            result.put("repository", freemarkerTool.processString("xxl-code-generator/repository.ftl", params));
            result.put("jpacontroller", freemarkerTool.processString("xxl-code-generator/jpacontroller.ftl", params));

            result.put("jtdao", freemarkerTool.processString("xxl-code-generator/jtdao.ftl", params));
            result.put("jtdaoimpl", freemarkerTool.processString("xxl-code-generator/jtdaoimpl.ftl", params));

            result.put("beetlmd", freemarkerTool.processString("xxl-code-generator/beetlmd.ftl", params));
            result.put("beetlentity", freemarkerTool.processString("xxl-code-generator/beetlentity.ftl", params));
            result.put("beetlcontroller", freemarkerTool.processString("xxl-code-generator/beetlcontroller.ftl", params));

            result.put("elementui", freemarkerTool.processString("xxl-code-generator/element-ui.ftl", params));
            result.put("bootstrap", freemarkerTool.processString("xxl-code-generator/bootstrap.ftl", params));

            // 计算,生成代码行数
            int lineNum = 0;
            for (Map.Entry<String, String> item: result.entrySet()) {
                if (item.getValue() != null) {
                    lineNum += StringUtils.countMatches(item.getValue(), "\n");
                }
            }
            logger.info("生成代码行数：{}", lineNum);
            //测试环境可自行开启
            //logger.info("生成代码数据：{}", result);
            return new ReturnT<Map<String, String>>(result);
        } catch (IOException | TemplateException e) {
            logger.error(e.getMessage(), e);
            return new ReturnT<Map<String, String>>(ReturnT.FAIL_CODE, "表结构解析失败"+e.getMessage());
        }

    }

}