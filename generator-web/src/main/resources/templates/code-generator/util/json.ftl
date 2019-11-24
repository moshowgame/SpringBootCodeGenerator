<#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
{
<#list classInfo.fieldList as fieldItem>
 "${fieldItem.fieldName}":""<#if fieldItem_has_next>,</#if>
</#list>
}
</#if>
