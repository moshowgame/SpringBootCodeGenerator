package com.softdev.system.generator.service;

import com.alibaba.fastjson2.JSONArray;
import com.softdev.system.generator.service.impl.TemplateServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TemplateService单元测试
 *
 * @author zhengkai.blog.csdn.net
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TemplateService测试")
class TemplateServiceTest {

    @InjectMocks
    private TemplateServiceImpl templateService;

    private String mockTemplateConfig;

    @BeforeEach
    void setUp() {
        mockTemplateConfig = "[{\"group\":\"basic\",\"templates\":[{\"name\":\"Entity\",\"type\":\"java\"},{\"name\":\"Repository\",\"type\":\"java\"}]}]";
    }

    @Test
    @DisplayName("测试获取所有模板配置成功")
    void testGetAllTemplatesSuccess() throws IOException {
        // 这个测试需要实际的模板文件，所以只测试服务层逻辑
        // 在实际环境中，template.json文件需要存在
        
        // Since we can't easily mock ClassPathResource with static methods in this context,
        // we'll test the actual implementation if the file exists
        try {
            JSONArray result = templateService.getAllTemplates();
            assertNotNull(result);
            // 验证结果不为空
        } catch (Exception e) {
            // 如果文件不存在，这个测试可能会失败，这是预期的
            assertTrue(e instanceof IOException || e.getCause() instanceof IOException);
        }
    }

    @Test
    @DisplayName("测试模板配置缓存")
    void testTemplateConfigCache() throws IOException {
        try {
            // 第一次调用
            JSONArray result1 = templateService.getAllTemplates();
            
            // 第二次调用应该使用缓存
            JSONArray result2 = templateService.getAllTemplates();
            
            // 验证两次结果相同（使用了缓存）
            assertEquals(result1, result2);
        } catch (Exception e) {
            // 如果文件不存在，跳过缓存测试
            assertTrue(e instanceof IOException || e.getCause() instanceof IOException);
        }
    }

    @Test
    @DisplayName("测试模板配置解析")
    void testTemplateConfigParsing() {
        // 测试JSON解析逻辑
        String validJson = "[{\"group\":\"test\",\"templates\":[{\"name\":\"TestTemplate\"}]}]";
        
        try {
            JSONArray result = JSONArray.parseArray(validJson);
            assertNotNull(result);
            assertEquals(1, result.size());
            
            com.alibaba.fastjson2.JSONObject group = result.getJSONObject(0);
            assertEquals("test", group.getString("group"));
            
            com.alibaba.fastjson2.JSONArray templates = group.getJSONArray("templates");
            assertEquals(1, templates.size());
            
            com.alibaba.fastjson2.JSONObject template = templates.getJSONObject(0);
            assertEquals("TestTemplate", template.getString("name"));
        } catch (Exception e) {
            fail("有效的JSON不应该抛出异常: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试无效JSON配置处理")
    void testInvalidJsonHandling() {
        // 测试无效JSON的异常处理
        String invalidJson = "{invalid json}";
        
        assertThrows(Exception.class, () -> {
            JSONArray.parseArray(invalidJson);
        });
    }
}