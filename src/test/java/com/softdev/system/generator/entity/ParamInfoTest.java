package com.softdev.system.generator.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ParamInfoTest {

    private ParamInfo paramInfo;
    private Map<String, Object> options;

    @BeforeEach
    void setUp() {
        paramInfo = new ParamInfo();
        options = new HashMap<>();
        options.put("key1", "value1");
        options.put("key2", 123);
        paramInfo.setOptions(options);
    }

    @Test
    void testGetTableSql() {
        assertNull(paramInfo.getTableSql());
        String sql = "CREATE TABLE test (id INT)";
        paramInfo.setTableSql(sql);
        assertEquals(sql, paramInfo.getTableSql());
    }

    @Test
    void testGetOptions() {
        assertNotNull(paramInfo.getOptions());
        assertEquals(2, paramInfo.getOptions().size());
        assertEquals("value1", paramInfo.getOptions().get("key1"));
        assertEquals(123, paramInfo.getOptions().get("key2"));
    }

    @Test
    void testSetOptions() {
        Map<String, Object> newOptions = new HashMap<>();
        newOptions.put("newKey", "newValue");
        paramInfo.setOptions(newOptions);
        assertEquals(1, paramInfo.getOptions().size());
        assertEquals("newValue", paramInfo.getOptions().get("newKey"));
    }

    @Test
    void testNameCaseTypeConstants() {
        assertEquals("CamelCase", ParamInfo.NAME_CASE_TYPE.CAMEL_CASE);
        assertEquals("UnderScoreCase", ParamInfo.NAME_CASE_TYPE.UNDER_SCORE_CASE);
        assertEquals("UpperUnderScoreCase", ParamInfo.NAME_CASE_TYPE.UPPER_UNDER_SCORE_CASE);
    }
}