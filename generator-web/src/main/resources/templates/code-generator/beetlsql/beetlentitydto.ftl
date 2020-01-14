import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * @description ${classInfo.classComment}
 * @author ${authorName}
 * @date ${.now?string('yyyy-MM-dd HH:mm:ss')}
 */
@Data
@ApiModel("${classInfo.classComment}")
public class ${classInfo.className}DTO {

<#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
<#list classInfo.fieldList as fieldItem >
    @ApiModelProperty("${fieldItem.fieldComment}")
    private ${fieldItem.fieldClass} ${fieldItem.fieldName};

</#list>
    public ${classInfo.className}() {
    }
</#if>

}
