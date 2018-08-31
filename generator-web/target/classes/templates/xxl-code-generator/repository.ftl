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

/**
*  ${classInfo.classComment}
*
*  Created by xuxueli on '${.now?string('yyyy-MM-dd HH:mm:ss')}'.
*/
@Repository
public interface ${classInfo.className}Respotory extends JpaRepository<${classInfo.className},Integer> {



}
