import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

/**
* ${classInfo.classComment}
* @author ${authorName} ${.now?string('yyyy-MM-dd')}
*/
@RestController
@RequestMapping("/${classInfo.className?uncap_first}")
public class ${classInfo.className}Controller {

    @Autowired
    private SQLManager sqlManager;

    /**
    * 新增或编辑
    */
    @PostMapping("/save")
    public Object save(${classInfo.className} ${classInfo.className?uncap_first}){
        ${classInfo.className} ${classInfo.className?uncap_first}=sqlManager.unique(${classInfo.className}.class,${classInfo.className?uncap_first}.getId());
        if(${classInfo.className?uncap_first}!=null){
            sqlManager.updateById(${classInfo.className?uncap_first});
            return ApiReturnUtil.success("编辑成功");
        }else{
            sqlManager.insert(${classInfo.className?uncap_first});
            return ApiReturnUtil.error("保存成功");
        }
    }

    /**
    * 删除
    */
    @PostMapping("/delete")
    public Object delete(int id){
        ${classInfo.className} ${classInfo.className?uncap_first}=sqlManager.unique(${classInfo.className}.class,id);
        if(${classInfo.className?uncap_first}!=null){
            sqlManager.deleteById(id);
            return ApiReturnUtil.success("删除成功");
        }else{
            return ApiReturnUtil.error("没有找到该对象");
        }
    }

    /**
    * 查询
    */
    @PostMapping("/find")
    public Object find(int id){
        ${classInfo.className} ${classInfo.className?uncap_first}=sqlManager.unique(${classInfo.className}.class,id);
        if(${classInfo.className?uncap_first}!=null){
            return ApiReturnUtil.success(${classInfo.className?uncap_first});
        }else{
            return ApiReturnUtil.error("没有找到该对象");
        }
    }

    /**
    * 分页查询
    */
    @PostMapping("/list")
    public Object list(${classInfo.className} ${classInfo.className?uncap_first},
                        @RequestParam(required = false, defaultValue = "0") int pageNumber,
                        @RequestParam(required = false, defaultValue = "10") int pageSize) {
            List<${classInfo.className}> list = sqlManager.query(${classInfo.className}.class).select();
            return ApiReturnUtil.error(list);
    }

}
