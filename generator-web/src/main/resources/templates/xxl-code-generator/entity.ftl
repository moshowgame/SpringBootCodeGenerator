<#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
    <#list classInfo.fieldList as fieldItem >
        <#if fieldItem.fieldClass == "Date">
            <#assign importDdate = true />
        </#if>
    </#list>
</#if>
import java.io.Serializable;
import javax.persistence.*;
import lombok.Data;
<#if importDdate>
import java.util.Date;
</#if>

/**
*  ${classInfo.classComment}
*
*  Created by by-health on '${.now?string('yyyy-MM-dd HH:mm:ss')}'.
*/
@Entity
@Data
@Table(name="${classInfo.tableName}")
public class ${classInfo.className} implements Serializable {
    private static final long serialVersionUID = 42L;

<#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
<#list classInfo.fieldList as fieldItem >
    /**
    * ${fieldItem.fieldComment}
    */
    private ${fieldItem.fieldClass} ${fieldItem.fieldName};

</#list>
</#if>

}