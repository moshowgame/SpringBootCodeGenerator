package com.softdev.system.generator.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Data
public class DirectoryUtil {
    /**
     * 带目录层次创建文件
     * @param filename
     * @param conetent
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> boolean mkfile(String filename, T conetent) throws IOException {
        if (StringUtils.isEmpty(filename) || conetent == null){
            log.warn("empty filename or content");
            return false;
        }
        Path path = Paths.get(filename);
        Files.createDirectories(path.getParent());
        Files.write(path, conetent.toString().getBytes());
        return true;
    }
}
