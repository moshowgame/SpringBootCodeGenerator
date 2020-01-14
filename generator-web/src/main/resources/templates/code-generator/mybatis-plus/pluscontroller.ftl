package ${packageName}.controller;

import ${packageName}.entity.${classInfo.className};
import ${packageName}.mapper.${classInfo.className}Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import java.util.Map;

/**
 * @description ${classInfo.classComment}
 * @author ${authorName}
 * @date ${.now?string('yyyy-MM-dd HH:mm:ss')}
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
        ${classInfo.className} ${classInfo.className?uncap_first} = ${classInfo.className?uncap_first}Mapper.selectOne(new QueryWrapper<${classInfo.className}>().eq("id",id))
        if(${classInfo.className?uncap_first}!=null){
            ${classInfo.className?uncap_first}Mapper.updateById(${classInfo.className?uncap_first});
        }else{
            ${classInfo.className?uncap_first}Mapper.insert(${classInfo.className?uncap_first});
        }
        return ${returnUtil}.success(${classInfo.className?uncap_first});
    }

    /**
    * 删除
    */
    @PostMapping("/delete")
    public Object delete(int id){
        ${classInfo.className} ${classInfo.className?uncap_first} = ${classInfo.className?uncap_first}Mapper.selectOne(new QueryWrapper<${classInfo.className}>().eq("id",id))
        if(${classInfo.className?uncap_first}!=null){
            return ${returnUtil}.success(${classInfo.className?uncap_first});
        }else{
            return ${returnUtil}.error("没有找到该对象");
        }
    }

    /**
    * 查询
    */
    @PostMapping("/find")
    public Object find(int id){
        ${classInfo.className} ${classInfo.className?uncap_first} = ${classInfo.className?uncap_first}Mapper.selectOne(new QueryWrapper<${classInfo.className}>().eq("id",id))
        if(${classInfo.className?uncap_first}!=null){
            return ${returnUtil}.success(${classInfo.className?uncap_first});
        }else{
            return ${returnUtil}.error("没有找到该对象");
        }
    }

    /**
    * 分页查询
    */
    @PostMapping("/list")
    public Object list(${classInfo.className} ${classInfo.className?uncap_first},
                        @RequestParam(required = false, defaultValue = "0") int pageNumber,
                        @RequestParam(required = false, defaultValue = "10") int pageSize) {
        //分页构造器
        Page<${classInfo.className}> page = new Page<${classInfo.className}>(pageNumber,pageSize);
        //条件构造器
        QueryWrapper<${classInfo.className}> queryWrapperw = new QueryWrapper<${classInfo.className}>(${classInfo.className?uncap_first});
        //执行分页
        IPage<${classInfo.className}> pageList = certPersonMapper.selectPage(page, queryWrapperw);
        //返回结果
        return ${returnUtil}.success(pageList);
    }

}
