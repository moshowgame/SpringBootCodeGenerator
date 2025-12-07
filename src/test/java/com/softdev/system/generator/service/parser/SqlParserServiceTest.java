package com.softdev.system.generator.service.parser;

import com.softdev.system.generator.entity.dto.ClassInfo;
import com.softdev.system.generator.entity.dto.ParamInfo;
import com.softdev.system.generator.service.impl.parser.SqlParserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SqlParserService单元测试
 *
 * @author zhengkai.blog.csdn.net
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SqlParserService测试")
class SqlParserServiceTest {

    @InjectMocks
    private SqlParserServiceImpl sqlParserService;

    private ParamInfo paramInfo;
    private ParamInfo createTableParamInfo;
    private ParamInfo insertTableParamInfo;

    @BeforeEach
    void setUp() {
        // 创建测试参数
        paramInfo = new ParamInfo();
        paramInfo.setTableSql("SELECT id, name, age FROM users WHERE status = 'active'");
        paramInfo.setOptions(new HashMap<>());

        createTableParamInfo = new ParamInfo();
        createTableParamInfo.setTableSql("CREATE TABLE users (\n" +
                "    id BIGINT PRIMARY KEY AUTO_INCREMENT,\n" +
                "    name VARCHAR(100) NOT NULL COMMENT '用户名',\n" +
                "    age INT DEFAULT 0 COMMENT '年龄',\n" +
                "    email VARCHAR(255) UNIQUE,\n" +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n" +
                ");");
        createTableParamInfo.setOptions(new HashMap<>());

        insertTableParamInfo = new ParamInfo();
        insertTableParamInfo.setTableSql("INSERT INTO users (id, name, age, email) VALUES (1, '张三', 25, 'zhangsan@example.com')");
        insertTableParamInfo.setOptions(new HashMap<>());
    }

    @Test
    @DisplayName("测试解析Select SQL")
    void testGenerateSelectSqlBySQLPraser() throws Exception {
        // When
        ClassInfo result = sqlParserService.generateSelectSqlBySQLPraser(paramInfo);

        // Then
        assertNotNull(result);
        assertNotNull(result.getTableName());
        assertNotNull(result.getFieldList());
        assertTrue(result.getFieldList().size() > 0);
    }

    @Test
    @DisplayName("测试解析Create SQL")
    void testGenerateCreateSqlBySQLPraser() throws Exception {
        // When
        ClassInfo result = sqlParserService.generateCreateSqlBySQLPraser(createTableParamInfo);

        // Then
        assertNotNull(result);
        assertNotNull(result.getTableName());
        assertNotNull(result.getFieldList());
        assertTrue(result.getFieldList().size() > 0);
    }

    @Test
    @DisplayName("测试处理表结构到类信息")
    void testProcessTableIntoClassInfo() throws Exception {
        // When
        ClassInfo result = sqlParserService.processTableIntoClassInfo(createTableParamInfo);

        // Then
        assertNotNull(result);
        assertNotNull(result.getTableName());
        assertNotNull(result.getFieldList());
        assertTrue(result.getFieldList().size() > 0);
    }

    @Test
    @DisplayName("测试正则表达式解析表结构")
    void testProcessTableToClassInfoByRegex() {
        // When
        ClassInfo result = sqlParserService.processTableToClassInfoByRegex(createTableParamInfo);

        // Then
        assertNotNull(result);
        assertNotNull(result.getTableName());
        assertNotNull(result.getFieldList());
        // 正则表达式版本可能解析不如JSqlParser精确，但应该能解析出基本字段
    }

    @Test
    @DisplayName("测试解析Insert SQL")
    void testProcessInsertSqlToClassInfo() {
        // When
        ClassInfo result = sqlParserService.processInsertSqlToClassInfo(insertTableParamInfo);

        // Then
        assertNotNull(result);
        assertNotNull(result.getTableName());
        assertNotNull(result.getFieldList());
        assertTrue(result.getFieldList().size() > 0);
    }

    @Test
    @DisplayName("测试空SQL字符串")
    void testEmptySqlString() throws Exception {
        // Given
        paramInfo.setTableSql("");

        // When & Then
        assertThrows(Exception.class, () -> {
            sqlParserService.generateSelectSqlBySQLPraser(paramInfo);
        });
    }

    @Test
    @DisplayName("测试null SQL字符串")
    void testNullSqlString() throws Exception {
        // Given
        paramInfo.setTableSql(null);

        // When & Then
        assertThrows(Exception.class, () -> {
            sqlParserService.generateSelectSqlBySQLPraser(paramInfo);
        });
    }

    @Test
    @DisplayName("测试无效SQL语法")
    void testInvalidSqlSyntax() throws Exception {
        // Given
        paramInfo.setTableSql("INVALID SQL SYNTAX");

        // When & Then
        assertThrows(Exception.class, () -> {
            sqlParserService.generateSelectSqlBySQLPraser(paramInfo);
        });
    }

    @Test
    @DisplayName("测试复杂Select SQL")
    void testComplexSelectSql() throws Exception {
        // Given
        paramInfo.setTableSql("SELECT u.id, u.name, p.title FROM users u " +
                "LEFT JOIN profiles p ON u.id = p.user_id " +
                "WHERE u.status = 'active' AND p.is_verified = true " +
                "ORDER BY u.created_at DESC " +
                "LIMIT 10");

        // When
        ClassInfo result = sqlParserService.generateSelectSqlBySQLPraser(paramInfo);

        // Then
        assertNotNull(result);
        assertNotNull(result.getTableName());
        assertNotNull(result.getFieldList());
        assertTrue(result.getFieldList().size() > 0);
    }

    @Test
    @DisplayName("测试带别名的Select SQL")
    void testSelectSqlWithAliases() throws Exception {
        // Given
        paramInfo.setTableSql("SELECT id AS user_id, name AS user_name FROM users AS u");

        // When
        ClassInfo result = sqlParserService.generateSelectSqlBySQLPraser(paramInfo);

        // Then
        assertNotNull(result);
        assertNotNull(result.getTableName());
        assertNotNull(result.getFieldList());
        assertTrue(result.getFieldList().size() > 0);
    }

    @Test
    @DisplayName("测试Insert SQL解析正则表达式")
    void testInsertSqlRegexParsing() {
        // Given
        insertTableParamInfo.setTableSql("INSERT INTO `users` (`id`, `name`, `age`) VALUES (1, '测试', 25)");

        // When
        ClassInfo result = sqlParserService.processInsertSqlToClassInfo(insertTableParamInfo);

        // Then
        assertNotNull(result);
        assertEquals("users", result.getTableName());
        assertNotNull(result.getFieldList());
        assertTrue(result.getFieldList().size() > 0);
    }
}