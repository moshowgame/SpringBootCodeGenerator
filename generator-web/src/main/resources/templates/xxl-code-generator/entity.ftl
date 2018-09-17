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
import java.util.Date;
import java.util.List;

/**
*  ${classInfo.classComment}
* @author ${authorName} ${.now?string('yyyy-MM-dd')}
*/
@Entity
@Data
@Table(name="${classInfo.tableName}")
public class ${classInfo.className} implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue
<#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
<#list classInfo.fieldList as fieldItem >
    /**
    * ${fieldItem.fieldComment}
    */
    private ${fieldItem.fieldClass} ${fieldItem.fieldName};

</#list>
</#if>

}