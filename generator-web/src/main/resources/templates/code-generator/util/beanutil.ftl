/**
* ${classInfo.classComment}对象Get Set
* @author ${authorName} ${.now?string('yyyy-MM-dd')}
*/

<#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
<#list classInfo.fieldList as fieldItem>
// ${fieldItem.fieldComment}
${fieldItem.fieldClass} ${fieldItem.fieldName} = ${classInfo.className?uncap_first}.get${fieldItem.fieldName?cap_first}();
</#list>

<#list classInfo.fieldList as fieldItem>
// ${fieldItem.fieldComment}
${classInfo.className?uncap_first}.set${fieldItem.fieldName?cap_first}();
</#list>

<#list classInfo.fieldList as fieldItem>
// ${fieldItem.fieldComment}
${classInfo.className?uncap_first}.set${fieldItem.fieldName?cap_first}(${classInfo.className?uncap_first}2.get${fieldItem.fieldName?cap_first}());
</#list>

<#list classInfo.fieldList as fieldItem>
// ${fieldItem.fieldComment}
map.put("${fieldItem.fieldName?uncap_first}",${classInfo.className?uncap_first}.get${fieldItem.fieldName?cap_first}());
</#list>

<#list classInfo.fieldList as fieldItem>
// ${fieldItem.fieldComment}
map.put("${fieldItem.columnName}",${classInfo.className?uncap_first}.get${fieldItem.fieldName?cap_first}());
</#list>

<#list classInfo.fieldList as fieldItem>
map.put("${fieldItem.fieldComment}",${classInfo.className?uncap_first}.get${fieldItem.fieldName?cap_first}());
</#list>

<#list classInfo.fieldList as fieldItem>
// ${fieldItem.fieldComment}
map.put("${fieldItem.fieldName?uncap_first}",${classInfo.className?uncap_first}.get${fieldItem.fieldName?cap_first}());
</#list>

</#if>
