package com.softdev.system.generator.service;

import com.softdev.system.generator.entity.dto.ClassInfo;
import com.softdev.system.generator.entity.dto.ParamInfo;
import com.softdev.system.generator.entity.vo.ResultVo;

import java.util.Map;

/**
 * 代码生成服务接口
 *
 * @author zhengkai.blog.csdn.net
 */
public interface CodeGenService {

    /**
     * 生成代码
     *
     * @param paramInfo 参数信息
     * @return 生成的代码映射
     * @throws Exception 生成过程中的异常
     */
    ResultVo generateCode(ParamInfo paramInfo) throws Exception;

    /**
     * 根据参数获取结果
     *
     * @param params 参数映射
     * @return 结果映射
     * @throws Exception 处理过程中的异常
     */
    Map<String, String> getResultByParams(Map<String, Object> params) throws Exception;
}