<#if isAutoImport?exists && isAutoImport==true>
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
</#if>

/**
 * @description ${classInfo.classComment}
 * @author ${authorName}
 * @date ${.now?string('yyyy-MM-dd')}
 */
@RestController
@RequestMapping(value = "/${classInfo.className?uncap_first}")
public class ${classInfo.className}Controller {

    @Resource
    private ${classInfo.className}Service ${classInfo.className?uncap_first}Service;

    /**
    * 新增
    * @author ${authorName}
    * @date ${.now?string('yyyy/MM/dd')}
    **/
    @RequestMapping("/insert")
    public Object insert(${classInfo.className} ${classInfo.className?uncap_first}){
        return ${classInfo.className?uncap_first}Service.insert(${classInfo.className?uncap_first});
    }

    /**
    * 刪除
    * @author ${authorName}
    * @date ${.now?string('yyyy/MM/dd')}
    **/
    @RequestMapping("/delete")
    public Object delete(int id){
        return ${classInfo.className?uncap_first}Service.delete(id);
    }

    /**
    * 更新
    * @author ${authorName}
    * @date ${.now?string('yyyy/MM/dd')}
    **/
    @RequestMapping("/update")
    public Object update(${classInfo.className} ${classInfo.className?uncap_first}){
        return ${classInfo.className?uncap_first}Service.update(${classInfo.className?uncap_first});
    }

    /**
    * 查询 根据主键 id 查询
    * @author ${authorName}
    * @date ${.now?string('yyyy/MM/dd')}
    **/
    @RequestMapping("/find")
    public Object find(int id){
        return ${classInfo.className?uncap_first}Service.find(id);
    }

    /**
    * 查询 分页查询
    * @author ${authorName}
    * @date ${.now?string('yyyy/MM/dd')}
    **/
    @RequestMapping("/pageList")
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int offset,
                                        @RequestParam(required = false, defaultValue = "10") int pagesize) {
        return ${classInfo.className?uncap_first}Service.pageList(offset, pagesize);
    }

    /**
    * 查询 动态条件分页查询 - 根据对象属性自动构建条件
    * 如果字段有值则进行分页+指定条件查询，否则仅进行分页查询
    * @author ${authorName}
    * @date ${.now?string('yyyy/MM/dd')}
    **/
    @RequestMapping("/pageByCondition")
    public Map<String, Object> pageByCondition(${classInfo.className} queryParamDTO,
                                             @RequestParam(required = false, defaultValue = "0") int offset,
                                             @RequestParam(required = false, defaultValue = "10") int pagesize) {
        return ${classInfo.className?uncap_first}Service.pageByCondition(queryParamDTO, offset, pagesize);
    }

    /**
    * 激活/停用
    * @author ${authorName}
    * @date ${.now?string('yyyy/MM/dd')}
    **/
    @RequestMapping("/active")
    public Object active(int id, Integer status) {
        ${classInfo.className} ${classInfo.className?uncap_first} = ${classInfo.className?uncap_first}Service.find(id);
        if(${classInfo.className?uncap_first} != null) {
            ${classInfo.className?uncap_first}.setStatus(status);
            Object result = ${classInfo.className?uncap_first}Service.update(${classInfo.className?uncap_first});
            return result;
        } else {
            return ${returnUtilFailure}("未找到记录");
        }
    }

}
