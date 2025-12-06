<#if isWithPackage?exists && isWithPackage==true>package ${packageName}.controller;</#if>
<#if isAutoImport?exists && isAutoImport==true>
import ${packageName}.entity.${classInfo.className};
import ${packageName}.mapper.${classInfo.className}Mapper;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@RestController
@RequestMapping("/${classInfo.className?uncap_first}")
public class ${classInfo.className}Controller {

    @Autowired
    private ${classInfo.className}Mapper ${classInfo.className?uncap_first}Mapper;

    /**
    * 新增或编辑
    */
    @PostMapping("/save")
    public Object save(${classInfo.className} ${classInfo.className?uncap_first}){
        if(${classInfo.className?uncap_first}Mapper.selectCount(${classInfo.className?uncap_first})>0){
            ${classInfo.className?uncap_first}Mapper.insert(${classInfo.className?uncap_first});
        }else{
            ${classInfo.className?uncap_first}Mapper.updateByPrimaryKeySelective(${classInfo.className?uncap_first});
        }
        return ${returnUtilSuccess}("新增或编辑成功");
    }

    /**
    * 删除
    */
    @PostMapping("/delete")
    public Object delete(int id){
        if(${classInfo.className?uncap_first}Mapper.selectCount(${classInfo.className?uncap_first})>0){
            ${classInfo.className?uncap_first}Mapper.deleteByPrimaryKey(id);
            return ${returnUtilSuccess}("删除成功");
        }else{
            return ${returnUtilFailure}("没有找到该对象");
        }
    }

    /**
    * 查询
    */
    @PostMapping("/find")
    public Object find(int id){
        Optional<${classInfo.className}> ${classInfo.className?uncap_first}=${classInfo.className?uncap_first}Mapper.selectOne(id);
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
    public Object list(${classInfo.className} ${classInfo.className?uncap_first},
                        @RequestParam(required = false, defaultValue = "0") int pageNumber,
                        @RequestParam(required = false, defaultValue = "10") int pageSize) {
            //TBC
            return ${classInfo.className?uncap_first}Mapper.selectList(${classInfo.className?uncap_first});
    }

}
