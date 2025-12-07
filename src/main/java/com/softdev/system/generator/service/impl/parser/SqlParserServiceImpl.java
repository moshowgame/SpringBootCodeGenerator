package com.softdev.system.generator.service.impl.parser;

import com.alibaba.fastjson2.JSON;
import com.softdev.system.generator.entity.dto.ClassInfo;
import com.softdev.system.generator.entity.dto.FieldInfo;
import com.softdev.system.generator.entity.dto.ParamInfo;
import com.softdev.system.generator.service.parser.SqlParserService;
import com.softdev.system.generator.util.MapUtil;
import com.softdev.system.generator.util.StringUtilsPlus;
import com.softdev.system.generator.util.exception.SqlParseException;
import com.softdev.system.generator.util.mysqlJavaTypeUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * SQL解析服务实现类
 *
 * @author zhengkai.blog.csdn.net
 */
@Slf4j
@Service
public class SqlParserServiceImpl implements SqlParserService {

    @Override
    public ClassInfo generateSelectSqlBySQLPraser(ParamInfo paramInfo) throws Exception {
        ClassInfo classInfo = new ClassInfo();
        String processedSql = paramInfo.getTableSql().trim()
                .replaceAll("'", "`")          // 将单引号替换为反引号
                .replaceAll("\"", "`")         // 将双引号替换为反引号
                .replaceAll("，", ",");        // 将中文逗号替换为英文逗号

        Statement statement = null;
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        statement = parserManager.parse(new StringReader(processedSql));
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder(); // 创建表名发现者对象
        List<String> tableNameList = tablesNamesFinder.getTableList(statement); // 获取到表名列表
        //一般这里应该只解析到一个表名，除非多个表名，取第一个
        if (!CollectionUtils.isEmpty(tableNameList)) {
            String tableName = tableNameList.get(0).trim();
            classInfo.setTableName(tableName);
            classInfo.setOriginTableName(tableName);
            String className = StringUtilsPlus.upperCaseFirst(StringUtilsPlus.underlineToCamelCase(tableName)).replaceAll("`", "");
            if (className.contains("_")) {
                className = className.replaceAll("_", "");
            }
            classInfo.setClassName(className);
            classInfo.setClassComment(paramInfo.getTableSql());
        }
        //解析查询元素
        Select select = null;
        select = (Select) CCJSqlParserUtil.parse(paramInfo.getTableSql());
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
        List<SelectItem<?>> selectItems = plainSelect.getSelectItems();

        // field List
        List<FieldInfo> fieldList = new ArrayList<FieldInfo>();
        selectItems.forEach(t->{
            FieldInfo fieldInfo = new FieldInfo();
            String fieldName = ((Column)t.getExpression()).getColumnName().replaceAll("`", "");
            String aliasName = t.getAlias() != null ? t.getAlias().getName() : ((Column)t.getExpression()).getColumnName();
            //存储原始字段名
            fieldInfo.setFieldComment(aliasName);fieldInfo.setColumnName(aliasName);
            //处理字段名是t.xxx的情况
            fieldName=fieldName.contains(".")?fieldName.substring(fieldName.indexOf(".")+1):fieldName;
            //转换前
            fieldInfo.setColumnName(fieldName);
            fieldName = switch ((String) paramInfo.getOptions().get("nameCaseType")) {
                case ParamInfo.NameCaseType.CAMEL_CASE ->
                    // 2024-1-27 L&J 适配任意(maybe)原始风格转小写驼峰
                        StringUtilsPlus.toLowerCamel(aliasName);
                case ParamInfo.NameCaseType.UNDER_SCORE_CASE -> StringUtilsPlus.toUnderline(aliasName, false);
                case ParamInfo.NameCaseType.UPPER_UNDER_SCORE_CASE ->
                        StringUtilsPlus.toUnderline(aliasName.toUpperCase(), true);
                default -> aliasName;
            };
            //转换后
            fieldInfo.setFieldName(fieldName);

            //无法推测类型，所有都set为String
            fieldInfo.setFieldClass("String");
            fieldList.add(fieldInfo);
        });
        classInfo.setFieldList(fieldList);
        log.info("classInfo:{}", JSON.toJSONString(classInfo));
        return classInfo;
    }

