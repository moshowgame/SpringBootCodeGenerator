package com.softdev.system.generator.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.softdev.system.generator.entity.ClassInfo;
import com.softdev.system.generator.entity.FieldInfo;
import com.softdev.system.generator.entity.NonCaseString;
import com.softdev.system.generator.entity.ParamInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
        NonCaseString tableSql = NonCaseString.of(paramInfo.getTableSql());
        String nameCaseType = MapUtil.getString(paramInfo.getOptions(),"nameCaseType");
        Boolean isPackageType = MapUtil.getBoolean(paramInfo.getOptions(),"isPackageType");

        if (tableSql == null || tableSql.trim().length() == 0) {
            throw new CodeGenerateException("Table structure can not be empty. 表结构不能为空。");
        }
        //deal with special character
        tableSql = tableSql.trim()
                .replaceAll("'", "`")
                .replaceAll("\"", "`")
                .replaceAll("，", ",")
                // 这里全部转小写, 会让驼峰风格的字段名丢失驼峰信息(真有驼峰sql字段名的呢(*￣︶￣)); 下文使用工具方法处理包含等
                // .toLowerCase()
        ;
        //deal with java string copy \n"
        tableSql = tableSql.trim().replaceAll("\\\\n`", "").replaceAll("\\+", "").replaceAll("``", "`").replaceAll("\\\\", "");
        // table Name
        String tableName = null;
        int tableKwIx = tableSql.indexOf("TABLE"); // 包含判断和位置一次搞定
        if (tableKwIx > -1 && tableSql.contains("(")) {
            tableName = tableSql.substring(tableKwIx + 5, tableSql.indexOf("(")).get();
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
        if (tableSql.containsAny("comment=", "comment on table")) {
            int ix = tableSql.lastIndexOf("comment=");
            String classCommentTmp = (ix > -1) ?
                    tableSql.substring(ix + 8).trim().get() :
                    tableSql.substring(tableSql.lastIndexOf("comment on table") + 17).trim().get();
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
        String fieldListTmp = tableSql.substring(tableSql.indexOf("(") + 1, tableSql.lastIndexOf(")")).get();

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
            for (String columnLine0 : fieldLineList) {
                NonCaseString columnLine = NonCaseString.of(columnLine0);
                i++;
                columnLine = columnLine.replaceAll("\n", "").replaceAll("\t", "").trim();
                // `userid` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                // 2018-9-18 zhengk 修改为contains，提升匹配率和匹配不按照规矩出牌的语句
                // 2018-11-8 zhengkai 修复tornadoorz反馈的KEY FK_permission_id (permission_id),KEY FK_role_id (role_id)情况
                // 2019-2-22 zhengkai 要在条件中使用复杂的表达式
                // 2019-4-29 zhengkai 优化对普通和特殊storage关键字的判断（感谢@AhHeadFloating的反馈 ）
                // 2020-10-20 zhengkai 优化对fulltext/index关键字的处理（感谢@WEGFan的反馈）
                // 2023-8-27 L&J 改用工具方法判断, 且修改变量名(非特殊标识), 方法抽取
                boolean notSpecialFlag = isNotSpecialColumnLine(columnLine, i);

                if (notSpecialFlag) {
                    //如果是oracle的number(x,x)，可能出现最后分割残留的,x)，这里做排除处理
                    if (columnLine.length() < 5) {
                        continue;
                    }
                    //2018-9-16 zhengkai 支持'符号以及空格的oracle语句// userid` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                    String columnName = "";
                    columnLine = columnLine.replaceAll("`", " ").replaceAll("\"", " ").replaceAll("'", "").replaceAll("  ", " ").trim();
                    //如果遇到username varchar(65) default '' not null,这种情况，判断第一个空格是否比第一个引号前
                    try {
                        columnName = columnLine.substring(0, columnLine.indexOf(" ")).get();
                    } catch (StringIndexOutOfBoundsException e) {
                        System.out.println("err happened: " + columnLine);
                        throw e;
                    }

                    // field Name
                    // 2019-09-08 yj 添加是否下划线转换为驼峰的判断
                    // 2023-8-27 L&J 支持原始列名任意命名风格, 不依赖用户是否输入下划线
                    String fieldName = null;
                    if (ParamInfo.NAME_CASE_TYPE.CAMEL_CASE.equals(nameCaseType)) {
                        // 2024-1-27 L&J 适配任意(maybe)原始风格转小写驼峰
                        fieldName = StringUtils.toLowerCamel(columnName);
                    } else if (ParamInfo.NAME_CASE_TYPE.UNDER_SCORE_CASE.equals(nameCaseType)) {
                        fieldName = StringUtils.toUnderline(columnName, false);
                    } else if (ParamInfo.NAME_CASE_TYPE.UPPER_UNDER_SCORE_CASE.equals(nameCaseType)) {
                        fieldName = StringUtils.toUnderline(columnName.toUpperCase(), true);
                    } else {
                        fieldName = columnName;
                    }
                    columnLine = columnLine.substring(columnLine.indexOf("`") + 1).trim();
                    String mysqlType = columnLine.split("\\s+")[1];
                    if(mysqlType.contains("(")){
                        mysqlType = mysqlType.substring(0, mysqlType.indexOf("("));
                    }
                    //swagger class
                    String swaggerClass = "string" ;
                    if(mysqlJavaTypeUtil.getMysqlSwaggerTypeMap().containsKey(mysqlType)){
                        swaggerClass = mysqlJavaTypeUtil.getMysqlSwaggerTypeMap().get(mysqlType);
                    }
                    // field class
                    // int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                    String fieldClass = "String";
                    //2018-9-16 zhengk 补充char/clob/blob/json等类型，如果类型未知，默认为String
                    //2018-11-22 lshz0088 处理字段类型的时候，不严谨columnLine.contains(" int") 类似这种的，可在前后适当加一些空格之类的加以区分，否则当我的字段包含这些字符的时候，产生类型判断问题。
                    //2020-05-03 MOSHOW.K.ZHENG 优化对所有类型的处理
                    //2020-10-20 zhengkai 新增包装类型的转换选择
                    if(mysqlJavaTypeUtil.getMysqlJavaTypeMap().containsKey(mysqlType)){
                        fieldClass = mysqlJavaTypeUtil.getMysqlJavaTypeMap().get(mysqlType);
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
                            fieldComment = tableSql.substring(tableSql.indexOf(columnCommentTmp) + columnCommentTmp.length()).trim().get();
                            fieldComment = fieldComment.substring(0, fieldComment.indexOf("`")).trim();
                        }
                    } else if (columnLine.contains(" comment")) {
                        //20200518 zhengkai 修复包含comment关键字的问题
                        String commentTmp = columnLine.substring(columnLine.lastIndexOf("comment") + 7).trim().get();
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

    private static boolean isNotSpecialColumnLine(NonCaseString columnLine, int lineSeq) {
        return (
                !columnLine.containsAny(
                        "key ",
                        "constraint",
                        "using",
                        "unique ",
                        "fulltext ",
                        "index ",
                        "pctincrease",
                        "buffer_pool",
                        "tablespace"
                )
                && !(columnLine.contains("primary ") && columnLine.indexOf("storage") + 3 > columnLine.indexOf("("))
                && !(columnLine.contains("primary ") && lineSeq > 3)
        );
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
