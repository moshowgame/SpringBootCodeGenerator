package com.softdev.system.generator.util;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

/**
 * freemarker tool
 *
 * @author xuxueli 2018-05-02 19:56:00
 */
@Slf4j
@Component
public class FreemarkerUtil {

    @Autowired
    private Configuration configuration;

    /**
     * 传入需要转义的字符串进行转义
     * 20200503 zhengkai.blog.csdn.net
     */
    public static String escapeString(String originStr) {
        return originStr.replaceAll("井", "\\#").replaceAll("￥", "\\$");
    }

    /**
     * freemarker config
     */
    private static Configuration freemarkerConfig = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);

    static {
        try {
            //2020-06-21 zhengkai 修复path问题导致jar无法运行而本地项目可以运行的bug
            freemarkerConfig.setClassForTemplateLoading(FreemarkerUtil.class, "/templates/code-generator");
            freemarkerConfig.setTemplateLoader(new ClassTemplateLoader(FreemarkerUtil.class, "/templates/code-generator"));
            //freemarkerConfig.setDirectoryForTemplateLoading(new File(templatePath, "templates/code-generator"));
            freemarkerConfig.setNumberFormat("#");
            freemarkerConfig.setClassicCompatible(true);
            freemarkerConfig.setDefaultEncoding("UTF-8");
            freemarkerConfig.setLocale(Locale.CHINA);
            freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * process Template Into String
     *
     * @param template
     * @param model
     * @return
     * @throws IOException
     * @throws TemplateException
     */
    public static String processTemplateIntoString(Template template, Object model)
            throws IOException, TemplateException {

        StringWriter result = new StringWriter();
        template.process(model, result);
        return result.toString();
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
    public static String processString(String templateName, Map<String, Object> params)
            throws IOException, TemplateException {

        Template template = freemarkerConfig.getTemplate(templateName);
        String htmlText = escapeString(processTemplateIntoString(template, params));
        return htmlText;
    }

}
