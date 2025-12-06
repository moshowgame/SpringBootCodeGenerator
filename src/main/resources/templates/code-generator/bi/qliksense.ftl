//***************************
//[${classInfo.classComment} - ${classInfo.tableName}]
//AUTHOR ${authorName}
//HISTORY ${.now?string('yyyy-MM-dd')}
//***************************

//***************************
//load all
[${classInfo.tableName}]:
LOAD * FROM ['LIB://QVD/${classInfo.className}.qvd'](qvd);

//***************************
//load columns
[${classInfo.tableName}]:
LOAD
<#list classInfo.fieldList as fieldItem >
    "${fieldItem.columnName}" as "${fieldItem.fieldName}"<#if fieldItem_has_next>,</#if>
</#list>
FROM
['LIB://QVD/${classInfo.className}.qvd'](qvd);
;

//load inline
[${classInfo.tableName}]:
LOAD * INLINE
[
<#list classInfo.fieldList as fieldItem >${fieldItem.columnName} <#if fieldItem_has_next>,</#if></#list>
<#list classInfo.fieldList as fieldItem >${fieldItem.fieldName}  <#if fieldItem_has_next>,</#if></#list>
<#list classInfo.fieldList as fieldItem >${fieldItem.fieldComment}  <#if fieldItem_has_next>,</#if></#list>
];

//***************************
//load from api data connection (wrap on)
LIB CONNECT TO '${classInfo.tableName}_api';

RestConnectorMasterTable:
    SQL SELECT
    "__KEY_root",
    (SELECT
<#list classInfo.fieldList as fieldItem >
    "${fieldItem.columnName}"
</#list>
        "__FK_object"
    FROM "object" FK "__FK_object")
    FROM JSON (wrap on) "root" PK "__KEY_root"
//    WITH CONNECTION (
//    Url "https://localhost:8080/${classInfo.tableName}_api",
//    QUERY "page" "1",
//    QUERY "size" "100",
//    HTTPHEADER "token" "123456",
//    BODY "Post body here")
;

[${classInfo.className}]:
LOAD
<#list classInfo.fieldList as fieldItem >
    [${fieldItem.columnName}] as [${fieldItem.fieldName}]
</#list>
    [__FK_object] AS [__KEY_root]
RESIDENT RestConnectorMasterTable
WHERE NOT IsNull([__FK_stores]);

DROP TABLE [${classInfo.className}];
DROP TABLE RestConnectorMasterTable;

//***************************
//load from api data connection (wrap off)
LIB CONNECT TO '${classInfo.tableName}_api';
[${classInfo.className}]:
SQL SELECT
<#list classInfo.fieldList as fieldItem >
    [${fieldItem.fieldName}] as [${fieldItem.fieldName}]<#if fieldItem_has_next>,</#if>
</#list>
FROM JSON(wrap off) "${classInfo.className}"
//    WITH CONNECTION (
//    Url "https://localhost:8080/${classInfo.tableName}_api",
//    QUERY "page" "1",
//    QUERY "size" "100",
//    HTTPHEADER "token" "123456",
//    BODY "Post body here")
;

//***************************
//load from sql data connection
LIB CONNECT TO '${classInfo.tableName}_db';

SQL SELECT
<#list classInfo.fieldList as fieldItem >
    [${fieldItem.columnName}] as [${fieldItem.fieldName}]<#if fieldItem_has_next>,</#if>
</#list>
FROM
     ${classInfo.tableName}
WHERE
     Create_Time > '2023-01-01 00:00:00';