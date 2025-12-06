<#if isWithPackage?exists && isWithPackage==true>package ${packageName}.service;</#if>
<#if isAutoImport?exists && isAutoImport==true>
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ${packageName}.common.utils.PageUtils;
import ${packageName}.common.utils.Query;

import ${packageName}.dao.${classInfo.className}Dao;
import ${packageName}.entity.${classInfo.className}Entity;
</#if>

@Service("${classInfo.className?uncap_first}Service")
public class ${classInfo.className}Service extends ServiceImpl<${classInfo.className}Dao, ${classInfo.className}Entity> {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<${classInfo.className}Entity> page = this.page(
            new Query<${classInfo.className}Entity>().getPage(params),
                new QueryWrapper<${classInfo.className}Entity>()
            );

        return new PageUtils(page);
    }

}