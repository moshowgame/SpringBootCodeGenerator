package com.softdev.system.generator.service.parser;

import com.softdev.system.generator.entity.dto.ClassInfo;
import com.softdev.system.generator.entity.dto.ParamInfo;
import com.softdev.system.generator.service.impl.parser.JsonParserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonParserService单元测试
 *
 * @author zhengkai.blog.csdn.net
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JsonParserService测试")
class JsonParserServiceTest {

    @InjectMocks
    private JsonParserServiceImpl jsonParserService;

    private ParamInfo paramInfo;
    private ParamInfo complexJsonParamInfo;
    private ParamInfo emptyJsonParamInfo;

    @BeforeEach
    void setUp() {
        // 简单JSON - 使用tableSql字段存储JSON
        paramInfo = new ParamInfo();
        paramInfo.setTableSql("{\"id\": 1, \"name\": \"张三\", \"age\": 25}");
        paramInfo.setOptions(new HashMap<>());

        // 复杂嵌套JSON
        complexJsonParamInfo = new ParamInfo();
        complexJsonParamInfo.setTableSql("{\n" +
                "  \"id\": 1,\n" +
                "  \"name\": \"张三\",\n" +
                "  \"profile\": {\n" +
                "    \"email\": \"zhangsan@example.com\",\n" +
                "    \"phone\": \"13800138000\"\n" +
                "  },\n" +
                "  \"tags\": [\"tag1\", \"tag2\"],\n" +
                "  \"isActive\": true,\n" +
                "  \"score\": 95.5\n" +
                "}");
        complexJsonParamInfo.setOptions(new HashMap<>());

        // 空JSON
        emptyJsonParamInfo = new ParamInfo();
        emptyJsonParamInfo.setTableSql("{}");
        emptyJsonParamInfo.setOptions(new HashMap<>());
    }

    @Test
    @DisplayName("测试解析简单JSON")
    void testProcessSimpleJsonToClassInfo() {
        // When
        ClassInfo result = jsonParserService.processJsonToClassInfo(paramInfo);

        // Then
        assertNotNull(result);
        assertNotNull(result.getTableName());
        assertNotNull(result.getFieldList());
        assertTrue(result.getFieldList().size() > 0);
        
        // 验证字段解析
        boolean hasId = result.getFieldList().stream()
                .anyMatch(field -> "id".equals(field.getFieldName()));
        boolean hasName = result.getFieldList().stream()
                .anyMatch(field -> "name".equals(field.getFieldName()));
        boolean hasAge = result.getFieldList().stream()
                .anyMatch(field -> "age".equals(field.getFieldName()));
        
        assertTrue(hasId);
        assertTrue(hasName);
        assertTrue(hasAge);
    }

    @Test
    @DisplayName("测试解析复杂嵌套JSON")
    void testProcessComplexJsonToClassInfo() {
        // When
        ClassInfo result = jsonParserService.processJsonToClassInfo(complexJsonParamInfo);

        // Then
        assertNotNull(result);
        assertNotNull(result.getTableName());
        assertNotNull(result.getFieldList());
        assertTrue(result.getFieldList().size() > 0);
        
        // 验证顶层字段解析
        boolean hasId = result.getFieldList().stream()
                .anyMatch(field -> "id".equals(field.getFieldName()));
        boolean hasName = result.getFieldList().stream()
                .anyMatch(field -> "name".equals(field.getFieldName()));
        boolean hasIsActive = result.getFieldList().stream()
                .anyMatch(field -> "isActive".equals(field.getFieldName()));
        boolean hasScore = result.getFieldList().stream()
                .anyMatch(field -> "score".equals(field.getFieldName()));
        
        assertTrue(hasId);
        assertTrue(hasName);
        assertTrue(hasIsActive);
        assertTrue(hasScore);
    }

