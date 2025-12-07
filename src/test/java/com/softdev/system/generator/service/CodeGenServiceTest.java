package com.softdev.system.generator.service;

import com.alibaba.fastjson2.JSONArray;
import com.softdev.system.generator.entity.dto.ClassInfo;
import com.softdev.system.generator.entity.dto.ParamInfo;
import com.softdev.system.generator.entity.enums.ParserTypeEnum;
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

    private void setupSqlTestData() {
        paramInfo.setTableSql("""
                CREATE TABLE 'sys_user_info' (
                  'user_id' int(11) NOT NULL AUTO_INCREMENT COMMENT '用户编号',
                  'user_name' varchar(255) NOT NULL COMMENT '用户名',
                  'status' tinyint(1) NOT NULL COMMENT '状态',
                  'create_time' datetime NOT NULL COMMENT '创建时间',
                  PRIMARY KEY ('user_id')
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户信息'
                """);
        paramInfo.setOptions(new HashMap<>());
        paramInfo.getOptions().put("dataType", "sql");
    }

    private void setupJsonTestData() {
        paramInfo.setTableSql("""
                {
                  "user_id": "int",
                  "user_name":"用户名",
                  "status": "状态",
                  "create_time":"创建时间"
                }
                """);
        paramInfo.setOptions(new HashMap<>());
        paramInfo.getOptions().put("dataType", "json");
    }

    private void setupInsertSqlTestData() {
        paramInfo.setTableSql("""
                INSERT INTO sys_user_info (user_id, user_name, status, create_time) 
                VALUES (1, 'admin', 1, '2023-12-07 10:00:00')
                """);
        paramInfo.setOptions(new HashMap<>());
        paramInfo.getOptions().put("dataType", "insert-sql");
    }

    @Test
    @DisplayName("测试SQL类型生成代码成功")
    void testGenerateCodeSuccessWithSql() throws Exception {
        // Given
        setupSqlTestData();
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
    @DisplayName("测试SQL类型表结构信息为空时返回错误")
    void testGenerateCodeWithEmptyTableSql() {
        // Given
        setupSqlTestData();
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
    @DisplayName("测试SQL类型表结构信息为null时返回错误")
    void testGenerateCodeWithNullTableSql() {
        // Given
        setupSqlTestData();
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
    @DisplayName("测试SQL类型生成代码异常处理")
    void testGenerateCodeWithSqlException() throws Exception {
        // Given
        setupSqlTestData();
        when(sqlParserService.processTableIntoClassInfo(any(ParamInfo.class)))
                .thenThrow(new RuntimeException("SQL解析异常"));

        // When
        ResultVo result = codeGenService.generateCode(paramInfo);

        // Then
        assertNotNull(result);
        assertEquals(500, result.get("code"));
        assertTrue(result.get("msg").toString().contains("代码生成失败"));
    }

    @Test
    @DisplayName("测试JSON类型表结构信息为空时返回错误")
    void testGenerateCodeJsonWithEmptyTableSql() {
        // Given
        setupJsonTestData();
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
    @DisplayName("测试INSERT_SQL类型表结构信息为空时返回错误")
    void testGenerateCodeInsertSqlWithEmptyTableSql() {
        // Given
        setupInsertSqlTestData();
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
    @DisplayName("测试ParserTypeEnum解析")
    void testParserTypeEnum() {
        // 验证枚举解析行为
        assertEquals(ParserTypeEnum.SQL, ParserTypeEnum.fromValue("SQL"));
        assertEquals(ParserTypeEnum.SQL, ParserTypeEnum.fromValue("sql"));
        assertEquals(ParserTypeEnum.JSON, ParserTypeEnum.fromValue("JSON"));
        assertEquals(ParserTypeEnum.JSON, ParserTypeEnum.fromValue("json"));
        assertEquals(ParserTypeEnum.INSERT_SQL, ParserTypeEnum.fromValue("INSERT_SQL"));
        assertEquals(ParserTypeEnum.INSERT_SQL, ParserTypeEnum.fromValue("insert-sql"));
        
        // 测试未知值默认返回SQL
        assertEquals(ParserTypeEnum.SQL, ParserTypeEnum.fromValue("UNKNOWN"));
    }

    @Test
    @DisplayName("测试JSON模式解析")
    void testGenerateCodeWithJsonMode() throws Exception {
        // Given
        setupJsonTestData();
        
        // 验证 dataType 设置是否正确
        assertEquals("json", paramInfo.getOptions().get("dataType"));
        
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
            assertNotNull(result.get("data"));
            verify(jsonParserService).processJsonToClassInfo(paramInfo);
            verify(templateService).getAllTemplates();
        }
    }

    @Test
    @DisplayName("测试JSON类型解析异常处理")
    void testGenerateCodeWithJsonException() throws Exception {
        // Given
        setupJsonTestData();
        when(jsonParserService.processJsonToClassInfo(any(ParamInfo.class)))
                .thenThrow(new RuntimeException("JSON解析异常"));

        // When
        ResultVo result = codeGenService.generateCode(paramInfo);

        // Then
        assertNotNull(result);
        assertEquals(500, result.get("code"));
        assertTrue(result.get("msg").toString().contains("代码生成失败"));
    }

    @Test
    @DisplayName("测试INSERT SQL模式解析")
    void testGenerateCodeWithInsertSqlMode() throws Exception {
        // Given
        setupInsertSqlTestData();
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
            assertNotNull(result.get("data"));
            verify(sqlParserService).processInsertSqlToClassInfo(paramInfo);
            verify(templateService).getAllTemplates();
        }
    }

    @Test
    @DisplayName("测试INSERT SQL类型解析异常处理")
    void testGenerateCodeWithInsertSqlException() throws Exception {
        // Given
        setupInsertSqlTestData();
        when(sqlParserService.processInsertSqlToClassInfo(any(ParamInfo.class)))
                .thenThrow(new RuntimeException("INSERT SQL解析异常"));

        // When
        ResultVo result = codeGenService.generateCode(paramInfo);

        // Then
        assertNotNull(result);
        assertEquals(500, result.get("code"));
        assertTrue(result.get("msg").toString().contains("代码生成失败"));
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

    @Test
    @DisplayName("测试复杂SQL表结构解析")
    void testGenerateCodeWithComplexSql() throws Exception {
        // Given
        paramInfo.setTableSql("""
                CREATE TABLE `complex_table` (
                  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                  `user_name` varchar(50) NOT NULL DEFAULT '' COMMENT '用户名',
                  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
                  `age` int(3) DEFAULT '0' COMMENT '年龄',
                  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
                  `price` decimal(10,2) DEFAULT '0.00' COMMENT '价格',
                  `description` text COMMENT '描述',
                  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                  PRIMARY KEY (`id`),
                  UNIQUE KEY `uk_user_name` (`user_name`),
                  KEY `idx_email` (`email`),
                  KEY `idx_status` (`status`)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='复杂表结构示例'
                """);
        paramInfo.setOptions(new HashMap<>());
        paramInfo.getOptions().put("dataType", "sql");
        
        when(sqlParserService.processTableIntoClassInfo(any(ParamInfo.class))).thenReturn(classInfo);
        when(templateService.getAllTemplates()).thenReturn(mockTemplates);
        
        try (MockedStatic<FreemarkerUtil> freemarkerMock = mockStatic(FreemarkerUtil.class);
             MockedStatic<MapUtil> mapUtilMock = mockStatic(MapUtil.class)) {
            
            freemarkerMock.when(() -> FreemarkerUtil.processString(anyString(), any(Map.class)))
                    .thenReturn("generated code from complex SQL");
            mapUtilMock.when(() -> MapUtil.getString(any(Map.class), anyString()))
                    .thenReturn("complex_table");

            // When
            ResultVo result = codeGenService.generateCode(paramInfo);

            // Then
            assertNotNull(result);
            assertEquals(200, result.get("code"));
            assertNotNull(result.get("data"));
            verify(sqlParserService).processTableIntoClassInfo(paramInfo);
        }
    }

    @Test
    @DisplayName("测试嵌套JSON结构解析")
    void testGenerateCodeWithNestedJson() throws Exception {
        // Given
        paramInfo.setTableSql("""
                {
                  "user": {
                    "id": {"type": "number", "description": "用户ID"},
                    "profile": {
                      "name": {"type": "string", "description": "姓名"},
                      "contact": {
                        "email": {"type": "string", "format": "email", "description": "邮箱"},
                        "phone": {"type": "string", "description": "电话"}
                      }
                    },
                    "roles": {
                        "type": "array",
                        "items": {"type": "string"},
                        "description": "用户角色列表"
                    }
                  }
                }
                """);
        paramInfo.setOptions(new HashMap<>());
        paramInfo.getOptions().put("dataType", "json");
        
        when(jsonParserService.processJsonToClassInfo(any(ParamInfo.class))).thenReturn(classInfo);
        when(templateService.getAllTemplates()).thenReturn(mockTemplates);
        
        try (MockedStatic<FreemarkerUtil> freemarkerMock = mockStatic(FreemarkerUtil.class);
             MockedStatic<MapUtil> mapUtilMock = mockStatic(MapUtil.class)) {
            
            freemarkerMock.when(() -> FreemarkerUtil.processString(anyString(), any(Map.class)))
                    .thenReturn("generated code from nested JSON");
            mapUtilMock.when(() -> MapUtil.getString(any(Map.class), anyString()))
                    .thenReturn("user_profile");

            // When
            ResultVo result = codeGenService.generateCode(paramInfo);

            // Then
            assertNotNull(result);
            assertEquals(200, result.get("code"));
            assertNotNull(result.get("data"));
            verify(jsonParserService).processJsonToClassInfo(paramInfo);
        }
    }

    @Test
    @DisplayName("测试批量INSERT SQL解析")
    void testGenerateCodeWithBatchInsertSql() throws Exception {
        // Given
        paramInfo.setTableSql("""
                INSERT INTO sys_user_info (user_id, user_name, status, create_time) VALUES 
                (1, 'admin', 1, '2023-12-07 10:00:00'),
                (2, 'user1', 1, '2023-12-07 10:01:00'),
                (3, 'user2', 0, '2023-12-07 10:02:00');
                """);
        paramInfo.setOptions(new HashMap<>());
        paramInfo.getOptions().put("dataType", "insert-sql");
        
        when(sqlParserService.processInsertSqlToClassInfo(any(ParamInfo.class))).thenReturn(classInfo);
        when(templateService.getAllTemplates()).thenReturn(mockTemplates);
        
        try (MockedStatic<FreemarkerUtil> freemarkerMock = mockStatic(FreemarkerUtil.class);
             MockedStatic<MapUtil> mapUtilMock = mockStatic(MapUtil.class)) {
            
            freemarkerMock.when(() -> FreemarkerUtil.processString(anyString(), any(Map.class)))
                    .thenReturn("generated code from batch INSERT SQL");
            mapUtilMock.when(() -> MapUtil.getString(any(Map.class), anyString()))
                    .thenReturn("sys_user_info");

            // When
            ResultVo result = codeGenService.generateCode(paramInfo);

            // Then
            assertNotNull(result);
            assertEquals(200, result.get("code"));
            assertNotNull(result.get("data"));
            verify(sqlParserService).processInsertSqlToClassInfo(paramInfo);
        }
    }

    @Test
    @DisplayName("测试未知数据类型处理")
    void testGenerateCodeWithUnknownDataType() throws Exception {
        // Given
        setupSqlTestData();
        paramInfo.getOptions().put("dataType", "unknown-type");
        
        when(sqlParserService.processTableIntoClassInfo(any(ParamInfo.class))).thenReturn(classInfo);
        when(templateService.getAllTemplates()).thenReturn(mockTemplates);
        
        try (MockedStatic<FreemarkerUtil> freemarkerMock = mockStatic(FreemarkerUtil.class);
             MockedStatic<MapUtil> mapUtilMock = mockStatic(MapUtil.class)) {
            
            freemarkerMock.when(() -> FreemarkerUtil.processString(anyString(), any(Map.class)))
                    .thenReturn("default generated code");
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
    @DisplayName("测试正则表达式SQL解析")
    void testGenerateCodeWithSqlRegex() throws Exception {
        // Given
        paramInfo.setTableSql("""
                CREATE TABLE regex_test (
                    id INT PRIMARY KEY,
                    name VARCHAR(100)
                );
                """);
        paramInfo.setOptions(new HashMap<>());
        paramInfo.getOptions().put("dataType", "sql-regex");
        
        // 添加调试信息
        System.out.println("Test Debug: Setting dataType to: " + paramInfo.getOptions().get("dataType"));
        System.out.println("Test Debug: Expected parser type: SQL_REGEX");
        
        when(sqlParserService.processTableToClassInfoByRegex(any(ParamInfo.class))).thenReturn(classInfo);
        when(templateService.getAllTemplates()).thenReturn(mockTemplates);
        
        try (MockedStatic<FreemarkerUtil> freemarkerMock = mockStatic(FreemarkerUtil.class);
             MockedStatic<MapUtil> mapUtilMock = mockStatic(MapUtil.class)) {
            
            freemarkerMock.when(() -> FreemarkerUtil.processString(anyString(), any(Map.class)))
                    .thenReturn("generated code from regex SQL");
            mapUtilMock.when(() -> MapUtil.getString(any(Map.class), anyString()))
                    .thenReturn("regex_test");

            // When
            ResultVo result = codeGenService.generateCode(paramInfo);

            // Then
            assertNotNull(result);
            assertEquals(200, result.get("code"));
            assertNotNull(result.get("data"));
            verify(sqlParserService).processTableToClassInfoByRegex(paramInfo);
        }
    }

    @Test
    @DisplayName("测试SELECT SQL解析")
    void testGenerateCodeWithSelectSql() throws Exception {
        // Given
        paramInfo.setTableSql("""
                SELECT id, name, email FROM users WHERE status = 1
                """);
        paramInfo.setOptions(new HashMap<>());
        paramInfo.getOptions().put("dataType", "select-sql");
        
        when(sqlParserService.generateSelectSqlBySQLPraser(any(ParamInfo.class))).thenReturn(classInfo);
        when(templateService.getAllTemplates()).thenReturn(mockTemplates);
        
        try (MockedStatic<FreemarkerUtil> freemarkerMock = mockStatic(FreemarkerUtil.class);
             MockedStatic<MapUtil> mapUtilMock = mockStatic(MapUtil.class)) {
            
            freemarkerMock.when(() -> FreemarkerUtil.processString(anyString(), any(Map.class)))
                    .thenReturn("generated code from SELECT SQL");
            mapUtilMock.when(() -> MapUtil.getString(any(Map.class), anyString()))
                    .thenReturn("users");

            // When
            ResultVo result = codeGenService.generateCode(paramInfo);

            // Then
            assertNotNull(result);
            assertEquals(200, result.get("code"));
            assertNotNull(result.get("data"));
            verify(sqlParserService).generateSelectSqlBySQLPraser(paramInfo);
        }
    }
}