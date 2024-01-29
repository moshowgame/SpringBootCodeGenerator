package com.softdev.system.generator.util;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class StringUtilsTest {

    @Test
    public void toLowerCamel() {
        System.out.println(StringUtils.toLowerCamel("hello_world"));
        System.out.println(StringUtils.toLowerCamel("HELLO_WO-RLD-IK"));
        System.out.println(StringUtils.toLowerCamel("HELLO_WORLD-IKabc"));
        System.out.println(StringUtils.toLowerCamel("HELLO-WORLD-IKabc"));
        System.out.println(StringUtils.toLowerCamel("HELLO-123WORLD-IKabc"));
        System.out.println(StringUtils.toLowerCamel("helloWorldOKla"));
        assertEquals("helloWorldChina", StringUtils.toLowerCamel("hello_-_world-cHina"));
    }

    @Test
    public void upperCaseFirstShouldReturnStringWithFirstLetterCapitalized() {
        assertEquals("Hello", StringUtils.upperCaseFirst("hello"));
    }

    @Test
    public void upperCaseFirstShouldReturnEmptyStringWhenInputIsEmpty() {
        assertEquals("", StringUtils.upperCaseFirst(""));
    }

    @Test
    public void lowerCaseFirstShouldReturnStringWithFirstLetterLowercased() {
        assertEquals("hello", StringUtils.lowerCaseFirst("Hello"));
    }

    @Test
    public void lowerCaseFirstShouldReturnEmptyStringWhenInputIsEmpty() {
        assertEquals("", StringUtils.lowerCaseFirst(""));
    }

    @Test
    public void underlineToCamelCaseShouldReturnCamelCaseString() {
        assertEquals("helloWorld", StringUtils.underlineToCamelCase("hello_world"));
    }

    @Test
    public void underlineToCamelCaseShouldReturnEmptyStringWhenInputIsEmpty() {
        assertEquals("", StringUtils.underlineToCamelCase(""));
    }

    @Test
    public void toUnderlineShouldReturnUnderlinedString() {
        assertEquals("hello_world", StringUtils.toUnderline("helloWorld", false));
    }

    @Test
    public void toUnderlineShouldReturnEmptyStringWhenInputIsEmpty() {
        assertEquals("", StringUtils.toUnderline("", false));
    }

    @Test
    public void toCamelShouldReturnCamelCaseString() {
        assertEquals("helloWorld", StringUtils.toLowerCamel("hello_world"));
    }

    @Test
    public void toCamelShouldReturnEmptyStringWhenInputIsEmpty() {
        assertEquals("", StringUtils.toLowerCamel(""));
    }

    @Test
    public void isNotNullShouldReturnTrueWhenStringIsNotEmpty() {
        assertTrue(StringUtils.isNotNull("hello"));
    }

    @Test
    public void isNotNullShouldReturnFalseWhenStringIsEmpty() {
        assertFalse(StringUtils.isNotNull(""));
    }
}
