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
        return (str!=null&&str.length()>1)?str.substring(0, 1).toLowerCase() + str.substring(1):"";
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
                if ("_".charAt(0) == ch) {
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

    public static void main(String[] args) {
        /*String tableSql="comment on column check_info is `体检信息` ,comment on column check_xxxxx is `体检信息xx` ";
        String fieldName="check_info";
        String fieldComment="aaa";
        Matcher columnCommentMatcher = Pattern.compile(fieldName+" is `").matcher(tableSql);     // "\\{(.*?)\\}"
        while(columnCommentMatcher.find()){
            String columnCommentTmp = columnCommentMatcher.group();
            System.out.println(columnCommentTmp);
            fieldComment = tableSql.substring(tableSql.indexOf(columnCommentTmp)+columnCommentTmp.length()).trim();
            fieldComment = fieldComment.substring(0,fieldComment.indexOf("`")).trim();
        }
        System.out.println(fieldComment);*/
        /*ClassInfo classInfo=new ClassInfo();
        classInfo.setClassComment("xxxx");
        classInfo.setTableName("2b");
        ClassInfo newInfo=new ClassInfo();
        classInfo.setClassName("bbbb");
        classInfo.setTableName("3b");
        //以new为主
        BeanUtil.copyProperties(classInfo,newInfo,true, CopyOptions.create());
        System.out.println(JSON.toJSONString(newInfo));*/
    }
}
