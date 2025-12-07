package com.softdev.system.generator.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FieldInfoTest {

    private FieldInfo fieldInfo;

    @BeforeEach
    void setUp() {
        fieldInfo = new FieldInfo();
        fieldInfo.setFieldName("testField");
        fieldInfo.setColumnName("test_column");
        fieldInfo.setFieldClass("String");
        fieldInfo.setFieldComment("Test field comment");
        fieldInfo.setSwaggerClass("string");
    }

    @Test
    void testGetFieldName() {
        assertEquals("testField", fieldInfo.getFieldName());
    }

    @Test
    void testGetColumnName() {
        assertEquals("test_column", fieldInfo.getColumnName());
    }

    @Test
    void testGetFieldClass() {
        assertEquals("String", fieldInfo.getFieldClass());
    }

    @Test
    void testGetFieldComment() {
        assertEquals("Test field comment", fieldInfo.getFieldComment());
    }

    @Test
    void testGetSwaggerClass() {
        assertEquals("string", fieldInfo.getSwaggerClass());
    }

    @Test
    void testSetters() {
        FieldInfo newFieldInfo = new FieldInfo();
        newFieldInfo.setFieldName("newField");
        newFieldInfo.setColumnName("new_column");
        newFieldInfo.setFieldClass("Integer");
        newFieldInfo.setFieldComment("New field comment");
        newFieldInfo.setSwaggerClass("integer");

        assertEquals("newField", newFieldInfo.getFieldName());
        assertEquals("new_column", newFieldInfo.getColumnName());
        assertEquals("Integer", newFieldInfo.getFieldClass());
        assertEquals("New field comment", newFieldInfo.getFieldComment());
        assertEquals("integer", newFieldInfo.getSwaggerClass());
    }
}