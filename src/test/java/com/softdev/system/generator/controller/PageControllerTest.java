package com.softdev.system.generator.controller;

import com.softdev.system.generator.util.ValueUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * PageController单元测试
 *
 * @author zhengkai.blog.csdn.net
 */
@WebMvcTest(PageController.class)
@DisplayName("PageController测试")
class PageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ValueUtil valueUtil;

    @BeforeEach
    void setUp() {
        // 模拟ValueUtil的属性值，避免模板渲染时的null值错误
        when(valueUtil.getTitle()).thenReturn("Test Title");
        when(valueUtil.getHeader()).thenReturn("Test Header");
        when(valueUtil.getVersion()).thenReturn("1.0.0");
        when(valueUtil.getAuthor()).thenReturn("Test Author");
        when(valueUtil.getKeywords()).thenReturn("test,keywords");
        when(valueUtil.getSlogan()).thenReturn("Test Slogan");
        when(valueUtil.getCopyright()).thenReturn("Test Copyright");
        when(valueUtil.getDescription()).thenReturn("Test Description");
        when(valueUtil.getPackageName()).thenReturn("com.test");
        when(valueUtil.getReturnUtilSuccess()).thenReturn("success");
        when(valueUtil.getReturnUtilFailure()).thenReturn("failure");
        when(valueUtil.getOutputStr()).thenReturn("output");
        when(valueUtil.getMode()).thenReturn("test");
    }

    @Test
    @DisplayName("测试默认页面")
    void testDefaultPage() throws Exception {
        // When & Then
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("newui2"))
                .andExpect(model().attributeExists("value"));
    }

    @Test
    @DisplayName("测试首页")
    void testIndexPage() throws Exception {
        // When & Then
        mockMvc.perform(get("/index"))
                .andExpect(status().isOk())
                .andExpect(view().name("newui2"))
                .andExpect(model().attributeExists("value"));
    }
}