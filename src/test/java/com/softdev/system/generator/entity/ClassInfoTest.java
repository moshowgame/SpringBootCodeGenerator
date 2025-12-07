package com.softdev.system.generator.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClassInfoTest {

    private ClassInfo classInfo;
    private List<FieldInfo> fieldList;

    @BeforeEach
    void setUp() {
        classInfo = new ClassInfo();
        classInfo.setTableName("test_table");
        classInfo.setClassName("TestTable");
        classInfo.setClassComment("Test table comment");
        classInfo.setOriginTableName("origin_test_table");

        fieldList = new ArrayList<>();
        FieldInfo fieldInfo1 = new FieldInfo();
        fieldInfo1.setFieldName("id");
        fieldInfo1.setFieldClass("Integer");
        
        FieldInfo fieldInfo2 = new FieldInfo();
        fieldInfo2.setFieldName("name");
        fieldInfo2.setFieldClass("String");
        
        fieldList.add(fieldInfo1);
        fieldList.add(fieldInfo2);
        classInfo.setFieldList(fieldList);
    }

    @Test
    void testGetTableName() {
        assertEquals("test_table", classInfo.getTableName());
    }

    @Test
    void testGetClassName() {
        assertEquals("TestTable", classInfo.getClassName());
    }

    @Test
    void testGetClassComment() {
        assertEquals("Test table comment", classInfo.getClassComment());
    }

    @Test
    void testGetOriginTableName() {
        assertEquals("origin_test_table", classInfo.getOriginTableName());
    }

    @Test
    void testGetFieldList() {
        assertNotNull(classInfo.getFieldList());
        assertEquals(2, classInfo.getFieldList().size());
        assertEquals("id", classInfo.getFieldList().get(0).getFieldName());
        assertEquals("name", classInfo.getFieldList().get(1).getFieldName());
    }

    @Test
    void testSetters() {
        ClassInfo newClassInfo = new ClassInfo();
        newClassInfo.setTableName("new_table");
        newClassInfo.setClassName("NewTable");
        newClassInfo.setClassComment("New comment");
        newClassInfo.setOriginTableName("new_origin_table");
        newClassInfo.setFieldList(new ArrayList<>());

        assertEquals("new_table", newClassInfo.getTableName());
        assertEquals("NewTable", newClassInfo.getClassName());
        assertEquals("New comment", newClassInfo.getClassComment());
        assertEquals("new_origin_table", newClassInfo.getOriginTableName());
        assertTrue(newClassInfo.getFieldList().isEmpty());
    }
}