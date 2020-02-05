
import java.util.List;

/**
 * @description ${classInfo.classComment}
 * @author ${authorName}
 * @date ${.now?string('yyyy-MM-dd HH:mm:ss')}
 */
public interface I${classInfo.className}DAO {

    int add(${classInfo.className} ${classInfo.className?uncap_first});

    int update(${classInfo.className} ${classInfo.className?uncap_first});

    int delete(int id);

    ${classInfo.className} findById(int id);

    List<${classInfo.className}> findAllList(Map<String,Object> param);

}
