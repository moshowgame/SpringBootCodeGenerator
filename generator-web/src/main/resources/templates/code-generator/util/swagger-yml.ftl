
<#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
${classInfo.className}:
  type: "object"
  properties:
<#list classInfo.fieldList as fieldItem >
    ${fieldItem.fieldName}:
      type: ${fieldItem.swaggerClass}
      description:  <#if isComment?exists && isComment==true>"${fieldItem.fieldComment}"</#if>
</#list>
</#if>

