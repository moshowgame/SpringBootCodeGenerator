
SELECT * FROM 'your_project.your_dataset.${tableName}' t
order by t.id desc
LIMIT 100
;

SELECT * FROM 'your_project.your_dataset.${tableName}_error_records' t
order by t.timestamp desc
LIMIT 100
;

bigquery table -> SCHEMA -> Edit as text , then input below text:
[
<#list classInfo.fieldList as fieldItem >
    {"name":"${fieldItem.columnName}",type:"STRING","mode":"NULLABLE","description": "${fieldItem.fieldName} - ${fieldItem.fieldComment}"}<#if fieldItem_has_next>,</#if>
</#list>
]