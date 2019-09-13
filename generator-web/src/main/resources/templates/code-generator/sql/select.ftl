<#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
SELECT
<#list classInfo.fieldList as fieldItem >
    ${fieldItem.columnName}<#if fieldItem_has_next>,</#if>
</#list>
FROM
    ${classInfo.tableName}
WHERE
<#list classInfo.fieldList as fieldItem >
    <#if fieldItem_index != 0>AND </#if>${fieldItem.columnName} = ''
</#list>;
</#if>

