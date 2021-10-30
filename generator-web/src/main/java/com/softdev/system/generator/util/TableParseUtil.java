package com.softdev.system.generator.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.softdev.system.generator.entity.ClassInfo;
import com.softdev.system.generator.entity.FieldInfo;
import com.softdev.system.generator.entity.ParamInfo;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 表格解析Util
 *
 * @author zhengkai.blog.csdn.net
 */
public class TableParseUtil {

    /**
     * 解析DDL SQL生成类信息
     *
     * @param paramInfo
     * @return
     */
    public static ClassInfo processTableIntoClassInfo(ParamInfo paramInfo)
            throws IOException {
        //process the param
        String tableSql = paramInfo.getTableSql();
        String nameCaseType = MapUtil.getString(paramInfo.getOptions(),"nameCaseType");
        Boolean isPackageType = MapUtil.getBoolean(paramInfo.getOptions(),"isPackageType");

        if (tableSql == null || tableSql.trim().length() == 0) {
            throw new CodeGenerateException("Table structure can not be empty. 表结构不能为空。");
        }
        //deal with special character
        tableSql = tableSql.trim().replaceAll("'", "`").replaceAll("\"", "`").replaceAll("，", ",").toLowerCase();
        //deal with java string copy \n"
        tableSql = tableSql.trim().replaceAll("\\\\n`", "").replaceAll("\\+", "").replaceAll("``", "`").replaceAll("\\\\", "");
        // table Name
        String tableName = null;
        if (tableSql.contains("TABLE") && tableSql.contains("(")) {
            tableName = tableSql.substring(tableSql.indexOf("TABLE") + 5, tableSql.indexOf("("));
        } else if (tableSql.contains("table") && tableSql.contains("(")) {
            tableName = tableSql.substring(tableSql.indexOf("table") + 5, tableSql.indexOf("("));
        } else {
            throw new CodeGenerateException("Table structure incorrect.表结构不正确。");
        }

        //新增处理create table if not exists members情况
        if (tableName.contains("if not exists")) {
            tableName = tableName.replaceAll("if not exists", "");
        }

        if (tableName.contains("`")) {
            tableName = tableName.substring(tableName.indexOf("`") + 1, tableName.lastIndexOf("`"));
        } else {
            //空格开头的，需要替换掉\n\t空格
            tableName = tableName.replaceAll(" ", "").replaceAll("\n", "").replaceAll("\t", "");
        }
        //优化对byeas`.`ct_bd_customerdiscount这种命名的支持
        if (tableName.contains("`.`")) {
            tableName = tableName.substring(tableName.indexOf("`.`") + 3);
        } else if (tableName.contains(".")) {
            //优化对likeu.members这种命名的支持
            tableName = tableName.substring(tableName.indexOf(".") + 1);
        }
        String originTableName = tableName;
        //ignore prefix
        if(tableName!=null && StringUtils.isNotNull(MapUtil.getString(paramInfo.getOptions(),"ignorePrefix"))){
            tableName = tableName.replaceAll(MapUtil.getString(paramInfo.getOptions(),"ignorePrefix"),"");
        }
        // class Name
        String className = StringUtils.upperCaseFirst(StringUtils.underlineToCamelCase(tableName));
        if (className.contains("_")) {
            className = className.replaceAll("_", "");
        }

        // class Comment
        String classComment = null;
        //mysql是comment=,pgsql/oracle是comment on table,
        //2020-05-25 优化表备注的获取逻辑
        if (tableSql.contains("comment=") || tableSql.contains("comment on table")) {
            String classCommentTmp = (tableSql.contains("comment=")) ?
                    tableSql.substring(tableSql.lastIndexOf("comment=") + 8).trim() : tableSql.substring(tableSql.lastIndexOf("comment on table") + 17).trim();
            if (classCommentTmp.contains("`")) {
                classCommentTmp = classCommentTmp.substring(classCommentTmp.indexOf("`") + 1);
                classCommentTmp = classCommentTmp.substring(0, classCommentTmp.indexOf("`"));
                classComment = classCommentTmp;
            } else {
                //非常规的没法分析
                classComment = className;
            }
        } else {
            //修复表备注为空问题
            classComment = tableName;
        }
        //如果备注跟;混在一起，需要替换掉
        classComment = classComment.replaceAll(";", "");
        // field List
        List<FieldInfo> fieldList = new ArrayList<FieldInfo>();

        // 正常( ) 内的一定是字段相关的定义。
        String fieldListTmp = tableSql.substring(tableSql.indexOf("(") + 1, tableSql.lastIndexOf(")"));

        // 匹配 comment，替换备注里的小逗号, 防止不小心被当成切割符号切割
        String commentPattenStr1 = "comment `(.*?)\\`";
        Matcher matcher1 = Pattern.compile(commentPattenStr1).matcher(fieldListTmp);
        while (matcher1.find()) {

            String commentTmp = matcher1.group();
            //2018-9-27 zhengk 不替换，只处理，支持COMMENT评论里面多种注释
            //commentTmp = commentTmp.replaceAll("\\ comment `|\\`", " ");      // "\\{|\\}"

            if (commentTmp.contains(",")) {
                String commentTmpFinal = commentTmp.replaceAll(",", "，");
                fieldListTmp = fieldListTmp.replace(matcher1.group(), commentTmpFinal);
            }
        }
        //2018-10-18 zhengkai 新增支持double(10, 2)等类型中有英文逗号的特殊情况
        String commentPattenStr2 = "\\`(.*?)\\`";
        Matcher matcher2 = Pattern.compile(commentPattenStr2).matcher(fieldListTmp);
        while (matcher2.find()) {
            String commentTmp2 = matcher2.group();
            if (commentTmp2.contains(",")) {
                String commentTmpFinal = commentTmp2.replaceAll(",", "，").replaceAll("\\(", "（").replaceAll("\\)", "）");
                fieldListTmp = fieldListTmp.replace(matcher2.group(), commentTmpFinal);
            }
        }
        //2018-10-18 zhengkai 新增支持double(10, 2)等类型中有英文逗号的特殊情况
        String commentPattenStr3 = "\\((.*?)\\)";
        Matcher matcher3 = Pattern.compile(commentPattenStr3).matcher(fieldListTmp);
        while (matcher3.find()) {
            String commentTmp3 = matcher3.group();
            if (commentTmp3.contains(",")) {
                String commentTmpFinal = commentTmp3.replaceAll(",", "，");
                fieldListTmp = fieldListTmp.replace(matcher3.group(), commentTmpFinal);
            }
        }
        String[] fieldLineList = fieldListTmp.split(",");
        if (fieldLineList.length > 0) {
            int i = 0;
            //i为了解决primary key关键字出现的地方，出现在前3行，一般和id有关
            for (String columnLine : fieldLineList) {
                i++;
                columnLine = columnLine.replaceAll("\n", "").replaceAll("\t", "").trim();
                // `userid` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                // 2018-9-18 zhengk 修改为contains，提升匹配率和匹配不按照规矩出牌的语句
                // 2018-11-8 zhengkai 修复tornadoorz反馈的KEY FK_permission_id (permission_id),KEY FK_role_id (role_id)情况
                // 2019-2-22 zhengkai 要在条件中使用复杂的表达式
                // 2019-4-29 zhengkai 优化对普通和特殊storage关键字的判断（感谢@AhHeadFloating的反馈 ）
                // 2020-10-20 zhengkai 优化对fulltext/index关键字的处理（感谢@WEGFan的反馈）
                boolean specialFlag = (!columnLine.contains("key ") && !columnLine.contains("constraint") && !columnLine.contains("using") && !columnLine.contains("unique ")
                        && !(columnLine.contains("primary ") && columnLine.indexOf("storage") + 3 > columnLine.indexOf("("))
                        && !columnLine.contains("fulltext ") && !columnLine.contains("index ")
                        && !columnLine.contains("pctincrease")
                        && !columnLine.contains("buffer_pool") && !columnLine.contains("tablespace")
                        && !(columnLine.contains("primary ") && i > 3));
                if (specialFlag) {
                    //如果是oracle的number(x,x)，可能出现最后分割残留的,x)，这里做排除处理
                    if (columnLine.length() < 5) {
                        continue;
                    }
                    //2018-9-16 zhengkai 支持'符号以及空格的oracle语句// userid` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                    String columnName = "";
                    columnLine = columnLine.replaceAll("`", " ").replaceAll("\"", " ").replaceAll("'", "").replaceAll("  ", " ").trim();
                    //如果遇到username varchar(65) default '' not null,这种情况，判断第一个空格是否比第一个引号前
                    try {
                        columnName = columnLine.substring(0, columnLine.indexOf(" "));
                    } catch (StringIndexOutOfBoundsException e) {
                        System.out.println("err happened: " + columnLine);
                        throw e;
                    }

                    // field Name
                    // 2019-09-08 yj 添加是否下划线转换为驼峰的判断
                    String fieldName = null;
                    if (ParamInfo.NAME_CASE_TYPE.CAMEL_CASE.equals(nameCaseType)) {
                        fieldName = StringUtils.lowerCaseFirst(StringUtils.underlineToCamelCase(columnName));
                        if (fieldName.contains("_")) {
                            fieldName = fieldName.replaceAll("_", "");
                        }
                    } else if (ParamInfo.NAME_CASE_TYPE.UNDER_SCORE_CASE.equals(nameCaseType)) {
                        fieldName = StringUtils.lowerCaseFirst(columnName);
                    } else if (ParamInfo.NAME_CASE_TYPE.UPPER_UNDER_SCORE_CASE.equals(nameCaseType)) {
                        fieldName = StringUtils.lowerCaseFirst(columnName.toUpperCase());
                    } else {
                        fieldName = columnName;
                    }
                    columnLine = columnLine.substring(columnLine.indexOf("`") + 1).trim();

                    //swagger class
                    String swaggerClass = "string" ;
                    if (columnLine.contains(" tinyint")) {
                        swaggerClass = "integer";
                    } else if (columnLine.contains(" int") || columnLine.contains(" smallint")) {
                        swaggerClass = "integer";
                    } else if (columnLine.contains(" bigint")) {
                        swaggerClass = "integer";
                    } else if (columnLine.contains(" float")) {
                        swaggerClass = "number";
                    } else if (columnLine.contains(" double")) {
                        swaggerClass = "number";
                    }  else if (columnLine.contains(" boolean")) {
                        swaggerClass = "boolean";
                    }
                    // field class
                    // int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                    String fieldClass = Object.class.getSimpleName();
                    //2018-9-16 zhengk 补充char/clob/blob/json等类型，如果类型未知，默认为String
                    //2018-11-22 lshz0088 处理字段类型的时候，不严谨columnLine.contains(" int") 类似这种的，可在前后适当加一些空格之类的加以区分，否则当我的字段包含这些字符的时候，产生类型判断问题。
                    //2020-05-03 MOSHOW.K.ZHENG 优化对所有类型的处理
                    //2020-10-20 zhengkai 新增包装类型的转换选择
                    if (columnLine.contains(" tinyint")) {
                        //20191115 MOSHOW.K.ZHENG 支持对tinyint的特殊处理
                        fieldClass = MapUtil.getString(paramInfo.getOptions(),"tinyintTransType");;
                    } else if (columnLine.contains(" int") || columnLine.contains(" smallint")) {
                        fieldClass = (isPackageType)?Integer.class.getSimpleName():"int";
                    } else if (columnLine.contains(" bigint")) {
                        fieldClass = (isPackageType)?Long.class.getSimpleName():"long";
                    } else if (columnLine.contains(" float")) {
                        fieldClass = (isPackageType)?Float.class.getSimpleName():"float";
                    } else if (columnLine.contains(" double")) {
                        fieldClass = (isPackageType)?Double.class.getSimpleName():"double";
                    } else if (columnLine.contains(" time") || columnLine.contains(" date") || columnLine.contains(" datetime") || columnLine.contains(" timestamp")) {
                        fieldClass =  MapUtil.getString(paramInfo.getOptions(),"timeTransType");
                    } else if (columnLine.contains(" varchar") || columnLine.contains(" text") || columnLine.contains(" char")
                            || columnLine.contains(" clob") || columnLine.contains(" blob") || columnLine.contains(" json")) {
                        fieldClass = String.class.getSimpleName();
                    } else if (columnLine.contains(" decimal") || columnLine.contains(" number")) {
                        //2018-11-22 lshz0088 建议对number类型增加int，long，BigDecimal的区分判断
                        //如果startKh大于等于0，则表示有设置取值范围
                        int startKh = columnLine.indexOf("(");
                        if (startKh >= 0) {
                            int endKh = columnLine.indexOf(")", startKh);
                            String[] fanwei = columnLine.substring(startKh + 1, endKh).split("，");
                            //2019-1-5 zhengk 修复@arthaschan反馈的超出范围错误
                            //System.out.println("fanwei"+ JSON.toJSONString(fanwei));
                            //                            //number(20,6) fanwei["20","6"]
                            //                            //number(0,6) fanwei["0","6"]
                            //                            //number(20,0) fanwei["20","0"]
                            //                            //number(20) fanwei["20"]
                            //如果括号里是1位或者2位且第二位为0，则进行特殊处理。只有有小数位，都设置为BigDecimal。
                            if ((fanwei.length > 1 && "0".equals(fanwei[1])) || fanwei.length == 1) {
                                int length = Integer.parseInt(fanwei[0]);
                                if (fanwei.length > 1) {
                                    length = Integer.valueOf(fanwei[1]);
                                }
                                //数字范围9位及一下用Integer，大的用Long
                                if (length <= 9) {
                                    fieldClass = (isPackageType)?Integer.class.getSimpleName():"int";
                                } else {
                                    fieldClass = (isPackageType)?Long.class.getSimpleName():"long";
                                }
                            } else {
                                //有小数位数一律使用BigDecimal
                                fieldClass = BigDecimal.class.getSimpleName();
                            }
                        } else {
                            fieldClass = BigDecimal.class.getSimpleName();
                        }
                    } else if (columnLine.contains(" boolean")) {
                        //20190910 MOSHOW.K.ZHENG 新增对boolean的处理（感谢@violinxsc的反馈）以及修复tinyint类型字段无法生成boolean类型问题（感谢@hahaYhui的反馈）
                        fieldClass = (isPackageType)?Boolean.class.getSimpleName():"boolean";
                    } else {
                        fieldClass = String.class.getSimpleName();
                    }

                    // field comment，MySQL的一般位于field行，而pgsql和oralce多位于后面。
                    String fieldComment = null;
                    if (tableSql.contains("comment on column") && (tableSql.contains("." + columnName + " is ") || tableSql.contains(".`" + columnName + "` is"))) {
                        //新增对pgsql/oracle的字段备注支持
                        //COMMENT ON COLUMN public.check_info.check_name IS '检查者名称';
                        //2018-11-22 lshz0088 正则表达式的点号前面应该加上两个反斜杠，否则会认为是任意字符
                        //2019-4-29 zhengkai 优化对oracle注释comment on column的支持（@liukex）
                        tableSql = tableSql.replaceAll(".`" + columnName + "` is", "." + columnName + " is");
                        Matcher columnCommentMatcher = Pattern.compile("\\." + columnName + " is `").matcher(tableSql);
                        fieldComment = columnName;
                        while (columnCommentMatcher.find()) {
                            String columnCommentTmp = columnCommentMatcher.group();
                            //System.out.println(columnCommentTmp);
                            fieldComment = tableSql.substring(tableSql.indexOf(columnCommentTmp) + columnCommentTmp.length()).trim();
                            fieldComment = fieldComment.substring(0, fieldComment.indexOf("`")).trim();
                        }
                    } else if (columnLine.contains(" comment")) {
                        //20200518 zhengkai 修复包含comment关键字的问题
                        String commentTmp = columnLine.substring(columnLine.lastIndexOf("comment") + 7).trim();
                        // '用户ID',
                        if (commentTmp.contains("`") || commentTmp.indexOf("`") != commentTmp.lastIndexOf("`")) {
                            commentTmp = commentTmp.substring(commentTmp.indexOf("`") + 1, commentTmp.lastIndexOf("`"));
                        }
                        //解决最后一句是评论，无主键且连着)的问题:album_id int(3) default '1' null comment '相册id：0 代表头像 1代表照片墙')
                        if (commentTmp.contains(")")) {
                            commentTmp = commentTmp.substring(0, commentTmp.lastIndexOf(")") + 1);
                        }
                        fieldComment = commentTmp;
                    } else {
                        //修复comment不存在导致报错的问题
                        fieldComment = columnName;
                    }

                    FieldInfo fieldInfo = new FieldInfo();
                    //
                    fieldInfo.setColumnName(columnName);
                    fieldInfo.setFieldName(fieldName);
                    fieldInfo.setFieldClass(fieldClass);
                    fieldInfo.setSwaggerClass(swaggerClass);
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
        codeJavaInfo.setOriginTableName(originTableName);

        return codeJavaInfo;
    }

    /**
     * 解析JSON生成类信息
     *
     * @param paramInfo
     * @return
     */
    public static ClassInfo processJsonToClassInfo(ParamInfo paramInfo) {
        ClassInfo codeJavaInfo = new ClassInfo();
        codeJavaInfo.setTableName("JsonDto");
        codeJavaInfo.setClassName("JsonDto");
        codeJavaInfo.setClassComment("JsonDto");

        //support children json if forget to add '{' in front of json
        if (paramInfo.getTableSql().trim().startsWith("\"")) {
            paramInfo.setTableSql("{" + paramInfo.getTableSql());
        }
        if (JSON.isValid(paramInfo.getTableSql())) {
            if (paramInfo.getTableSql().trim().startsWith("{")) {
                JSONObject jsonObject = JSONObject.parseObject(paramInfo.getTableSql().trim());
                //parse FieldList by JSONObject
                codeJavaInfo.setFieldList(processJsonObjectToFieldList(jsonObject));
            } else if (paramInfo.getTableSql().trim().startsWith("[")) {
                JSONArray jsonArray = JSONArray.parseArray(paramInfo.getTableSql().trim());
                //parse FieldList by JSONObject
                codeJavaInfo.setFieldList(processJsonObjectToFieldList(jsonArray.getJSONObject(0)));
            }
        }

        return codeJavaInfo;
    }

    /**
     * parse SQL by regex
     *
     * @param paramInfo
     * @return
     * @author https://github.com/ydq
     */
    public static ClassInfo processTableToClassInfoByRegex(ParamInfo paramInfo) {
        // field List
        List<FieldInfo> fieldList = new ArrayList<FieldInfo>();
        //return classInfo
        ClassInfo codeJavaInfo = new ClassInfo();

        //匹配整个ddl，将ddl分为表名，列sql部分，表注释
        String DDL_PATTEN_STR = "\\s*create\\s+table\\s+(?<tableName>\\S+)[^\\(]*\\((?<columnsSQL>[\\s\\S]+)\\)[^\\)]+?(comment\\s*(=|on\\s+table)\\s*'(?<tableComment>.*?)'\\s*;?)?$";

        Pattern DDL_PATTERN = Pattern.compile(DDL_PATTEN_STR, Pattern.CASE_INSENSITIVE);

        //匹配列sql部分，分别解析每一列的列名 类型 和列注释
        String COL_PATTERN_STR = "\\s*(?<fieldName>\\S+)\\s+(?<fieldType>\\w+)\\s*(?:\\([\\s\\d,]+\\))?((?!comment).)*(comment\\s*'(?<fieldComment>.*?)')?\\s*(,|$)";

        Pattern COL_PATTERN = Pattern.compile(COL_PATTERN_STR, Pattern.CASE_INSENSITIVE);

        Matcher matcher = DDL_PATTERN.matcher(paramInfo.getTableSql().trim());
        if (matcher.find()) {
            String tableName = matcher.group("tableName");
            String tableComment = matcher.group("tableComment");
            codeJavaInfo.setTableName(tableName.replaceAll("'", ""));
            codeJavaInfo.setClassName(tableName.replaceAll("'", ""));
            codeJavaInfo.setClassComment(tableComment.replaceAll("'", ""));
            String columnsSQL = matcher.group("columnsSQL");
            if (columnsSQL != null && columnsSQL.length() > 0) {
                Matcher colMatcher = COL_PATTERN.matcher(columnsSQL);
                while (colMatcher.find()) {
                    String fieldName = colMatcher.group("fieldName");
                    String fieldType = colMatcher.group("fieldType");
                    String fieldComment = colMatcher.group("fieldComment");
                    if (!"key".equalsIgnoreCase(fieldType)) {
                        FieldInfo fieldInfo = new FieldInfo();
                        fieldInfo.setFieldName(fieldName.replaceAll("'", ""));
                        fieldInfo.setColumnName(fieldName.replaceAll("'", ""));
                        fieldInfo.setFieldClass(fieldType.replaceAll("'", ""));
                        fieldInfo.setFieldComment(fieldComment.replaceAll("'", ""));
                        fieldList.add(fieldInfo);
                    }
                }
            }
            codeJavaInfo.setFieldList(fieldList);
        }
        return codeJavaInfo;
    }

    public static List<FieldInfo> processJsonObjectToFieldList(JSONObject jsonObject) {
        // field List
        List<FieldInfo> fieldList = new ArrayList<FieldInfo>();
        jsonObject.keySet().stream().forEach(jsonField -> {
            FieldInfo fieldInfo = new FieldInfo();
            fieldInfo.setFieldName(jsonField);
            fieldInfo.setColumnName(jsonField);
            fieldInfo.setFieldClass(String.class.getSimpleName());
            fieldInfo.setFieldComment("father:" + jsonField);
            fieldList.add(fieldInfo);
            if (jsonObject.get(jsonField) instanceof JSONArray) {
                jsonObject.getJSONArray(jsonField).stream().forEach(arrayObject -> {
                    FieldInfo fieldInfo2 = new FieldInfo();
                    fieldInfo2.setFieldName(arrayObject.toString());
                    fieldInfo2.setColumnName(arrayObject.toString());
                    fieldInfo2.setFieldClass(String.class.getSimpleName());
                    fieldInfo2.setFieldComment("children:" + arrayObject.toString());
                    fieldList.add(fieldInfo2);
                });
            } else if (jsonObject.get(jsonField) instanceof JSONObject) {
                jsonObject.getJSONObject(jsonField).keySet().stream().forEach(arrayObject -> {
                    FieldInfo fieldInfo2 = new FieldInfo();
                    fieldInfo2.setFieldName(arrayObject.toString());
                    fieldInfo2.setColumnName(arrayObject.toString());
                    fieldInfo2.setFieldClass(String.class.getSimpleName());
                    fieldInfo2.setFieldComment("children:" + arrayObject.toString());
                    fieldList.add(fieldInfo2);
                });
            }
        });
        if (fieldList.size() < 1) {
            throw new CodeGenerateException("JSON解析失败");
        }
        return fieldList;
    }

    public static ClassInfo processInsertSqlToClassInfo(ParamInfo paramInfo) {
        // field List
        List<FieldInfo> fieldList = new ArrayList<FieldInfo>();
        //return classInfo
        ClassInfo codeJavaInfo = new ClassInfo();

        //get origin sql
        String fieldSqlStr = paramInfo.getTableSql().toLowerCase().trim();
        fieldSqlStr = fieldSqlStr.replaceAll("  ", " ").replaceAll("\\\\n`", "")
                .replaceAll("\\+", "").replaceAll("``", "`").replaceAll("\\\\", "");
        String valueStr = fieldSqlStr.substring(fieldSqlStr.lastIndexOf("values") + 6).replaceAll(" ", "").replaceAll("\\(", "").replaceAll("\\)", "");
        //get the string between insert into and values
        fieldSqlStr = fieldSqlStr.substring(0, fieldSqlStr.lastIndexOf("values"));

        System.out.println(fieldSqlStr);

        String insertSqlPattenStr = "insert into (?<tableName>.*) \\((?<columnsSQL>.*)\\)";
        //String DDL_PATTEN_STR="\\s*create\\s+table\\s+(?<tableName>\\S+)[^\\(]*\\((?<columnsSQL>[\\s\\S]+)\\)[^\\)]+?(comment\\s*(=|on\\s+table)\\s*'(?<tableComment>.*?)'\\s*;?)?$";

        Matcher matcher1 = Pattern.compile(insertSqlPattenStr).matcher(fieldSqlStr);
        while (matcher1.find()) {

            String tableName = matcher1.group("tableName");
            //System.out.println("tableName:"+tableName);
            codeJavaInfo.setClassName(tableName);
            codeJavaInfo.setTableName(tableName);

            String columnsSQL = matcher1.group("columnsSQL");
            //System.out.println("columnsSQL:"+columnsSQL);

            List<String> valueList = new ArrayList<>();
            //add values as comment
            Arrays.stream(valueStr.split(",")).forEach(column -> {
                valueList.add(column);
            });
            AtomicInteger n = new AtomicInteger(0);
            //add column to fleldList
            Arrays.stream(columnsSQL.replaceAll(" ", "").split(",")).forEach(column -> {
                FieldInfo fieldInfo2 = new FieldInfo();
                fieldInfo2.setFieldName(column);
                fieldInfo2.setColumnName(column);
                fieldInfo2.setFieldClass(String.class.getSimpleName());
                if (n.get() < valueList.size()) {
                    fieldInfo2.setFieldComment(column + " , eg." + valueList.get(n.get()));
                }
                fieldList.add(fieldInfo2);
                n.getAndIncrement();
            });

        }
        if (fieldList.size() < 1) {
            throw new CodeGenerateException("INSERT SQL解析失败");
        }
        codeJavaInfo.setFieldList(fieldList);
        return codeJavaInfo;
    }

}
