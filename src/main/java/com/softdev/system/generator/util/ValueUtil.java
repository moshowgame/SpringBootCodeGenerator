package com.softdev.system.generator.util;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Get Value From Application.yml
 * @author zhengkai.blog.csdn.net
 */
@Data
@Component
public class ValueUtil {
    @Value("${OEM.title}")
    public String title;

    @Value("${OEM.header}")
    public String header;

    @Value("${OEM.version}")
    public String version;

    @Value("${OEM.author}")
    public String author;

    @Value("${OEM.keywords}")
    public String keywords;

    @Value("${OEM.slogan}")
    public String slogan;

    @Value("${OEM.copyright}")
    public String copyright;

    @Value("${OEM.description}")
    public String description;

    @Value("${OEM.packageName}")
    public String packageName;

    @Value("${OEM.returnUtilSuccess}")
    public String returnUtilSuccess;

    @Value("${OEM.returnUtilFailure}")
    public String returnUtilFailure;

    @Value("${OEM.outputStr}")
    public String outputStr;

    @Value("${OEM.mode}")
    public String mode;
}