    @Override
    public ClassInfo generateCreateSqlBySQLPraser(ParamInfo paramInfo) throws Exception {
        ClassInfo classInfo = new ClassInfo();
        Statement statement = null;
        // 对SQL进行预处理，以提高解析成功率
        String processedSql = paramInfo.getTableSql().trim()
                .replaceAll("'", "`")          // 将单引号替换为反引号
                .replaceAll("\"", "`")         // 将双引号替换为反引号
                .replaceAll("，", ",");        // 将中文逗号替换为英文逗号

        try {
            statement = CCJSqlParserUtil.parse(processedSql);
        }catch (Exception e) {
            e.printStackTrace();
            throw new SqlParseException("SQL语法错误:"+e.getMessage());
        }

        // 确保是CREATE TABLE语句
        if (!(statement instanceof CreateTable createTable)) {
            throw new SqlParseException("检测到SQL语句不是DLL CREATE TABLE语句");
        }

        // 提取表名
        String tableName = createTable.getTable().getName().replaceAll("`", "");
        classInfo.setTableName(tableName);
        String className = StringUtilsPlus.upperCaseFirst(StringUtilsPlus.underlineToCamelCase(tableName));
        if (className.contains("_")) {
            className = className.replaceAll("_", "");
        }
        classInfo.setClassName(className);
        classInfo.setOriginTableName(tableName);
        classInfo.setClassComment(paramInfo.getTableSql());
        
        // 提取字段信息
        List<FieldInfo> fieldList = new ArrayList<FieldInfo>();
        List<ColumnDefinition> columnDefinitions = createTable.getColumnDefinitions();
        
        if (columnDefinitions != null) {
            for (ColumnDefinition columnDefinition : columnDefinitions) {
                FieldInfo fieldInfo = new FieldInfo();
                String columnName = columnDefinition.getColumnName().replaceAll("`", "");
                fieldInfo.setColumnName(columnName);
                fieldInfo.setFieldComment(columnDefinition.toString());
                
                // 根据命名规则转换字段名
                String fieldName = switch ((String) paramInfo.getOptions().get("nameCaseType")) {
                    case ParamInfo.NameCaseType.CAMEL_CASE -> StringUtilsPlus.toLowerCamel(columnName);
                    case ParamInfo.NameCaseType.UNDER_SCORE_CASE -> StringUtilsPlus.toUnderline(columnName, false);
                    case ParamInfo.NameCaseType.UPPER_UNDER_SCORE_CASE ->
                            StringUtilsPlus.toUnderline(columnName.toUpperCase(), true);
                    default -> columnName;
                };
                fieldInfo.setFieldName(fieldName);
                
                // 设置字段类型为String（因为无法准确推测类型）
                fieldInfo.setFieldClass("String");
                fieldList.add(fieldInfo);
            }
        }
        
        classInfo.setFieldList(fieldList);
        log.info("classInfo:{}", JSON.toJSONString(classInfo));
        return classInfo;
    }

