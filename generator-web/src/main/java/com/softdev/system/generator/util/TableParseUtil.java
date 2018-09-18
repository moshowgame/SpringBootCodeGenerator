package com.softdev.system.generator.util;



import com.softdev.system.generator.entity.ClassInfo;
import com.softdev.system.generator.entity.FieldInfo;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xuxueli 2018-05-02 21:10:45
 * @modify zhengk/moshow 20180913
 */
public class TableParseUtil {

    /**
     * 解析建表SQL生成代码（model-dao-xml）
     *
     * @param tableSql
     * @return
     */
    public static ClassInfo processTableIntoClassInfo(String tableSql) throws IOException {
        if (tableSql==null || tableSql.trim().length()==0) {
            throw new CodeGenerateException("Table structure can not be empty.");
        }
        tableSql = tableSql.trim().replaceAll("'","`").replaceAll("\"","`").toLowerCase();

        // table Name
        String tableName = null;
        if (tableSql.contains("TABLE") && tableSql.contains("(")) {
            tableName = tableSql.substring(tableSql.indexOf("TABLE")+5, tableSql.indexOf("("));
        } else if (tableSql.contains("table") && tableSql.contains("(")) {
            tableName = tableSql.substring(tableSql.indexOf("table")+5, tableSql.indexOf("("));
        } else {
            throw new CodeGenerateException("Table structure anomaly.");
        }

        //新增处理create table if not exists members情况
        if (tableName.contains("if not exists")) tableName=tableName.replaceAll("if not exists","");

        if (tableName.contains("`")) {
            tableName = tableName.substring(tableName.indexOf("`")+1, tableName.lastIndexOf("`"));
        }else{
            //空格开头的，需要替换掉\n\t空格
            tableName=tableName.replaceAll(" ","").replaceAll("\n","").replaceAll("\t","");
        }
        //优化对byeas`.`ct_bd_customerdiscount这种命名的支持
        if(tableName.contains("`.`")){
            tableName=tableName.substring(tableName.indexOf("`.`")+3);
        }else if(tableName.contains(".")){
            //优化对likeu.members这种命名的支持
            tableName=tableName.substring(tableName.indexOf(".")+1);
        }
        // class Name
        String className = StringUtils.upperCaseFirst(StringUtils.underlineToCamelCase(tableName));
        if (className.contains("_")) {
            className = className.replaceAll("_", "");
        }

        // class Comment
        String classComment = null;
        if (tableSql.contains("COMMENT=")) {
            String classCommentTmp = tableSql.substring(tableSql.lastIndexOf("COMMENT=")+8).trim();
            if (classCommentTmp.contains("'") || classCommentTmp.indexOf("'")!=classCommentTmp.lastIndexOf("'")) {
                classCommentTmp = classCommentTmp.substring(classCommentTmp.indexOf("'")+1, classCommentTmp.lastIndexOf("'"));
            }
            if (classCommentTmp!=null && classCommentTmp.trim().length()>0) {
                classComment = classCommentTmp;
            }else{
                //修复表备注为空问题
                classComment = className;
            }
        }else{
            //修复表备注为空问题
            classComment = className;
        }

        // field List
        List<FieldInfo> fieldList = new ArrayList<FieldInfo>();

        String fieldListTmp = tableSql.substring(tableSql.indexOf("(")+1, tableSql.lastIndexOf(")"));

        // replave "," by "，" in comment
        Matcher matcher = Pattern.compile("\\ COMMENT '(.*?)\\'").matcher(fieldListTmp);     // "\\{(.*?)\\}"
        while(matcher.find()){

            String commentTmp = matcher.group();
            commentTmp = commentTmp.replaceAll("\\ COMMENT '|\\'", "");      // "\\{|\\}"

            if (commentTmp.contains(",")) {
                String commentTmpFinal = commentTmp.replaceAll(",", "，");
                fieldListTmp = fieldListTmp.replace(matcher.group(), commentTmpFinal);
            }
        }


        String[] fieldLineList = fieldListTmp.split(",");
        if (fieldLineList.length > 0) {
            int i=0;//i为了解决primary key关键字出现的地方，出现在前3行，一般和id有关
            for (String columnLine :fieldLineList) {
                i++;
                columnLine = columnLine.replaceAll("\n","").replaceAll("\t","").trim();
                // `userid` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                // 2018-9-18 zhengk 修改为contains，提升匹配率和匹配不按照规矩出牌的语句
                if (!columnLine.contains("constraint")&&!columnLine.contains("using")
                        &&!columnLine.contains("storage")&&!columnLine.contains("pctincrease")
                        &&!columnLine.contains("buffer_pool")&&!columnLine.contains("tablespace")
                        &&!(columnLine.contains("primary")&&i>3)){

                    //如果是oracle的number(x,x)，可能出现最后分割残留的,x)，这里做排除处理
                    if(columnLine.length()<5) continue;
                    //2018-9-16 zhengkai 支持'符号以及空格的oracle语句// userid` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                    String columnName = "";
                    columnLine=columnLine.replaceAll("`"," ").replaceAll("\""," ").replaceAll("'","").replaceAll("  "," ").trim();
                    //如果遇到username varchar(65) default '' not null,这种情况，判断第一个空格是否比第一个引号前
                    columnName = columnLine.substring(0, columnLine.indexOf(" "));

                    // field Name
                    String fieldName = StringUtils.lowerCaseFirst(StringUtils.underlineToCamelCase(columnName));
                    if (fieldName.contains("_")) {
                        fieldName = fieldName.replaceAll("_", "");
                    }

                    // field class
                    columnLine = columnLine.substring(columnLine.indexOf("`")+1).trim();	// int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                    String fieldClass = Object.class.getSimpleName();
                    //2018-9-16 zhengk 补充char/clob/blob/json等类型，如果类型未知，默认为String
                    if (columnLine.contains("int") || columnLine.contains("tinyint") || columnLine.contains("smallint")) {
                        fieldClass = Integer.TYPE.getSimpleName();
                    } else if (columnLine.contains("bigint")) {
                        fieldClass = Long.TYPE.getSimpleName();
                    } else if (columnLine.contains("float")) {
                        fieldClass = Float.TYPE.getSimpleName();
                    } else if (columnLine.contains("double")) {
                        fieldClass = Double.TYPE.getSimpleName();
                    } else if (columnLine.contains("datetime") || columnLine.contains("timestamp")) {
                        fieldClass = Date.class.getSimpleName();
                    } else if (columnLine.contains("varchar") || columnLine.contains("text")|| columnLine.contains("char")
                            || columnLine.contains("clob")||columnLine.contains("blob")||columnLine.contains("json")) {
                        fieldClass = String.class.getSimpleName();
                    } else if (columnLine.contains("decimal")||columnLine.contains("number")) {
                        fieldClass = BigDecimal.class.getSimpleName();
                    } else {
                        fieldClass = String.class.getSimpleName();
                    }

                    // field comment
                    String fieldComment = null;
                    if (columnLine.contains("COMMENT")) {
                        String commentTmp = fieldComment = columnLine.substring(columnLine.indexOf("COMMENT")+7).trim();	// '用户ID',
                        if (commentTmp.contains("'") || commentTmp.indexOf("'")!=commentTmp.lastIndexOf("'")) {
                            commentTmp = commentTmp.substring(commentTmp.indexOf("'")+1, commentTmp.lastIndexOf("'"));
                        }
                        fieldComment = commentTmp;
                    }else{
                        //修复comment不存在导致报错的问题
                        fieldComment = columnName;
                    }

                    FieldInfo fieldInfo = new FieldInfo();
                    fieldInfo.setColumnName(columnName);
                    fieldInfo.setFieldName(fieldName);
                    fieldInfo.setFieldClass(fieldClass);
                    fieldInfo.setFieldComment(fieldComment);

                    fieldList.add(fieldInfo);
                }
            }
        }

        if (fieldList.size() < 1) {
            throw new CodeGenerateException("Table structure anomaly.");
        }

        ClassInfo codeJavaInfo = new ClassInfo();
        codeJavaInfo.setTableName(tableName);
        codeJavaInfo.setClassName(className);
        codeJavaInfo.setClassComment(classComment);
        codeJavaInfo.setFieldList(fieldList);

        return codeJavaInfo;
    }

}
