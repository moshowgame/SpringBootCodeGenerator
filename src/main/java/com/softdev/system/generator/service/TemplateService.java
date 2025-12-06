package com.softdev.system.generator.service;

import com.alibaba.fastjson2.JSONArray;

import java.io.IOException;

/**
 * 模板服务接口
 *
 * @author zhengkai.blog.csdn.net
 */
public interface TemplateService {

    /**
     * 获取所有模板配置
     *
     * @return 模板配置字符串
     * @throws IOException IO异常
     */
    JSONArray getAllTemplates() throws IOException;
}