<#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
INSERT INTO ${classInfo.tableName} ( <#list classInfo.fieldList as fieldItem >${fieldItem.columnName}<#if fieldItem_has_next>,</#if></#list> )
VALUES
    (
    <#list classInfo.fieldList as fieldItem >
    ''<#if fieldItem_has_next>,</#if>
    </#list>
    );
</#if>
