package com.softdev.system.generator.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * freemarker tool
 *
 * @author xuxueli 2018-05-02 19:56:00
 */
@Component
public class FreemarkerTool {

    @Autowired
    private Configuration configuration;

    /**
     * process Template Into String
     *
     * @param template
     * @param model
     * @return
     * @throws IOException
     * @throws TemplateException
     */
    public String processTemplateIntoString(Template template, Object model)
            throws IOException, TemplateException {

        StringWriter result = new StringWriter();
        template.process(model, result);
        return result.toString();
    }
    /**
    * 传入需要转义的字符串进行转义
    * 20200503 zhengkai.blog.csdn.net
    * */
    public String escapeString(String originStr){
        return originStr.replaceAll("井","\\#").replaceAll("￥","\\$");
    }
    /**
     * process String
     *
     * @param templateName
     * @param params
     * @return
     * @throws IOException
     * @throws TemplateException
     */
    public String processString(String templateName, Map<String, Object> params)
            throws IOException, TemplateException {
        //获取对应的模板
        Template template = configuration.getTemplate(templateName);
        //处理为template并进行转义
        String htmlText = escapeString(processTemplateIntoString(template, params));
        return htmlText;
    }


}
