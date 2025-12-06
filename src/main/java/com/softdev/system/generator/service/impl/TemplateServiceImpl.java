package com.softdev.system.generator.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.softdev.system.generator.service.TemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * 模板服务实现类
 *
 * @author zhengkai.blog.csdn.net
 */
@Slf4j
@Service
public class TemplateServiceImpl implements TemplateService {

    private String templateConfig = null;

    @Override
    public JSONArray getAllTemplates() throws IOException {
        if (templateConfig == null) {
            ClassPathResource resource = new ClassPathResource("template.json");
            try (InputStream inputStream = resource.getInputStream()) {
                templateConfig = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            }
        }
        return JSONArray.parseArray(templateConfig);
    }
}