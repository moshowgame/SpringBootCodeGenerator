package com.softdev.system.generator.util;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

public class StringUtilsPlusTest {

    @Test
    public void toLowerCamel() {
        System.out.println(StringUtilsPlus.toLowerCamel("hello_world"));
        System.out.println(StringUtilsPlus.toLowerCamel("HELLO_WO-RLD-IK"));
        System.out.println(StringUtilsPlus.toLowerCamel("HELLO_WORLD-IKabc"));
        System.out.println(StringUtilsPlus.toLowerCamel("HELLO-WORLD-IKabc"));
        System.out.println(StringUtilsPlus.toLowerCamel("HELLO-123WORLD-IKabc"));
        System.out.println(StringUtilsPlus.toLowerCamel("helloWorldOKla"));
        assertEquals("helloWorldChina", StringUtilsPlus.toLowerCamel("hello_-_world-cHina"));
    }

    @Test
    public void upperCaseFirstShouldReturnStringWithFirstLetterCapitalized() {
        assertEquals("Hello", StringUtilsPlus.upperCaseFirst("hello"));
    }

    @Test
    public void upperCaseFirstShouldReturnEmptyStringWhenInputIsEmpty() {
        assertEquals("", StringUtilsPlus.upperCaseFirst(""));
    }

    @Test
    public void lowerCaseFirstShouldReturnStringWithFirstLetterLowercased() {
        assertEquals("hello", StringUtilsPlus.lowerCaseFirst("Hello"));
    }

    @Test
    public void lowerCaseFirstShouldReturnEmptyStringWhenInputIsEmpty() {
        assertEquals("", StringUtilsPlus.lowerCaseFirst(""));
    }

    @Test
    public void underlineToCamelCaseShouldReturnCamelCaseString() {
        assertEquals("helloWorld", StringUtilsPlus.underlineToCamelCase("hello_world"));
    }

    @Test
    public void underlineToCamelCaseShouldReturnEmptyStringWhenInputIsEmpty() {
        assertEquals("", StringUtilsPlus.underlineToCamelCase(""));
    }

    @Test
    public void toUnderlineShouldReturnUnderlinedString() {
        assertEquals("hello_world", StringUtilsPlus.toUnderline("helloWorld", false));
    }

    @Test
    public void toUnderlineShouldReturnEmptyStringWhenInputIsEmpty() {
        assertEquals("", StringUtilsPlus.toUnderline("", false));
    }

    @Test
    public void toCamelShouldReturnCamelCaseString() {
        assertEquals("helloWorld", StringUtilsPlus.toLowerCamel("hello_world"));
    }

    @Test
    public void toCamelShouldReturnEmptyStringWhenInputIsEmpty() {
        assertEquals("", StringUtilsPlus.toLowerCamel(""));
    }

    @Test
    public void isNotNullShouldReturnTrueWhenStringIsNotEmpty() {
        assertTrue(StringUtilsPlus.isNotNull("hello"));
    }

    @Test
    public void isNotNullShouldReturnFalseWhenStringIsEmpty() {
        assertFalse(StringUtilsPlus.isNotNull(""));
    }


    public static void main(String[] args) {
        // String updateTime = StringUtils.underlineToCamelCase("updateTime");
        // System.out.println(updateTime);


        // System.out.println(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, "userName"));
        // System.out.println(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, "userNAme-UUU"));

        System.out.println(StringUtilsPlus.toUnderline("userName",false));
        System.out.println(StringUtilsPlus.toUnderline("UserName",false));
        System.out.println(StringUtilsPlus.toUnderline("user_NameGgg_x-UUU",false));
        System.out.println(StringUtilsPlus.toUnderline("username",false));

        System.out.println(StringUtilsPlus.toUnderline("userName",true));
        System.out.println(StringUtilsPlus.toUnderline("UserName",true));
        System.out.println(StringUtilsPlus.toUnderline("user_NameGgg_x-UUU",true));
        System.out.println(StringUtilsPlus.toUnderline("username",true));

        System.out.println(StringUtilsPlus.underlineToCamelCase("CREATE_TIME"));
    }

}
