<#if isWithPackage?exists && isWithPackage==true>package ${packageName}.controller;</#if>
<#if isAutoImport?exists && isAutoImport==true>
import ${packageName}.entity.${classInfo.className};
import ${packageName}.repository.${classInfo.className}Repository;
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
import java.util.Optional;

</#if>
/**
 * @description ${classInfo.classComment}
 * @author ${authorName}
 * @date ${.now?string('yyyy-MM-dd')}
 */

@Slf4j
@Api(tags = "${classInfo.className?uncap_first}")
@CrossOrigin
@RestController
@RequestMapping("/${classInfo.className?uncap_first}")
public class ${classInfo.className}Controller {

    @Autowired
    private ${classInfo.className}Repository ${classInfo.className?uncap_first}Repository;

    /**
    * 新增或编辑
    */
    @PostMapping("/save")
    @ApiOperation(value = "save ${classInfo.className}", notes = "save ${classInfo.className}")
    public Object save(@RequestBody ${classInfo.className} ${classInfo.className?uncap_first}){
        try {
            return ReturnT.success(${classInfo.className?uncap_first}Repository.save(${classInfo.className?uncap_first}));
        } catch (Exception e) {
            e.printStackTrace();
            return ReturnT.error("保存失败");
        }

    }

    /**
    * 删除
    */
    @PostMapping("/delete")
    @ApiOperation(value = "delete ${classInfo.className}", notes = "delete ${classInfo.className}")
    public Object delete(int id){
        Optional<${classInfo.className}> ${classInfo.className?uncap_first}=${classInfo.className?uncap_first}Repository.findById(id);
        if(${classInfo.className?uncap_first}.isPresent()){
            ${classInfo.className?uncap_first}Repository.deleteById(id);
            return ${returnUtilSuccess}("删除成功");
        }else{
            return ${returnUtilFailure}("没有找到该对象");
        }
    }

    /**
    * 查询
    */
    @PostMapping("/find")
    @ApiOperation(value = "find ${classInfo.className} by id", notes = "find ${classInfo.className} by id")
    public Object find(int id){
        Optional<${classInfo.className}> ${classInfo.className?uncap_first}=${classInfo.className?uncap_first}Repository.findById(id);
        if(${classInfo.className?uncap_first}.isPresent()){
            return ${returnUtilSuccess}(${classInfo.className?uncap_first}.get());
        }else{
            return ${returnUtilFailure}("没有找到该对象");
        }
    }

    /**
    * 分页查询
    */
    @PostMapping("/list")
    @ApiOperation(value = "list ${classInfo.className}", notes = "list ${classInfo.className}")
    public Object list(@RequestBody ${classInfo.className} ${classInfo.className?uncap_first},
                        @RequestParam(required = false, defaultValue = "0") int pageNumber,
                        @RequestParam(required = false, defaultValue = "10") int pageSize) {

            try {
                //创建匹配器，需要查询条件请修改此处代码
                ExampleMatcher matcher = ExampleMatcher.matchingAll();

                //创建实例
                Example<${classInfo.className}> example = Example.of(${classInfo.className?uncap_first}, matcher);
                //分页构造
                Pageable pageable = PageRequest.of(pageNumber,pageSize);

                return ReturnT.success(${classInfo.className?uncap_first}Repository.findAll(example, pageable));

            } catch (Exception e) {
                e.printStackTrace();
                return ReturnT.error(e.getMessage());
            }

    }

}
