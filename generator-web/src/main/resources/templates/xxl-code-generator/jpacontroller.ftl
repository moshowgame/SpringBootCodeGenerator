import org.springframework.stereotype.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
        return ${classInfo.className?uncap_first}Respository.deleteById(id);
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

//创建匹配器，即如何使用查询条件
            ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                    //这里追加查询处理方式。例如xxx字段采用“包含”的方式查询
                    //.withMatcher("xxx", GenericPropertyMatchers.contains())
            ;

            //创建实例
            Example<${classInfo.className}> ex = Example.of(${classInfo.className?uncap_first}, matcher);
            //分页构造
            Pageable pageable = PageRequest.of(pageNumber,pageSize);

            return ${classInfo.className?uncap_first}Respository.findAll(matcher, pageable);
    }

}
