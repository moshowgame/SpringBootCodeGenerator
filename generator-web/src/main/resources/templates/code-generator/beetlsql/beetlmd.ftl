sample
===

select #use("cols")# from ${classInfo.tableName} where #use("condition")#

cols
===
<#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
    <#list classInfo.fieldList as fieldItem >
        `${fieldItem.columnName}`<#if fieldItem_has_next>,</#if>
    </#list>
</#if>

updateSample
===
<#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
    <#list classInfo.fieldList as fieldItem >
        `${fieldItem.columnName}=#${fieldItem.fieldName}#`<#if fieldItem_has_next>,</#if>
    </#list>
</#if>

condition
===
    1 = 1
<#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
    <#list classInfo.fieldList as fieldItem >
    @if(!isEmpty(${fieldItem.fieldName})){
      and `${fieldItem.columnName}`=#${fieldItem.fieldName}#
    @}
    </#list>
</#if>
