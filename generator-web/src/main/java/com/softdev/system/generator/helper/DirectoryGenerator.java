package com.softdev.system.generator.helper;

import com.google.common.base.CaseFormat;
import com.softdev.system.generator.util.DirectoryUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;

@Slf4j
public class DirectoryGenerator {
    /**
     * 按包名、类名创建目录层次及文件，写入内容
     * @param packageName
     * @param filename
     * @param content
     */
    public void generateFile(String packageName, String filename, String content){
        filename = toDir(packageName) + File.separator + CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, filename);
        try {
            if (!DirectoryUtil.mkfile(filename, content)){
                log.error("generate file {}, content {} failed", filename, content);
            }
        } catch (IOException e) {
            log.error("generate file error: {}", e);
        }
    }

    private String toDir(String packageName){
        return packageName.replaceAll("\\.", "\\" + File.separator);
    }

    public static void main(String[] args) {
        log.info(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, "userinfomapper"));
    }
}
