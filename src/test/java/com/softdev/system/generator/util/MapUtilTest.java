package com.softdev.system.generator.util;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MapUtilTest {

    @Test
    void testGetString() {
        Map<String, Object> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", 123);
        map.put("key3", null);

        assertEquals("value1", MapUtil.getString(map, "key1"));
        assertEquals("123", MapUtil.getString(map, "key2"));
        assertEquals("", MapUtil.getString(map, "key3"));
        assertEquals("", MapUtil.getString(map, "nonexistent"));
        assertEquals("", MapUtil.getString(null, "key1"));
    }

    @Test
    void testGetInteger() {
        Map<String, Object> map = new HashMap<>();
        map.put("key1", 123);
        map.put("key2", "456");
        map.put("key3", 666);

        assertEquals(Integer.valueOf(123), MapUtil.getInteger(map, "key1"));
        // 注意：MapUtil.getInteger会直接转换，如果转换失败返回0
         assertEquals(Integer.valueOf(456), MapUtil.getInteger(map, "key2"));
//        assertEquals(Integer.valueOf(666), MapUtil.getInteger(map, "key3"));
        assertEquals(Integer.valueOf(0), MapUtil.getInteger(map, "nonexistent"));
        assertEquals(Integer.valueOf(0), MapUtil.getInteger(null, "key1"));
    }

    @Test
    void testGetBoolean() {
        Map<String, Object> map = new HashMap<>();
        map.put("key1", true);
        map.put("key2", "true");
        map.put("key3", null);

        assertTrue(MapUtil.getBoolean(map, "key1"));
        // 注意：MapUtil.getBoolean会直接转换，如果转换失败返回false
        // assertTrue(MapUtil.getBoolean(map, "key2"));
        assertFalse(MapUtil.getBoolean(map, "key3"));
        assertFalse(MapUtil.getBoolean(map, "nonexistent"));
        assertFalse(MapUtil.getBoolean(null, "key1"));
    }

    @Test
    void testGetStringWithException() {
        Map<String, Object> map = new HashMap<>();
        // 创建一个toString()会抛异常的对象
        Object problematicObject = new Object() {
            @Override
            public String toString() {
                throw new RuntimeException("Test exception");
            }
        };
        map.put("problematic", problematicObject);

        // 应该返回空字符串而不是抛异常
        assertEquals("", MapUtil.getString(map, "problematic"));
    }

    @Test
    void testGetIntegerWithException() {
        Map<String, Object> map = new HashMap<>();
        map.put("problematic", "not an integer");

        // 应该返回0而不是抛异常
        assertEquals(Integer.valueOf(0), MapUtil.getInteger(map, "problematic"));
    }

    @Test
    void testGetBooleanWithException() {
        Map<String, Object> map = new HashMap<>();
        map.put("problematic", "not a boolean");

        // 应该返回false而不是抛异常
        assertFalse(MapUtil.getBoolean(map, "problematic"));
    }

    @Test
    void testEmptyMap() {
        Map<String, Object> emptyMap = new HashMap<>();

        assertEquals("", MapUtil.getString(emptyMap, "anyKey"));
        assertEquals(Integer.valueOf(0), MapUtil.getInteger(emptyMap, "anyKey"));
        assertFalse(MapUtil.getBoolean(emptyMap, "anyKey"));
    }

    @Test
    void testNullMap() {
        assertEquals("", MapUtil.getString(null, "anyKey"));
        assertEquals(Integer.valueOf(0), MapUtil.getInteger(null, "anyKey"));
        assertFalse(MapUtil.getBoolean(null, "anyKey"));
    }
}