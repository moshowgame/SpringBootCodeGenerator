package com.softdev.system.generator.util;

/**
 * string tool
 *
 * @author xuxueli 2018-05-02 20:43:25
 */
public class StringUtils {

    /**
     * 首字母大写
     *
     * @param str
     * @return
     */
    public static String upperCaseFirst(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 首字母小写
     *
     * @param str
     * @return
     */
    public static String lowerCaseFirst(String str) {
        //2019-2-10 解决StringUtils.lowerCaseFirst潜在的NPE异常@liutf
        return (str != null && str.length() > 1) ? str.substring(0, 1).toLowerCase() + str.substring(1) : "";
    }

    /**
     * 下划线，转换为驼峰式
     *
     * @param underscoreName
     * @return
     */
    public static String underlineToCamelCase(String underscoreName) {
        StringBuilder result = new StringBuilder();
        if (underscoreName != null && underscoreName.trim().length() > 0) {
            boolean flag = false;
            for (int i = 0; i < underscoreName.length(); i++) {
                char ch = underscoreName.charAt(i);
                if ('_' == ch) {
                    flag = true;
                } else {
                    if (flag) {
                        result.append(Character.toUpperCase(ch));
                        flag = false;
                    } else {
                        result.append(ch);
                    }
                }
            }
        }
        return result.toString();
    }

    /**
     * 转 user_name 风格
     *
     * 不管原始是什么风格
     */
    public static String toUnderline(String str, boolean upperCase) {
        if (str == null || str.trim().isEmpty()) {
            return str;
        }

        StringBuilder result = new StringBuilder();
        boolean preIsUnderscore = false;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == '_') {
                preIsUnderscore = true;
            } else if (ch == '-') {
                ch = '_';
                preIsUnderscore = true; // -A -> _a
            } else if (ch >= 'A' && ch <= 'Z') {
                // A -> _a
                if (!preIsUnderscore && i > 0) { // _A -> _a
                    result.append("_");
                }
                preIsUnderscore = false;
            } else {
                preIsUnderscore = false;
            }
            result.append(upperCase ? Character.toUpperCase(ch) : Character.toLowerCase(ch));
        }

        return result.toString();
    }


    public static boolean isNotNull(String str){
        return org.apache.commons.lang3.StringUtils.isNotEmpty(str);
    }
    public static void main(String[] args) {

    }

}
