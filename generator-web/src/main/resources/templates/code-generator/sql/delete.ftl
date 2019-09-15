<#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
DELETE
FROM
    ${classInfo.tableName}
WHERE
<#list classInfo.fieldList as fieldItem >
    ${fieldItem.columnName} = ''<#if fieldItem_has_next>,</#if>
</#list>;
</#if>
