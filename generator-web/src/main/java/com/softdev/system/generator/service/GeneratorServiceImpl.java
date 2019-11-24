package com.softdev.system.generator.service;

import com.softdev.system.generator.util.FreemarkerTool;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * GeneratorService
 * @author zhengkai.blog.csdn.net
 */
@Slf4j
@Service
public class GeneratorServiceImpl implements GeneratorService {

    @Autowired
    private FreemarkerTool freemarkerTool;

    @Override
    public Map<String, String> getResultByParams(Map<String, Object> params) throws IOException, TemplateException {
        // result
        Map<String, String> result = new HashMap<String, String>(32);

        //UI
        result.put("swagger-ui", freemarkerTool.processString("code-generator/ui/swagger-ui.ftl", params));
        result.put("element-ui", freemarkerTool.processString("code-generator/ui/element-ui.ftl", params));
        result.put("bootstrap-ui", freemarkerTool.processString("code-generator/ui/bootstrap-ui.ftl", params));
        //mybatis old
        result.put("controller", freemarkerTool.processString("code-generator/mybatis/controller.ftl", params));
        result.put("service", freemarkerTool.processString("code-generator/mybatis/service.ftl", params));
        result.put("service_impl", freemarkerTool.processString("code-generator/mybatis/service_impl.ftl", params));
        result.put("mapper", freemarkerTool.processString("code-generator/mybatis/mapper.ftl", params));
        result.put("mybatis", freemarkerTool.processString("code-generator/mybatis/mybatis.ftl", params));
        result.put("model", freemarkerTool.processString("code-generator/mybatis/model.ftl", params));
        //jpa
        result.put("entity", freemarkerTool.processString("code-generator/jpa/entity.ftl", params));
        result.put("repository", freemarkerTool.processString("code-generator/jpa/repository.ftl", params));
        result.put("jpacontroller", freemarkerTool.processString("code-generator/jpa/jpacontroller.ftl", params));
        //jdbc template
        result.put("jtdao", freemarkerTool.processString("code-generator/jdbc-template/jtdao.ftl", params));
        result.put("jtdaoimpl", freemarkerTool.processString("code-generator/jdbc-template/jtdaoimpl.ftl", params));
        //beetsql
        result.put("beetlmd", freemarkerTool.processString("code-generator/beetlsql/beetlmd.ftl", params));
        result.put("beetlentity", freemarkerTool.processString("code-generator/beetlsql/beetlentity.ftl", params));
        result.put("beetlentitydto", freemarkerTool.processString("code-generator/beetlsql/beetlentitydto.ftl", params));
        result.put("beetlcontroller", freemarkerTool.processString("code-generator/beetlsql/beetlcontroller.ftl", params));
        //mybatis plus
        result.put("pluscontroller", freemarkerTool.processString("code-generator/mybatis-plus/pluscontroller.ftl", params));
        result.put("plusmapper", freemarkerTool.processString("code-generator/mybatis-plus/plusmapper.ftl", params));
        //util
        result.put("util", freemarkerTool.processString("code-generator/util/util.ftl", params));
        result.put("json", freemarkerTool.processString("code-generator/util/json.ftl", params));
        result.put("xml", freemarkerTool.processString("code-generator/util/xml.ftl", params));
        //sql generate
        result.put("select", freemarkerTool.processString("code-generator/sql/select.ftl", params));
        result.put("insert", freemarkerTool.processString("code-generator/sql/insert.ftl", params));
        result.put("update", freemarkerTool.processString("code-generator/sql/update.ftl", params));
        result.put("delete", freemarkerTool.processString("code-generator/sql/delete.ftl", params));

        // 计算,生成代码行数
        /*int lineNum = 0;
        for (Map.Entry<String, String> item: result.entrySet()) {
            if (item.getValue() != null) {
                lineNum += StringUtils.countMatches(item.getValue(), "\n");
            }
        }
        log.info("生成代码行数：{}", lineNum);*/

        //测试环境可自行开启
        //log.info("生成代码数据：{}", result);
        return result;
    }
}