    @Test
    @DisplayName("测试解析空JSON")
    void testProcessEmptyJsonToClassInfo() {
        // When
        ClassInfo result = jsonParserService.processJsonToClassInfo(emptyJsonParamInfo);

        // Then
        assertNotNull(result);
        assertNotNull(result.getTableName());
        assertNotNull(result.getFieldList());
        // 空JSON应该没有字段
        assertEquals(0, result.getFieldList().size());
    }

    @Test
    @DisplayName("测试null JSON字符串")
    void testProcessNullJsonToClassInfo() {
        // Given
        paramInfo.setTableSql(null);

        // When
        ClassInfo result = jsonParserService.processJsonToClassInfo(paramInfo);

        // Then
        assertNotNull(result);
        assertNotNull(result.getTableName());
        assertNotNull(result.getFieldList());
    }

    @Test
    @DisplayName("测试空字符串JSON")
    void testProcessEmptyStringJsonToClassInfo() {
        // Given
        paramInfo.setTableSql("");

        // When
        ClassInfo result = jsonParserService.processJsonToClassInfo(paramInfo);

        // Then
        assertNotNull(result);
        assertNotNull(result.getTableName());
        assertNotNull(result.getFieldList());
    }

    @Test
    @DisplayName("测试无效JSON格式")
    void testProcessInvalidJsonToClassInfo() {
        // Given
        paramInfo.setTableSql("{invalid json}");

        // When
        ClassInfo result = jsonParserService.processJsonToClassInfo(paramInfo);

        // Then - 应该能处理无效JSON但不抛异常
        assertNotNull(result);
        assertNotNull(result.getTableName());
        assertNotNull(result.getFieldList());
    }

    @Test
    @DisplayName("测试JSON数组")
    void testProcessJsonArrayToClassInfo() {
        // Given
        paramInfo.setTableSql("[{\"id\": 1, \"name\": \"张三\"}, {\"id\": 2, \"name\": \"李四\"}]");

        // When
        ClassInfo result = jsonParserService.processJsonToClassInfo(paramInfo);

        // Then
        assertNotNull(result);
        assertNotNull(result.getTableName());
        assertNotNull(result.getFieldList());
        // JSON数组应该能解析第一个元素的字段
        assertTrue(result.getFieldList().size() > 0);
    }

    @Test
    @DisplayName("测试不同数据类型字段")
    void testProcessDifferentDataTypesJsonToClassInfo() {
        // Given
        paramInfo.setTableSql("{\n" +
                "  \"stringValue\": \"hello\",\n" +
                "  \"intValue\": 123,\n" +
                "  \"longValue\": 123456789012345,\n" +
                "  \"doubleValue\": 123.45,\n" +
                "  \"booleanValue\": true,\n" +
                "  \"nullValue\": null\n" +
                "}");

        // When
        ClassInfo result = jsonParserService.processJsonToClassInfo(paramInfo);

        // Then
        assertNotNull(result);
        assertNotNull(result.getTableName());
        assertNotNull(result.getFieldList());
        assertTrue(result.getFieldList().size() > 0);
        
        // 验证所有字段都被解析
        boolean hasString = result.getFieldList().stream()
                .anyMatch(field -> "stringValue".equals(field.getFieldName()));
        boolean hasInt = result.getFieldList().stream()
                .anyMatch(field -> "intValue".equals(field.getFieldName()));
        boolean hasLong = result.getFieldList().stream()
                .anyMatch(field -> "longValue".equals(field.getFieldName()));
        boolean hasDouble = result.getFieldList().stream()
                .anyMatch(field -> "doubleValue".equals(field.getFieldName()));
        boolean hasBoolean = result.getFieldList().stream()
                .anyMatch(field -> "booleanValue".equals(field.getFieldName()));
        boolean hasNull = result.getFieldList().stream()
                .anyMatch(field -> "nullValue".equals(field.getFieldName()));
        
        assertTrue(hasString);
        assertTrue(hasInt);
        assertTrue(hasLong);
        assertTrue(hasDouble);
        assertTrue(hasBoolean);
        assertTrue(hasNull);
    }
}