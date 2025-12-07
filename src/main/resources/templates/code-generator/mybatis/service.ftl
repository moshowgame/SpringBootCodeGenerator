<#if isAutoImport?exists && isAutoImport==true>
import java.util.Map;
</#if>
/**
 * @description ${classInfo.classComment}
 * @author ${authorName}
 * @date ${.now?string('yyyy-MM-dd')}
 */
public interface ${classInfo.className}Service {

    /**
    * 新增
    */
    public Object insert(${classInfo.className} ${classInfo.className?uncap_first});

    /**
    * 删除
    */
    public Object delete(int id);

    /**
    * 更新
    */
    public Object update(${classInfo.className} ${classInfo.className?uncap_first});

    /**
    * 根据主键 id 查询
    */
    public ${classInfo.className} find(int id);

    /**
    * 分页查询
    */
    public Map<String,Object> pageList(int offset, int pagesize);

    /**
    * 动态条件分页查询 - 根据对象属性自动构建条件
    * 如果字段有值则进行分页+指定条件查询，否则仅进行分页查询
    */
    public Map<String,Object> pageByCondition(${classInfo.className} queryParamDTO, int offset, int pagesize);

}