    @Override
    public ClassInfo processTableIntoClassInfo(ParamInfo paramInfo) throws Exception {
        //process the param
        String tableSql = paramInfo.getTableSql();
        String nameCaseType = MapUtil.getString(paramInfo.getOptions(),"nameCaseType");
        String isPackageType = MapUtil.getString(paramInfo.getOptions(),"isPackageType");

        //更新空值处理
        if (StringUtils.isBlank(tableSql)) {
            throw new Exception("Table structure can not be empty. 表结构不能为空。");
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
            tableName = tableSql.substring(tableKwIx + 5, tableSql.indexOf("("));
        } else {
            throw new Exception("Table structure incorrect.表结构不正确。");
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
        if(tableName!=null && StringUtilsPlus.isNotNull(MapUtil.getString(paramInfo.getOptions(),"ignorePrefix"))){
            tableName = tableName.replaceAll(MapUtil.getString(paramInfo.getOptions(),"ignorePrefix"),"");
        }
        // class Name
        String className = StringUtilsPlus.upperCaseFirst(StringUtilsPlus.underlineToCamelCase(tableName));
        if (className.contains("_")) {
            className = className.replaceAll("_", "");
        }

        // class Comment
        String classComment = null;
        //mysql是comment=,pgsql/oracle是comment on table,
        //2020-05-25 优化表备注的获取逻辑
        if (tableSql.toLowerCase().contains("comment=") || tableSql.toLowerCase().contains("comment on table")) {
            int ix = tableSql.toLowerCase().lastIndexOf("comment=");
            String classCommentTmp = (ix > -1) ?
                    tableSql.substring(ix + 8).trim() :
                    tableSql.substring(tableSql.toLowerCase().lastIndexOf("comment on table") + 17).trim();
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
        String fieldListTmp = tableSql.substring(tableSql.indexOf("(") + 1, tableSql.lastIndexOf(")")).trim();

        // 匹配 comment，替换备注里的小逗号, 防止不小心被当成切割符号切割
        String commentPattenStr1 = "comment `(.*?)\\`";
        Matcher matcher1 = Pattern.compile(commentPattenStr1).matcher(fieldListTmp.toLowerCase());
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
                i++;
                String columnLine = columnLine0.replaceAll("\n", "").replaceAll("\t", "").trim();
                // `userid` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
                // 2018-9-18 zhengk 修改为contains，提升匹配率和匹配不按照规矩出牌的语句
                // 2018-11-8 zhengkai 修复tornadoorz反馈的KEY FK_permission_id (permission_id),KEY FK_role_id (role_id)情况
                // 2019-2-22 zhengkai 要在条件中使用复杂的表达式
                // 2019-4-29 zhengkai 优化对普通和特殊storage关键字的判断（感谢@AhHeadFloating的反馈 ）
                // 2020-10-20 zhengkai 优化对fulltext/index关键字的处理（感谢@WEGFan的反馈）
                // 2025-12-07 zhengkai 修复对primary key的处理
                boolean notSpecialFlag = (
                        !columnLine.contains("key ")
                                && !columnLine.toLowerCase().contains("constraint")
                                && !columnLine.toLowerCase().contains(" using ")
                                && !columnLine.toLowerCase().contains("unique ")
                                && !columnLine.toLowerCase().contains("fulltext ")
                                && !columnLine.toLowerCase().contains("index ")
                                && !columnLine.toLowerCase().contains("pctincrease")
                                && !columnLine.toLowerCase().contains("buffer_pool")
                                && !columnLine.toLowerCase().contains("tablespace")
                                && !(columnLine.toLowerCase().contains("primary ") && columnLine.indexOf("storage") + 3 > columnLine.indexOf("("))
                                && !(columnLine.toLowerCase().contains("primary ") && i > 3)
                                && !columnLine.toLowerCase().contains("primary key")
                );

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
                        columnName = columnLine.substring(0, columnLine.indexOf(" "));
                    } catch (StringIndexOutOfBoundsException e) {
                        System.out.println("err happened: " + columnLine);
                        throw e;
                    }

                    // field Name
                    // 2019-09-08 yj 添加是否下划线转换为驼峰的判断
                    // 2023-8-27 L&J 支持原始列名任意命名风格, 不依赖用户是否输入下划线
                    String fieldName = null;
                    if (ParamInfo.NameCaseType.CAMEL_CASE.equals(nameCaseType)) {
                        // 2024-1-27 L&J 适配任意(maybe)原始风格转小写驼峰
                        fieldName = StringUtilsPlus.toLowerCamel(columnName);
                    } else if (ParamInfo.NameCaseType.UNDER_SCORE_CASE.equals(nameCaseType)) {
                        fieldName = StringUtilsPlus.toUnderline(columnName, false);
                    } else if (ParamInfo.NameCaseType.UPPER_UNDER_SCORE_CASE.equals(nameCaseType)) {
                        fieldName = StringUtilsPlus.toUnderline(columnName.toUpperCase(), true);
                    } else {
                        fieldName = columnName;
                    }
                    // 修复Oracle字段名不带引号的情况
                    if (columnLine.contains("`")) {
                        columnLine = columnLine.substring(columnLine.indexOf("`") + 1).trim();
                    }
                    //2025-03-16 修复由于类型大写导致无法转换的问题
                    String mysqlType = columnLine.split("\\s+")[1].toLowerCase();
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
                    if (tableSql.toLowerCase().contains("comment on column") && (tableSql.toLowerCase().contains("." + columnName + " is ") || tableSql.toLowerCase().contains(".`" + columnName + "` is"))) {
                        //新增对pgsql/oracle的字段备注支持
                        //COMMENT ON COLUMN public.check_info.check_name IS '检查者名称';
                        //2018-11-22 lshz0088 正则表达式的点号前面应该加上两个反斜杠，否则会认为是任意字符
                        //2019-4-29 zhengkai 优化对oracle注释comment on column的支持（@liukex）
                        tableSql = tableSql.toLowerCase().replaceAll(".`" + columnName + "` is", "." + columnName + " is");
                        Matcher columnCommentMatcher = Pattern.compile("\\." + columnName + " is `").matcher(tableSql.toLowerCase());
                        fieldComment = columnName;
                        while (columnCommentMatcher.find()) {
                            String columnCommentTmp = columnCommentMatcher.group();
                            //System.out.println(columnCommentTmp);
                            fieldComment = tableSql.substring(tableSql.indexOf(columnCommentTmp) + columnCommentTmp.length()).trim();
                            fieldComment = fieldComment.substring(0, fieldComment.indexOf("`")).trim();
                        }
                    } else if (columnLine.toLowerCase().contains(" comment")) {
                        //20200518 zhengkai 修复包含comment关键字的问题
                        String commentTmp = columnLine.toLowerCase().substring(columnLine.toLowerCase().lastIndexOf("comment") + 7).trim();
                        // '用户ID',
                        if (commentTmp.contains("`") || commentTmp.indexOf("`") != commentTmp.lastIndexOf("`")) {
                            commentTmp = commentTmp.substring(commentTmp.indexOf("`") + 1, commentTmp.lastIndexOf("`"));
                        }
                        //解决最后一句是评论，无主键且连着)的问题:album_id int(3) default '1' null comment '相册id：0 代表头像 1代表照片墙')
                        if (commentTmp.contains(")")) {
                            commentTmp = commentTmp.substring(0, commentTmp.lastIndexOf(")") + 1);
                        }
                        fieldComment = commentTmp;
                    } else if (columnLine.contains("--")) {
                        // 支持Oracle风格的注释（--）
                        fieldComment = columnLine.substring(columnLine.indexOf("--") + 2).trim();
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

        if (fieldList.isEmpty()) {
            throw new Exception("表结构分析失败，请检查语句或者提交issue给我");
        }
        //build Class Info
        ClassInfo codeJavaInfo = new ClassInfo();
        codeJavaInfo.setTableName(tableName);
        codeJavaInfo.setClassName(className);
        codeJavaInfo.setClassComment(classComment);
        codeJavaInfo.setFieldList(fieldList);
        codeJavaInfo.setOriginTableName(originTableName);

        return codeJavaInfo;
    }

    @Override
    public ClassInfo processTableToClassInfoByRegex(ParamInfo paramInfo) {
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

        Matcher matcher = Pattern.compile(DDL_PATTEN_STR).matcher(paramInfo.getTableSql().trim());
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

    @Override
    public ClassInfo processInsertSqlToClassInfo(ParamInfo paramInfo) {
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
            String[] values = valueStr.split(",");
            for (String column : values) {
                valueList.add(column);
            }
            AtomicInteger n = new AtomicInteger(0);
            //add column to fleldList
            String[] columns = columnsSQL.replaceAll(" ", "").split(",");
            for (String column : columns) {
                FieldInfo fieldInfo2 = new FieldInfo();
                fieldInfo2.setFieldName(column);
                fieldInfo2.setColumnName(column);
                fieldInfo2.setFieldClass(String.class.getSimpleName());
                if (n.get() < valueList.size()) {
                    fieldInfo2.setFieldComment(column + " , eg." + valueList.get(n.get()));
                }
                fieldList.add(fieldInfo2);
                n.getAndIncrement();
            }

        }
        if (fieldList.size() < 1) {
            throw new RuntimeException("INSERT SQL解析失败");
        }
        codeJavaInfo.setFieldList(fieldList);
        return codeJavaInfo;
    }
}