<#if isWithPackage?exists && isWithPackage==true>package ${packageName}.mapper;</#if>
<#if isAutoImport?exists && isAutoImport==true>
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import ${packageName}.entity.${classInfo.className};
import java.util.List;
</#if>
/**
 * @description ${classInfo.classComment}Mapper
 * @author ${authorName}
 * @date ${.now?string('yyyy-MM-dd')}
 */
@Mapper
public interface ${classInfo.className}Mapper extends BaseMapper<${classInfo.className}> {

    /**
     * 动态条件分页查询 - 根据对象属性自动构建条件
     * 如果字段有值则进行分页+指定条件查询，否则仅进行分页查询
     */
    @Select("""
        <script>
        SELECT * FROM ${classInfo.tableName}
        <where>
        <#list classInfo.fieldList as fieldItem>
            <#if fieldItem.fieldClass?contains("String")>
            <if test='queryParamDTO.${fieldItem.fieldName} != null and queryParamDTO.${fieldItem.fieldName} != ""'>
                AND ${fieldItem.columnName} = 井{queryParamDTO.${fieldItem.fieldName}}
            </if>
            <#else>
            <if test='queryParamDTO.${fieldItem.fieldName} != null'>
                AND ${fieldItem.columnName} = 井{queryParamDTO.${fieldItem.fieldName}}
            </if>
            </#if>
        </#list>
        </where>
        ORDER BY id DESC
        LIMIT 井{offset}, 井{limit}
        </script>
    """)
    List<${classInfo.className}> selectPageByCondition(@Param("queryParamDTO") ${classInfo.className} queryParamDTO, 
                                                     @Param("offset") int offset, 
                                                     @Param("limit") int limit);

    /**
     * 动态条件分页查询总数
     */
    @Select("""
        <script>
        SELECT COUNT(*) FROM ${classInfo.tableName}
        <where>
        <#list classInfo.fieldList as fieldItem>
            <#if fieldItem.fieldClass?contains("String")>
            <if test='queryParamDTO.${fieldItem.fieldName} != null and queryParamDTO.${fieldItem.fieldName} != ""'>
                AND ${fieldItem.columnName} = 井{queryParamDTO.${fieldItem.fieldName}}
            </if>
            <#else>
            <if test='queryParamDTO.${fieldItem.fieldName} != null'>
                AND ${fieldItem.columnName} = 井{queryParamDTO.${fieldItem.fieldName}}
            </if>
            </#if>
        </#list>
        </where>
        </script>
    """)
    int selectPageByConditionCount(@Param("queryParamDTO") ${classInfo.className} queryParamDTO);

}
