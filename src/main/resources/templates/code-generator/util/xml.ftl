<!--
 ${classInfo.classComment}对象Get Set
 @author ${authorName} ${.now?string('yyyy-MM-dd')}
-->
<#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
<${classInfo.className}>
<#list classInfo.fieldList as fieldItem>
 <${fieldItem.fieldName}>${fieldItem.fieldComment}</${fieldItem.fieldName}>
</#list>
</${classInfo.className}>
</#if>
