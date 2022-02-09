<#if isWithPackage?exists && isWithPackage==true>package ${packageName}.repository;</#if>
<#if isAutoImport?exists && isAutoImport==true>import ${packageName}.entity.${classInfo.className};

<#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
    <#list classInfo.fieldList as fieldItem >
        <#if fieldItem.fieldClass == "Date">
            <#assign importDdate = true />
        </#if>
    </#list>
</#if>
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
</#if>
/**
 * @description ${classInfo.classComment}
 * @author ${authorName}
 * @date ${.now?string('yyyy-MM-dd')}
 */
@Repository
public interface ${classInfo.className}Repository extends JpaRepository<${classInfo.className},Integer> {



}
