<#if isWithPackage?exists && isWithPackage==true>package ${packageName}.mapper;</#if>
<#if isAutoImport?exists && isAutoImport==true>
import ${packageName}.entity.${classInfo.className}Entity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
</#if>
/**
* @description ${classInfo.classComment}Mapper
* @author ${authorName}
* @date ${.now?string('yyyy-MM-dd')}
*/
@Mapper
public interface ${classInfo.className}Dao extends BaseMapper<${classInfo.className}Entity> {

}