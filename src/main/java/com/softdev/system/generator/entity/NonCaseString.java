package com.softdev.system.generator.entity;

import lombok.AllArgsConstructor;

/**
 * String 包装类
 * <p>
 * 忽略大小写
 **考虑增加这个类是, 如果在 StringUtils 中加工具方法, 使用起来代码非常冗长且不方便
 * @author Nisus
 * @see String
 */
@AllArgsConstructor
public class NonCaseString implements CharSequence {
    private String value;

    public static NonCaseString of(String str) {
        assert str != null;
        return new NonCaseString(str);
    }


    /**
     * {@link String#indexOf(String)} 增强, 忽略大小写
     */
    public int indexOf(String m) {
        String text = this.value;
        if (text == null || m == null || text.length() < m.length()) {
            return -1;
        }
        return text.toLowerCase().indexOf(m.toLowerCase());
    }

    /**
     * {@link String#lastIndexOf(String)} 增强, 忽略大小写
     */
    public int lastIndexOf(String m) {
        String text = this.value;
        if (text == null || m == null || text.length() < m.length()) {
            return -1;
        }
        return text.toLowerCase().lastIndexOf(m.toLowerCase());
    }

    /**
     * 是否包含, 大小写不敏感
     * <pre
     * "abcxyz" 包含 "abc" => true
     * "abcxyz" 包含 "ABC" => true
     * "abcxyz" 包含 "aBC" => true
     * </pre>
     *
     * @param m 被包含字符串
     */
    public boolean contains(String m) {
        String text = this.value;
        if (text.length() < m.length()) {
            return false;
        }
        return text.toLowerCase().contains(m.toLowerCase());
    }

    /**
     * 任意一个包含返回true
     * <pre>
     * containsAny("abcdef", "a", "b)
     * 等价
     * "abcdef".contains("a") || "abcdef".contains("b")
     * </pre>
     *
     * @param matchers 多个要判断的被包含项
     */
    public boolean containsAny(String... matchers) {
        for (String matcher : matchers) {
            if (contains(matcher)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 所有都包含才返回true
     *
     * @param matchers 多个要判断的被包含项
     */
    public boolean containsAllIgnoreCase(String... matchers) {
        for (String matcher : matchers) {
            if (contains(matcher) == false) {
                return false;
            }
        }
        return true;
    }

    public NonCaseString trim() {
        return NonCaseString.of(this.value.trim());
    }

    public NonCaseString replace(char oldChar, char newChar) {
        return NonCaseString.of(this.value.replace(oldChar, newChar));
    }

    public NonCaseString replaceAll(String regex, String replacement) {
        return NonCaseString.of(this.value.replaceAll(regex, replacement));
    }

    public NonCaseString substring(int beginIndex) {
        return NonCaseString.of(this.value.substring(beginIndex));
    }

    public NonCaseString substring(int beginIndex, int endIndex) {
        return NonCaseString.of(this.value.substring(beginIndex, endIndex));
    }

    public boolean isNotEmpty() {
        return !this.value.isEmpty();
    }

    @Override
    public int length() {
        return this.value.length();
    }

    @Override
    public char charAt(int index) {
        return this.value.charAt(index);
    }


    @Override
    public CharSequence subSequence(int start, int end) {
        return this.value.subSequence(start, end);
    }

    public String[] split(String regex) {
        return this.value.split(regex);
    }


    /**
     * @return 原始字符串
     */
    public String get() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
