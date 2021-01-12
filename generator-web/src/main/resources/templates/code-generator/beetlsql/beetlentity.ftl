<#if isAutoImport?exists && isAutoImport==true>
import java.io.Serializable;
import lombok.Data;
import java.util.Date;
import java.util.List;
</#if>
/**
 * @description ${classInfo.classComment}
 * @author ${authorName}
 * @date ${.now?string('yyyy-MM-dd')}
 */
@Data<#if isSwagger?exists && isSwagger==true>
@ApiModel("${classInfo.classComment}")</#if>
public class ${classInfo.className} implements Serializable {

    private static final long serialVersionUID = 1L;

<#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
<#list classInfo.fieldList as fieldItem >
    <#if isComment?exists && isComment==true>/**
    * ${fieldItem.fieldComment}
    */</#if><#if isSwagger?exists && isSwagger==true>
    @ApiModelProperty("${fieldItem.fieldComment}")</#if>
    private ${fieldItem.fieldClass} ${fieldItem.fieldName};

</#list>
    public ${classInfo.className}() {
    }
</#if>

}
