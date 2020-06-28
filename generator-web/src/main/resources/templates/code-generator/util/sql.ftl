
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

<#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
    SELECT
        *
    FROM
    ${classInfo.tableName} t1
    LEFT JOIN xxx t2
    ON t1.${classInfo.tableName}_id=t2.${classInfo.tableName}_id
    WHERE 1=1;
</#if>

<#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
    INSERT INTO ${classInfo.tableName} ( <#list classInfo.fieldList as fieldItem >${fieldItem.columnName}<#if fieldItem_has_next>,</#if></#list> )
    VALUES
    (
    <#list classInfo.fieldList as fieldItem >
        ''<#if fieldItem_has_next>,</#if>
    </#list>
    );
</#if>


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



<#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
    DELETE
    FROM
    ${classInfo.tableName}
    WHERE
    <#list classInfo.fieldList as fieldItem >
        ${fieldItem.columnName} = ''<#if fieldItem_has_next>,</#if>
    </#list>;
</#if>
