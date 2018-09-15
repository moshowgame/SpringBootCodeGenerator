import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

/**
* ${classInfo.classComment}
*
* Created by by-health on '${.now?string('yyyy-MM-dd HH:mm:ss')}'.
*/
@RestController
@RequestMapping("/${classInfo.className?uncap_first}")
public class ${classInfo.className}Controller {

    @Autowired
    private ${classInfo.className}Respository ${classInfo.className?uncap_first}Respository;

    /**
    * 新增或编辑
    */
    @RequestMapping("/save")
    public Object save(${classInfo.className} ${classInfo.className?uncap_first}){
        return ${classInfo.className?uncap_first}Respository.save(${classInfo.className?uncap_first});
    }

    /**
    * 删除
    */
    @RequestMapping("/delete")
    public Object delete(int id){
         ${classInfo.className?uncap_first}Respository.deleteById(id);
        return 1;
    }

    /**
    * 查询
    */
    @RequestMapping("/find")
    public Object load(int id){
        return ${classInfo.className?uncap_first}Respository.findById(id);
    }

    /**
    * 分页查询
    */
    @RequestMapping("/list")
    public Object list(${classInfo.className} ${classInfo.className?uncap_first},
                                    @RequestParam(required = false, defaultValue = "0") int pageNumber,
                                    @RequestParam(required = false, defaultValue = "10") int pageSize) {

            //创建匹配器，需要查询条件请修改此处代码
            ExampleMatcher matcher = ExampleMatcher.matchingAll();

            //创建实例
            Example<${classInfo.className}> example = Example.of(${classInfo.className?uncap_first}, matcher);
            //分页构造
            Pageable pageable = PageRequest.of(pageNumber,pageSize);

            return ${classInfo.className?uncap_first}Respository.findAll(example, pageable);
    }

}
