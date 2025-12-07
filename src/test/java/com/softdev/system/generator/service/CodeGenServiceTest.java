package com.softdev.system.generator.service;

import com.alibaba.fastjson2.JSONArray;
import com.softdev.system.generator.entity.dto.ClassInfo;
import com.softdev.system.generator.entity.dto.ParamInfo;
import com.softdev.system.generator.entity.vo.ResultVo;
import com.softdev.system.generator.service.impl.CodeGenServiceImpl;
import com.softdev.system.generator.service.parser.JsonParserService;
import com.softdev.system.generator.service.parser.SqlParserService;
import com.softdev.system.generator.util.FreemarkerUtil;
import com.softdev.system.generator.util.MapUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * CodeGenService单元测试
 *
 * @author zhengkai.blog.csdn.net
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CodeGenService测试")
class CodeGenServiceTest {

    @Mock
    private TemplateService templateService;

    @Mock
    private SqlParserService sqlParserService;

    @Mock
    private JsonParserService jsonParserService;

    @InjectMocks
    private CodeGenServiceImpl codeGenService;

    private ParamInfo paramInfo;
    private ClassInfo classInfo;
    private JSONArray mockTemplates;

    @BeforeEach
    void setUp() {
        paramInfo = new ParamInfo();
        paramInfo.setTableSql("CREATE TABLE test (id INT PRIMARY KEY, name VARCHAR(50));");
        paramInfo.setOptions(new HashMap<>());
        paramInfo.getOptions().put("dataType", "SQL");

        classInfo = new ClassInfo();
        classInfo.setTableName("test");

        // 创建模拟模板配置
        mockTemplates = new JSONArray();
        com.alibaba.fastjson2.JSONObject parentTemplate = new com.alibaba.fastjson2.JSONObject();
        parentTemplate.put("group", "basic");
        
        com.alibaba.fastjson2.JSONArray childTemplates = new com.alibaba.fastjson2.JSONArray();
        com.alibaba.fastjson2.JSONObject childTemplate = new com.alibaba.fastjson2.JSONObject();
        childTemplate.put("name", "Entity");
        childTemplates.add(childTemplate);
        
        parentTemplate.put("templates", childTemplates);
        mockTemplates.add(parentTemplate);
    }

    @Test
    @DisplayName("测试生成代码成功")
    void testGenerateCodeSuccess() throws Exception {
        // Given
        when(sqlParserService.processTableIntoClassInfo(any(ParamInfo.class))).thenReturn(classInfo);
        when(templateService.getAllTemplates()).thenReturn(mockTemplates);
        
        try (MockedStatic<FreemarkerUtil> freemarkerMock = mockStatic(FreemarkerUtil.class);
             MockedStatic<MapUtil> mapUtilMock = mockStatic(MapUtil.class)) {
            
            freemarkerMock.when(() -> FreemarkerUtil.processString(anyString(), any(Map.class)))
                    .thenReturn("generated code");
            mapUtilMock.when(() -> MapUtil.getString(any(Map.class), anyString()))
                    .thenReturn("test");

            // When
            ResultVo result = codeGenService.generateCode(paramInfo);

            // Then
            assertNotNull(result);
            assertEquals(200, result.get("code"));
            assertNotNull(result.get("data"));
            verify(sqlParserService).processTableIntoClassInfo(paramInfo);
            verify(templateService).getAllTemplates();
        }
    }

