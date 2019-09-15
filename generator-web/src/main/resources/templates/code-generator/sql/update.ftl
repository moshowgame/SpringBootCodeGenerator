<#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
UPDATE ${classInfo.tableName}
SET
<#list classInfo.fieldList as fieldItem >
    ${fieldItem.columnName} = ''<#if fieldItem_has_next>,</#if>
</#list>
WHERE
<#list classInfo.fieldList as fieldItem >
    ${fieldItem.columnName} = ''<#if fieldItem_has_next>,</#if>
</#list>;
</#if>
