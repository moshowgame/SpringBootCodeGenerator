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
        for (ParserTypeEnum type : ParserTypeEnum.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        // 默认返回SQL类型
        return SQL;
    }
}