    @Test
    @DisplayName("测试表结构信息为空时返回错误")
    void testGenerateCodeWithEmptyTableSql() {
        // Given
        paramInfo.setTableSql("");

        // When
        try {
            ResultVo result = codeGenService.generateCode(paramInfo);
            // Then
            assertNotNull(result);
            assertEquals(500, result.get("code"));
            assertEquals("表结构信息为空", result.get("msg"));
        } catch (Exception e) {
            fail("不应该抛出异常: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试表结构信息为null时返回错误")
    void testGenerateCodeWithNullTableSql() {
        // Given
        paramInfo.setTableSql(null);

        // When
        try {
            ResultVo result = codeGenService.generateCode(paramInfo);
            // Then
            assertNotNull(result);
            assertEquals(500, result.get("code"));
            assertEquals("表结构信息为空", result.get("msg"));
        } catch (Exception e) {
            fail("不应该抛出异常: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试生成代码异常处理")
    void testGenerateCodeWithException() throws Exception {
        // Given
        when(sqlParserService.processTableIntoClassInfo(any(ParamInfo.class)))
                .thenThrow(new RuntimeException("解析异常"));

        // When
        ResultVo result = codeGenService.generateCode(paramInfo);

        // Then
        assertNotNull(result);
        assertEquals(500, result.get("code"));
        assertTrue(result.get("msg").toString().contains("代码生成失败"));
    }

    @Test
    @DisplayName("测试JSON模式解析")
    void testGenerateCodeWithJsonMode() throws Exception {
        // Given
        paramInfo.getOptions().put("dataType", "JSON");
        when(jsonParserService.processJsonToClassInfo(any(ParamInfo.class))).thenReturn(classInfo);
        when(templateService.getAllTemplates()).thenReturn(mockTemplates);
        
        try (MockedStatic<FreemarkerUtil> freemarkerMock = mockStatic(FreemarkerUtil.class);
             MockedStatic<MapUtil> mapUtilMock = mockStatic(MapUtil.class)) {
            
            freemarkerMock.when(() -> FreemarkerUtil.processString(anyString(), any(Map.class)))
                    .thenReturn("generated code");
            mapUtilMock.when(() -> MapUtil.getString(any(Map.class), anyString()))
                    .thenReturn("test");

            // When
            ResultVo result = codeGenService.generateCode(paramInfo);

            // Then
            assertNotNull(result);
            assertEquals(200, result.get("code"));
            verify(jsonParserService).processJsonToClassInfo(paramInfo);
        }
    }

    @Test
    @DisplayName("测试INSERT SQL模式解析")
    void testGenerateCodeWithInsertSqlMode() throws Exception {
        // Given
        paramInfo.getOptions().put("dataType", "INSERT_SQL");
        when(sqlParserService.processInsertSqlToClassInfo(any(ParamInfo.class))).thenReturn(classInfo);
        when(templateService.getAllTemplates()).thenReturn(mockTemplates);
        
        try (MockedStatic<FreemarkerUtil> freemarkerMock = mockStatic(FreemarkerUtil.class);
             MockedStatic<MapUtil> mapUtilMock = mockStatic(MapUtil.class)) {
            
            freemarkerMock.when(() -> FreemarkerUtil.processString(anyString(), any(Map.class)))
                    .thenReturn("generated code");
            mapUtilMock.when(() -> MapUtil.getString(any(Map.class), anyString()))
                    .thenReturn("test");

            // When
            ResultVo result = codeGenService.generateCode(paramInfo);

            // Then
            assertNotNull(result);
            assertEquals(200, result.get("code"));
            verify(sqlParserService).processInsertSqlToClassInfo(paramInfo);
        }
    }

    @Test
    @DisplayName("测试根据参数获取结果")
    void testGetResultByParams() throws Exception {
        // Given
        Map<String, Object> params = new HashMap<>();
        params.put("tableName", "test");
        params.put("classInfo", classInfo);

        when(templateService.getAllTemplates()).thenReturn(mockTemplates);
        
        try (MockedStatic<FreemarkerUtil> freemarkerMock = mockStatic(FreemarkerUtil.class);
             MockedStatic<MapUtil> mapUtilMock = mockStatic(MapUtil.class)) {
            
            freemarkerMock.when(() -> FreemarkerUtil.processString(anyString(), any(Map.class)))
                    .thenReturn("generated code");
            mapUtilMock.when(() -> MapUtil.getString(any(Map.class), anyString()))
                    .thenReturn("test");

            // When
            Map<String, String> result = codeGenService.getResultByParams(params);

            // Then
            assertNotNull(result);
            assertEquals("test", result.get("tableName"));
            assertEquals("generated code", result.get("Entity"));
            verify(templateService).getAllTemplates();
        }
    }

    @Test
    @DisplayName("测试根据参数获取结果时模板为空")
    void testGetResultByParamsWithEmptyTemplates() throws Exception {
        // Given
        Map<String, Object> params = new HashMap<>();
        params.put("tableName", "test");
        
        JSONArray emptyTemplates = new JSONArray();
        com.alibaba.fastjson2.JSONObject parentTemplate = new com.alibaba.fastjson2.JSONObject();
        parentTemplate.put("group", "basic");
        parentTemplate.put("templates", new JSONArray());
        emptyTemplates.add(parentTemplate);

        when(templateService.getAllTemplates()).thenReturn(emptyTemplates);

        // When
        Map<String, String> result = codeGenService.getResultByParams(params);

        // Then
        assertNotNull(result);
        assertEquals("test", result.get("tableName"));
        verify(templateService).getAllTemplates();
    }
}