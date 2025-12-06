package com.softdev.system.generator.service.parser;

import com.softdev.system.generator.entity.dto.ClassInfo;
import com.softdev.system.generator.entity.dto.ParamInfo;

/**
 * JSON解析服务接口
 *
 * @author zhengkai.blog.csdn.net
 */
public interface JsonParserService {

    /**
     * 解析JSON生成类信息
     *
     * @param paramInfo 参数信息
     * @return 类信息
     */
    ClassInfo processJsonToClassInfo(ParamInfo paramInfo);
}