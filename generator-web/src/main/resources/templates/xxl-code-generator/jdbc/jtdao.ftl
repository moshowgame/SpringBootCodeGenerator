
import java.util.List;

/**
* ${classInfo.classComment}
* @author ${authorName} ${.now?string('yyyy-MM-dd')}
*/
public interface I${classInfo.className}DAO {

    int add(${classInfo.classComment} ${classInfo.className?uncap_first});

    int update(${classInfo.classComment} ${classInfo.className?uncap_first});

    int delete(int id);

    ${classInfo.classComment} findById(int id);

    List<${classInfo.classComment}> findAllList(Map<String,Object> param);

}
