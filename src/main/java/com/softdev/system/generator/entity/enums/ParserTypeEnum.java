package com.softdev.system.generator.entity.enums;

import lombok.Getter;

/**
 * 解析类型枚举
 */
@Getter
public enum ParserTypeEnum {
    
    /**
     * SQL解析类型
     */
    SQL("sql", "默认SQL解析"),
    JSON("json", "JSON解析"),
    INSERT_SQL("insert-sql", "INSERT SQL解析"),
    SQL_REGEX("sql-regex", "正则表达式SQL解析"),
    SELECT_SQL("select-sql", "SELECT SQL解析"),
    CREATE_SQL("create-sql", "CREATE SQL解析");

    private final String value;
    private final String description;

    ParserTypeEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public static ParserTypeEnum fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return SQL;
        }
        
        String trimmedValue = value.trim();
        
        // 首先尝试精确匹配枚举值
        for (ParserTypeEnum type : ParserTypeEnum.values()) {
            if (type.getValue().equals(trimmedValue)) {
                return type;
            }
        }
        
        // 如果精确匹配失败，尝试忽略大小写匹配
        for (ParserTypeEnum type : ParserTypeEnum.values()) {
            if (type.getValue().equalsIgnoreCase(trimmedValue)) {
                return type;
            }
        }
        
        // 尝试匹配枚举名称
        for (ParserTypeEnum type : ParserTypeEnum.values()) {
            if (type.name().equalsIgnoreCase(trimmedValue)) {
                return type;
            }
        }
        
        // 默认返回SQL类型
        return SQL;
    }
}