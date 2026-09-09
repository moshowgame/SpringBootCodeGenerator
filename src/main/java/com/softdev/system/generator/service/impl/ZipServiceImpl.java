package com.softdev.system.generator.service.impl;

import com.softdev.system.generator.service.ZipService;
import com.softdev.system.generator.util.HeritageUtil;
import com.softdev.system.generator.util.ZipFileNameResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * ZIP 打包服务实现类
 * <p>
 * 使用 JDK 自带 ZipOutputStream 打包，零依赖。
 * <p>
 * 目录结构：{group}/{fileName}，同名文件自动加序号去重。
 *
 * @author zhengkai.blog.csdn.net
 */
@Slf4j
@Service
public class ZipServiceImpl implements ZipService {

    @Override
    public byte[] buildZip(Map<String, String> generatedCode,
                           Map<String, String> fileNameTemplates,
                           Map<String, String> groupByTemplate,
                           String zipFileName,
                           Map<String, Object> context) {
        if (generatedCode == null || generatedCode.isEmpty()) {
            throw new IllegalArgumentException("没有可打包的生成内容");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream(64 * 1024);
        Set<String> usedPaths = new HashSet<>();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            zos.setLevel(java.util.zip.Deflater.BEST_SPEED);
            // 🏺 非遗传承：手工代码认证书作为 ZIP 首个条目
            if (HeritageUtil.isEnabled(context)) {
                List<String> templateNames = generatedCode.keySet().stream()
                        .filter(name -> !"tableName".equals(name))
                        .collect(Collectors.toList());
                String certificate = HeritageUtil.buildCertificate(
                        stringOf(context, "className"), stringOf(context, "tableName"),
                        stringOf(context, "author"), templateNames);
                writeEntry(zos, "手工代码认证书.txt", certificate);
            }
            for (Map.Entry<String, String> entry : generatedCode.entrySet()) {
                String templateName = entry.getKey();
                String content = entry.getValue();
                // tableName 是返回给前端的结构化字段，不是代码文件
                if (content == null || "tableName".equals(templateName)) {
                    continue;
                }
                String fileNameTpl = fileNameTemplates == null ? null : fileNameTemplates.get(templateName);
                String group = groupByTemplate == null ? "generated" : groupByTemplate.getOrDefault(templateName, "generated");
                String fileName = ZipFileNameResolver.resolve(fileNameTpl, templateName, group, context);
                String entryPath = uniquePath(group, fileName, usedPaths);
                writeEntry(zos, entryPath, content);
            }
        } catch (IOException e) {
            log.error("ZIP 打包失败: zipFileName={}", zipFileName, e);
            throw new RuntimeException("ZIP 打包失败: " + e.getMessage(), e);
        }
        return baos.toByteArray();
    }

    private void writeEntry(ZipOutputStream zos, String entryPath, String content) throws IOException {
        ZipEntry zipEntry = new ZipEntry(entryPath);
        zipEntry.setSize(content.getBytes(StandardCharsets.UTF_8).length);
        zos.putNextEntry(zipEntry);
        zos.write(content.getBytes(StandardCharsets.UTF_8));
        zos.closeEntry();
    }

    private String stringOf(Map<String, Object> context, String key) {
        if (context == null || key == null) {
            return "";
        }
        Object value = context.get(key);
        return value == null ? "" : value.toString();
    }

    /**
     * 构造 entry 路径；遇到重名时加 _1 / _2 ...
     */
    private String uniquePath(String group, String fileName, Set<String> used) {
        String base = sanitizeGroup(group) + "/" + fileName;
        if (used.add(base)) {
            return base;
        }
        int dot = fileName.lastIndexOf('.');
        String prefix = dot < 0 ? fileName : fileName.substring(0, dot);
        String suffix = dot < 0 ? "" : fileName.substring(dot);
        for (int i = 1; i < 1000; i++) {
            String candidate = sanitizeGroup(group) + "/" + prefix + "_" + i + suffix;
            if (used.add(candidate)) {
                return candidate;
            }
        }
        return sanitizeGroup(group) + "/" + System.nanoTime() + "_" + fileName;
    }

    private String sanitizeGroup(String group) {
        if (group == null || group.trim().isEmpty()) {
            return "generated";
        }
        return group.trim().replaceAll("[\\\\/:*?\"<>|\\r\\n\\t]", "_");
    }
}
