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
        tableSql = tableSql.trim().replaceAll("'","`").replaceAll("\"","`").replaceAll("，",",").toLowerCase();

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
        //mysql是comment=,pgsql/oracle是comment on table,
        if (tableSql.contains("comment=")) {
            String classCommentTmp = tableSql.substring(tableSql.lastIndexOf("comment=")+8).replaceAll("`","").trim();
            if (classCommentTmp.indexOf(" ")!=classCommentTmp.lastIndexOf(" ")) {
                classCommentTmp = classCommentTmp.substring(classCommentTmp.indexOf(" ")+1, classCommentTmp.lastIndexOf(" "));
            }
            if (classCommentTmp!=null && classCommentTmp.trim().length()>0) {
                classComment = classCommentTmp;
            }else{
                //修复表备注为空问题
                classComment = className;
            }
        }else if(tableSql.contains("comment on table")) {
            //COMMENT ON TABLE CT_BAS_FEETYPE IS 'CT_BAS_FEETYPE';
            String classCommentTmp = tableSql.substring(tableSql.lastIndexOf("comment on table")+17).trim();
            //证明这是一个常规的COMMENT ON TABLE  xxx IS 'xxxx'
            if (classCommentTmp.contains("`")) {
                classCommentTmp = classCommentTmp.substring(classCommentTmp.indexOf("`")+1);
                classCommentTmp = classCommentTmp.substring(0,classCommentTmp.indexOf("`"));
                classComment = classCommentTmp;
            }else{
                //非常规的没法分析
                classComment = tableName;
            }
        }else{
            //修复表备注为空问题
            classComment = tableName;
        }
        //如果备注跟;混在一起，需要替换掉
        classComment=classComment.replaceAll(";","");
        // field List
        List<FieldInfo> fieldList = new ArrayList<FieldInfo>();

        // 正常( ) 内的一定是字段相关的定义。
        String fieldListTmp = tableSql.substring(tableSql.indexOf("(")+1, tableSql.lastIndexOf(")"));

        // 匹配 comment，替换备注里的小逗号, 防止不小心被当成切割符号切割
        Matcher matcher = Pattern.compile("comment `(.*?)\\`").matcher(fieldListTmp);     // "\\{(.*?)\\}"
        while(matcher.find()){

            String commentTmp = matcher.group();
            //2018-9-27 zhengk 不替换，只处理，支持COMMENT评论里面多种注释
            //commentTmp = commentTmp.replaceAll("\\ comment `|\\`", " ");      // "\\{|\\}"

            if (commentTmp.contains(",")) {
                String commentTmpFinal = commentTmp.replaceAll(",", "，");
                fieldListTmp = fieldListTmp.replace(matcher.group(), commentTmpFinal);
            }
        }
        //2018-10-18 zhengkai 新增支持double(10, 2)等类型中有英文逗号的特殊情况
        Matcher matcher2 = Pattern.compile("\\`(.*?)\\`").matcher(fieldListTmp);     // "\\{(.*?)\\}"
        while(matcher2.find()){
            String commentTmp2 = matcher2.group();
            if (commentTmp2.contains(",")) {
                String commentTmpFinal = commentTmp2.replaceAll(",", "，").replaceAll("\\(", "（").replaceAll("\\)", "）");
                fieldListTmp = fieldListTmp.replace(matcher2.group(), commentTmpFinal);
            }
        }
        //2018-10-18 zhengkai 新增支持double(10, 2)等类型中有英文逗号的特殊情况
        Matcher matcher3 = Pattern.compile("\\((.*?)\\)").matcher(fieldListTmp);     // "\\{(.*?)\\}"
        while(matcher3.find()){
            String commentTmp3 = matcher3.group();
            if (commentTmp3.contains(",")) {
                String commentTmpFinal = commentTmp3.replaceAll(",", "，");
                fieldListTmp = fieldListTmp.replace(matcher3.group(), commentTmpFinal);
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
                // 2018-11-8 zhengkai 修复tornadoorz反馈的KEY FK_permission_id (permission_id),KEY FK_role_id (role_id)情况
                if (!columnLine.contains("key ")&&!columnLine.contains("constraint")&&!columnLine.contains("using")&&!columnLine.contains("unique")
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
                    //2018-11-22 lshz0088 处理字段类型的时候，不严谨columnLine.contains(" int") 类似这种的，可在前后适当加一些空格之类的加以区分，否则当我的字段包含这些字符的时候，产生类型判断问题。
                    if (columnLine.contains(" int") || columnLine.contains("tinyint") || columnLine.contains("smallint")) {
                        fieldClass = Integer.TYPE.getSimpleName();
                    } else if (columnLine.contains("bigint")) {
                        fieldClass = Long.TYPE.getSimpleName();
                    } else if (columnLine.contains("float")) {
                        fieldClass = Float.TYPE.getSimpleName();
                    } else if (columnLine.contains("double")) {
                        fieldClass = Double.TYPE.getSimpleName();
                    } else if (columnLine.contains("datetime") || columnLine.contains("timestamp")) {
                        fieldClass = Date.class.getSimpleName();
                    } else if (columnLine.contains("varchar") || columnLine.contains(" text")|| columnLine.contains("char")
                            || columnLine.contains("clob")||columnLine.contains("blob")||columnLine.contains("json")) {
                        fieldClass = String.class.getSimpleName();
                    } else if (columnLine.contains("decimal")||columnLine.contains(" number")) {
                        //2018-11-22 lshz0088 建议对number类型增加int，long，BigDecimal的区分判断
                        //如果startKh大于等于0，则表示有设置取值范围
                        int startKh=columnLine.indexOf("(");
                        if(startKh>=0){
                            int endKh=columnLine.indexOf(")",startKh);
                            String[] fanwei=columnLine.substring(startKh+1,endKh).split("，");
                            if("0".equals(fanwei[1])){
                                //如果没有小数位数
                                int length=Integer.valueOf(fanwei[0]);
                                if(length<=9){
                                    fieldClass = Integer.class.getSimpleName();
                                }else{
                                    fieldClass = Long.class.getSimpleName();
                                }
                            }else{
                                //有小数位数一律使用BigDecimal
                                fieldClass = BigDecimal.class.getSimpleName();
                            }
                        }else{
                            fieldClass = BigDecimal.class.getSimpleName();
                        }
                    }else {
                        fieldClass = String.class.getSimpleName();
                    }

                    // field comment，MySQL的一般位于field行，而pgsql和oralce多位于后面。
                    String fieldComment = null;
                    if (columnLine.contains("comment")) {
                        String commentTmp = columnLine.substring(columnLine.indexOf("comment")+7).trim();	// '用户ID',
                        if (commentTmp.contains("`") || commentTmp.indexOf("`")!=commentTmp.lastIndexOf("`")) {
                            commentTmp = commentTmp.substring(commentTmp.indexOf("`")+1, commentTmp.lastIndexOf("`"));
                        }
                        //解决最后一句是评论，无主键且连着)的问题:album_id int(3) default '1' null comment '相册id：0 代表头像 1代表照片墙')
                        if(commentTmp.contains(")")){
                            commentTmp = commentTmp.substring(0, commentTmp.lastIndexOf(")")+1);
                        }
                        fieldComment = commentTmp;
                    }else if(tableSql.contains("comment on column")&&tableSql.contains("."+columnName+" is `")){
                        //新增对pgsql/oracle的字段备注支持
                        //COMMENT ON COLUMN public.check_info.check_name IS '检查者名称';
                        //2018-11-22 lshz0088 正则表达式的点号前面应该加上两个反斜杠，否则会认为是任意字符
                        Matcher columnCommentMatcher = Pattern.compile("\\."+columnName+" is `").matcher(tableSql);     // "\\{(.*?)\\}"
                        while(columnCommentMatcher.find()){
                            String columnCommentTmp = columnCommentMatcher.group();
                            System.out.println(columnCommentTmp);
                            fieldComment = tableSql.substring(tableSql.indexOf(columnCommentTmp)+columnCommentTmp.length()).trim();
                            fieldComment = fieldComment.substring(0,fieldComment.indexOf("`")).trim();
                        }
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
            throw new CodeGenerateException("表结构分析失败，请检查语句或者提交issue给我");
        }

        ClassInfo codeJavaInfo = new ClassInfo();
        codeJavaInfo.setTableName(tableName);
        codeJavaInfo.setClassName(className);
        codeJavaInfo.setClassComment(classComment);
        codeJavaInfo.setFieldList(fieldList);

        return codeJavaInfo;
    }

}
