package com.softdev.system.generator.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StringUtilsPlusTest {



    @Test
    void testToUnderline() {
        assertNull(StringUtilsPlus.toUnderline(null, false));
        assertEquals("", StringUtilsPlus.toUnderline("", false));
        assertEquals("test_string", StringUtilsPlus.toUnderline("testString", false));
        assertEquals("test_string", StringUtilsPlus.toUnderline("testString", true));
        assertEquals("TEST_STRING", StringUtilsPlus.toUnderline("testString", true));
    }



    @Test
    void testToUnderScoreCase() {
        assertNull(StringUtilsPlus.toUnderline(null, false));
        assertEquals("", StringUtilsPlus.toUnderline("", false));
        assertEquals("test_string", StringUtilsPlus.toUnderline("testString", false));
    }

    @Test
    void testToUpperCaseFirst() {
        assertNull(StringUtilsPlus.upperCaseFirst(null));
        assertEquals("", StringUtilsPlus.upperCaseFirst(""));
        assertEquals("Test", StringUtilsPlus.upperCaseFirst("test"));
        assertEquals("TEST", StringUtilsPlus.upperCaseFirst("TEST"));
    }

    @Test
    void testToLowerCaseFirst() {
        assertNull(StringUtilsPlus.lowerCaseFirst(null));
        assertEquals("", StringUtilsPlus.lowerCaseFirst(""));
        assertEquals("test", StringUtilsPlus.lowerCaseFirst("Test"));
        assertEquals("test", StringUtilsPlus.lowerCaseFirst("TEST"));
    }

    @Test
    void testUnderlineToCamelCase() {
        assertNull(StringUtilsPlus.underlineToCamelCase(null));
        assertEquals("", StringUtilsPlus.underlineToCamelCase(""));
        assertEquals("testString", StringUtilsPlus.underlineToCamelCase("test_string"));
        assertEquals("testString", StringUtilsPlus.underlineToCamelCase("test__string"));
    }

    @Test
    void testIsNotNull() {
        assertFalse(StringUtilsPlus.isNotNull(null));
        assertFalse(StringUtilsPlus.isNotNull(""));
        assertFalse(StringUtilsPlus.isNotNull("   "));
        assertTrue(StringUtilsPlus.isNotNull("test"));
    }

    @Test
    void testToLowerCamel() {
        assertNull(StringUtilsPlus.toLowerCamel(null));
        assertEquals("", StringUtilsPlus.toLowerCamel(""));
        assertEquals("testString", StringUtilsPlus.toLowerCamel("testString"));
        assertEquals("testString", StringUtilsPlus.toLowerCamel("TestString"));
        assertEquals("testString", StringUtilsPlus.toLowerCamel("test_string"));
    }
}