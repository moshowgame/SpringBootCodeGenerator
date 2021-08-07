<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="${packageName}.dao.${classInfo.className}Dao">

    <!-- 可根据自己的需求，是否要使用 -->
    <resultMap type="${packageName}.entity.${classInfo.className}Entity" id="${classInfo.className}Map">
        <#if classInfo.fieldList?exists && classInfo.fieldList?size gt 0>
            <#list classInfo.fieldList as fieldItem >
                <result property="${fieldItem.fieldName}" column="${fieldItem.fieldName}"/>
            </#list>
        </#if>
    </resultMap>

</mapper>