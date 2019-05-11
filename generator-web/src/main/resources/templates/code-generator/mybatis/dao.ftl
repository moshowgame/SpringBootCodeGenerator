import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
* ${classInfo.classComment}
* @author ${authorName} ${.now?string('yyyy-MM-dd')}
*/
@Repository
public interface ${classInfo.className}Dao {

    /**
    * 新增
    * @param ${classInfo.className}Entity
    */
    public Integer insert(@Param("${classInfo.className?uncap_first}") ${classInfo.className}Entity ${classInfo.className?uncap_first});

    /**
    * 删除
    * @param id
    */
    public Integer delete(@Param("id") Integer id);

    /**
    * 更新
    * @param ${classInfo.className}Entity
    */
    public Integer update(@Param("${classInfo.className?uncap_first}") ${classInfo.className}Entity ${classInfo.className?uncap_first});

    /**
    * 根据主键查询一个实体
    * @param id
    */
    public ${classInfo.className}Entity selectByPrimaryKey(@Param("id") Integer id);

    /**
    * 分页查询Data
    * @param offset
    * @param pageSize
    */
    public List<${classInfo.className}Entity> pageList(@Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

    /**
    * 分页查询Count
    */
    public Integer pageListCount();

}