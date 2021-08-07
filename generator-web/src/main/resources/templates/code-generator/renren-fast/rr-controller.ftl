<#if isWithPackage?exists && isWithPackage==true>package ${packageName}.controller;</#if>
<#if isAutoImport?exists && isAutoImport==true>
import java.util.Arrays;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ${packageName}.entity.${classInfo.className}Entity;
import ${packageName}.service.${classInfo.className}Service;
import ${packageName}.common.utils.PageUtils;
import ${packageName}.common.utils.R;
</#if>


/**
* @description ${classInfo.classComment}控制器
* @author ${authorName}
* @date ${.now?string('yyyy-MM-dd')}
*/
@RestController
@RequestMapping("generator/${classInfo.className?uncap_first}")
public class ${classInfo.className}Controller {

@Autowired
private ${classInfo.className}Service ${classInfo.className?uncap_first}Service;

/**
* 列表
*/
@RequestMapping("/list")
@RequiresPermissions("generator:${classInfo.className?uncap_first}:list")
public R list(@RequestParam Map<String, Object> params){
    PageUtils page = ${classInfo.className?uncap_first}Service.queryPage(params);

    return R.ok().put("page", page);
}


/**
* 信息
*/
@RequestMapping("/info/{${classInfo.className?uncap_first}Id}")
@RequiresPermissions("generator:${classInfo.className?uncap_first}:info")
public R info(@PathVariable("${classInfo.className?uncap_first}Id") int ${classInfo.className?uncap_first}Id){
    ${classInfo.className}Entity ${classInfo.className?uncap_first} = ${classInfo.className?uncap_first}Service.getById(${classInfo.className?uncap_first}Id);

    return R.ok().put("${classInfo.className?uncap_first}", ${classInfo.className?uncap_first});
}

/**
* 保存
*/
@RequestMapping("/save")
@RequiresPermissions("generator:${classInfo.className?uncap_first}:save")
public R save(@RequestBody ${classInfo.className}Entity ${classInfo.className?uncap_first}){
    ${classInfo.className?uncap_first}Service.save(${classInfo.className?uncap_first});

    return R.ok();
}

/**
* 修改
*/
@RequestMapping("/update")
@RequiresPermissions("generator:${classInfo.className?uncap_first}:update")
public R update(@RequestBody ${classInfo.className}Entity ${classInfo.className?uncap_first}){
    ${classInfo.className?uncap_first}Service.updateById(${classInfo.className?uncap_first});

    return R.ok();
}

/**
* 删除
*/
@RequestMapping("/delete")
@RequiresPermissions("generator:${classInfo.className?uncap_first}:delete")
public R delete(@RequestBody int[] ${classInfo.className?uncap_first}Ids){
    ${classInfo.className?uncap_first}Service.removeByIds(Arrays.asList(${classInfo.className?uncap_first}Ids));

    return R.ok();
}

}
