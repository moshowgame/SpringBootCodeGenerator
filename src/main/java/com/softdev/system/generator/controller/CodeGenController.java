package com.softdev.system.generator.controller;

import com.softdev.system.generator.entity.dto.ClassInfo;
import com.softdev.system.generator.entity.dto.CodeGenResult;
import com.softdev.system.generator.entity.dto.ParamInfo;
import com.softdev.system.generator.entity.vo.ResultVo;
import com.softdev.system.generator.service.CodeGenService;
import com.softdev.system.generator.service.ZipService;
import com.softdev.system.generator.util.HeritageUtil;
import com.softdev.system.generator.util.MapUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 代码生成控制器
 *
 * @author zhengkai.blog.csdn.net
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/code")
public class CodeGenController {

    private final CodeGenService codeGenService;
    private final ZipService zipService;

    @PostMapping("/generate")
    public ResultVo generateCode(@RequestBody ParamInfo paramInfo) throws Exception {
        return codeGenService.generateCode(paramInfo);
    }

    /**
     * 一键 ZIP 打包下载：将生成的全部代码打成 ZIP 流返回
     *
     * @param paramInfo 参数信息
     * @return ZIP 字节流
     */
    @PostMapping(value = "/generate-zip", produces = "application/zip")
    public ResponseEntity<byte[]> generateCodeAsZip(@RequestBody ParamInfo paramInfo) throws Exception {
        log.info("收到 ZIP 下载请求");
        if (paramInfo == null || paramInfo.getTableSql() == null || paramInfo.getTableSql().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        CodeGenResult result = codeGenService.getResultByParams(prepareOptions(paramInfo));

        String zipName = (result.getClassName() != null && !result.getClassName().isEmpty())
                ? result.getClassName()
                : (result.getTableName() != null && !result.getTableName().isEmpty())
                ? result.getTableName()
                : "code-generator";

        byte[] zipBytes = zipService.buildZip(
                result.getGeneratedCode(),
                result.getFileNameTemplates(),
                result.getGroupByTemplate(),
                zipName,
                buildContext(result, paramInfo.getOptions())
        );

        String encoded = URLEncoder.encode(zipName + ".zip", StandardCharsets.UTF_8)
                .replace("+", "%20");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/zip"));
        // 注意：只设置一次 Content-Disposition，同时携带 ASCII 与 RFC 5987 UTF-8 形式，
        // 避免 ERR_RESPONSE_HEADERS_MULTIPLE_CONTENT_DISPOSITION 错误
        headers.set("Content-Disposition",
                "attachment; filename=\"" + zipName + ".zip\"; filename*=UTF-8''" + encoded);
        headers.setContentLength(zipBytes.length);
        return new ResponseEntity<>(zipBytes, headers, org.springframework.http.HttpStatus.OK);
    }

    /**
     * 与 generateCode 共享参数准备逻辑：解析、塞入 options
     */
    private Map<String, Object> prepareOptions(ParamInfo paramInfo) throws Exception {
        Map<String, Object> options = paramInfo.getOptions() == null ? new HashMap<>() : paramInfo.getOptions();
        paramInfo.setOptions(options);

        ClassInfo classInfo = codeGenService.parseTableStructure(paramInfo);
        options.put("classInfo", classInfo);
        options.put("tableName", classInfo == null ? System.currentTimeMillis() + "" : classInfo.getTableName());
        return options;
    }

    /**
     * 构造 ZIP 解析占位符所需的上下文（含非遗传承印章开关与作者）
     */
    private Map<String, Object> buildContext(CodeGenResult result, Map<String, Object> options) {
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("className", result.getClassName() == null ? "" : result.getClassName());
        ctx.put("tableName", result.getTableName() == null ? "" : result.getTableName());
        ctx.put("isHeritage", HeritageUtil.isEnabled(options));
        ctx.put("author", MapUtil.getString(options, "authorName"));
        return ctx;
    }
}
