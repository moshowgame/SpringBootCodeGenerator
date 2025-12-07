package com.softdev.system.generator.vo;

import com.softdev.system.generator.entity.vo.ResultVo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResultVoTest {

    @Test
    void testDefaultConstructor() {
        ResultVo resultVo = new ResultVo();
        assertEquals(200, resultVo.get("code"));
        assertEquals("success", resultVo.get("msg"));
        assertFalse(resultVo.containsKey("data"));
    }

    @Test
    void testOkStaticMethod() {
        ResultVo resultVo = ResultVo.ok();
        assertEquals(200, resultVo.get("code"));
        assertEquals("success", resultVo.get("msg"));
        assertFalse(resultVo.containsKey("data"));
    }

    @Test
    void testOkWithData() {
        String testData = "test data";
        ResultVo resultVo = ResultVo.ok(testData);
        assertEquals(200, resultVo.get("code"));
        assertEquals("success", resultVo.get("msg"));
        assertEquals(testData, resultVo.get("data"));
    }

    @Test
    void testError() {
        String errorMsg = "error message";
        ResultVo resultVo = ResultVo.error(errorMsg);
        assertEquals(500, resultVo.get("code"));
        assertEquals(errorMsg, resultVo.get("msg"));
        assertFalse(resultVo.containsKey("data"));
    }

    @Test
    void testErrorWithCode() {
        String errorMsg = "error message";
        int errorCode = 400;
        ResultVo resultVo = ResultVo.error(errorCode, errorMsg);
        assertEquals(errorCode, resultVo.get("code"));
        assertEquals(errorMsg, resultVo.get("msg"));
        assertFalse(resultVo.containsKey("data"));
    }

    @Test
    void testPut() {
        ResultVo resultVo = new ResultVo();
        resultVo.put("key1", "value1");
        resultVo.put("key2", 123);

        assertEquals("value1", resultVo.get("key1"));
        assertEquals(123, resultVo.get("key2"));
    }

    @Test
    void testPutChaining() {
        ResultVo resultVo = new ResultVo()
                .put("key1", "value1")
                .put("key2", 123);

        assertEquals("value1", resultVo.get("key1"));
        assertEquals(123, resultVo.get("key2"));
        assertEquals(200, resultVo.get("code"));
        assertEquals("success", resultVo.get("msg"));
    }



    @Test
    void testSize() {
        ResultVo resultVo = new ResultVo();
        assertEquals(2, resultVo.size()); // code and msg

        resultVo.put("key1", "value1");
        assertEquals(3, resultVo.size());

        resultVo.put("key2", 123);
        assertEquals(4, resultVo.size());
    }

    @Test
    void testContainsKey() {
        ResultVo resultVo = new ResultVo();
        assertTrue(resultVo.containsKey("code"));
        assertTrue(resultVo.containsKey("msg"));
        assertFalse(resultVo.containsKey("data"));

        resultVo.put("custom", "value");
        assertTrue(resultVo.containsKey("custom"));
    }

    @Test
    void testRemove() {
        ResultVo resultVo = new ResultVo();
        resultVo.put("temp", "value");
        assertTrue(resultVo.containsKey("temp"));

        resultVo.remove("temp");
        assertFalse(resultVo.containsKey("temp"));
    }

    @Test
    void testClear() {
        ResultVo resultVo = new ResultVo();
        resultVo.put("key1", "value1");
        resultVo.put("key2", "value2");
        assertTrue(resultVo.size() > 2);

        resultVo.clear();
        assertEquals(0, resultVo.size());
        assertFalse(resultVo.containsKey("code"));
        assertFalse(resultVo.containsKey("msg"));
    }
}