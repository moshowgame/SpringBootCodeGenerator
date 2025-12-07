<#if isWithPackage?exists && isWithPackage==true>package ${packageName}.mapper;</#if>
<#if isAutoImport?exists && isAutoImport==true>
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;
</#if>
/**
 * @description ${classInfo.classComment}Mapper
 * @author ${authorName}
 * @date ${.now?string('yyyy-MM-dd')}
 */
@Mapper
@Repository
public interface ${classInfo.className}Mapper {

    @Select("""
        select * from ${classInfo.tableName} where ${classInfo.tableName}_id=井{id}
        """)
    public ${classInfo.className} find(Integer id);

    @Options(useGeneratedKeys=true,keyProperty="${classInfo.className?uncap_first}Id")
    @Insert("""
        insert into ${classInfo.tableName} (
            <#list classInfo.fieldList as fieldItem >${fieldItem.columnName}<#if fieldItem_has_next>,</#if></#list>
        ) values (
            <#list classInfo.fieldList as fieldItem >井{${fieldItem.fieldName}}<#if fieldItem_has_next>,<#else>)</#if></#list>
        )
        """)
    public Integer insert(${classInfo.className} ${classInfo.className?uncap_first});

    @Delete("""
        delete from ${classInfo.tableName} where ${classInfo.tableName}_id=井{id}
        """)
    boolean delete(Integer id);

    @Update("""
        update ${classInfo.tableName} set
        <#list classInfo.fieldList as fieldItem >
            <#if fieldItem.columnName != "id">${fieldItem.columnName}=井{${fieldItem.fieldName}}<#if fieldItem_has_next>,</#if></#if>
        </#list>
        where ${classInfo.tableName}_id=井{id}
        """)
    boolean update(${classInfo.className} ${classInfo.className?uncap_first});


    @Results(value = {
    <#list classInfo.fieldList as fieldItem >
        @Result(property = "${fieldItem.fieldName}", column = "${fieldItem.columnName}")<#if fieldItem_has_next>,</#if>
    </#list>
    })
    @Select("""
        select * from ${classInfo.tableName} where ${classInfo.tableName}_id=井{id}
        """)
    ${classInfo.className} selectOne(Integer id);

    @Results(value = {
    <#list classInfo.fieldList as fieldItem >
        @Result(property = "${fieldItem.fieldName}", column = "${fieldItem.columnName}")<#if fieldItem_has_next>,</#if>
    </#list>
    })
    @Select("""
        select * from ${classInfo.tableName} where
        <#list classInfo.fieldList as fieldItem >
            ${fieldItem.columnName}=井{${fieldItem.fieldName}}<#if fieldItem_has_next> or </#if>
        </#list>
        """)
    List<${classInfo.className}> selectList(${classInfo.className} ${classInfo.className?uncap_first});

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
    List<${classInfo.className}> pageByCondition(@Param("queryParamDTO") ${classInfo.className} queryParamDTO, 
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
    int pageByConditionCount(@Param("queryParamDTO") ${classInfo.className} queryParamDTO);

}