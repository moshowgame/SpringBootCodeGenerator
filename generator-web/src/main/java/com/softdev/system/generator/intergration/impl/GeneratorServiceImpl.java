package com.softdev.system.generator.intergration.impl;

import com.softdev.system.generator.common.Constants;
import com.softdev.system.generator.helper.DirectoryGenerator;
import com.softdev.system.generator.intergration.GeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class GeneratorServiceImpl implements GeneratorService {
    @Override
    public void generateFile(Map<String, String> result) {
        DirectoryGenerator directoryGenerator = new DirectoryGenerator();

        String packageName =  result.get(Constants.PACKAGE_NAME);
        String className = result.get(Constants.CLASS_NAME);

        CompletableFuture.runAsync(() -> {
            directoryGenerator.generateFile(packageName + "." + Constants.MAPPER_NAME,
                    className + "_" + Constants.MAPPER_NAME + Constants.XML_POSTFIX,
                    result.get(Constants.MAPPER_NAME));
            directoryGenerator.generateFile(packageName + "." + Constants.CONTROLLER_NAME,
                    className + "_" + Constants.CONTROLLER_NAME + Constants.JAVA_POSTFIX,
                    result.get(Constants.CONTROLLER_NAME));
            directoryGenerator.generateFile(packageName + "." + Constants.DAO_NAME,
                    className + "_" + Constants.DAO_NAME + Constants.JAVA_POSTFIX,
                    result.get(Constants.DAO_NAME));
            directoryGenerator.generateFile(packageName + "." + Constants.SERVICE_NAME,
                    className + "_" + Constants.SERVICE_NAME + Constants.JAVA_POSTFIX,
                    result.get(Constants.SERVICE_NAME));
            directoryGenerator.generateFile(packageName + "." + Constants.SERVICE_IMPL_NAME,
                    className + "_" + Constants.SERVICE_NAME + "_" + Constants.SERVICE_IMPL_NAME + Constants.JAVA_POSTFIX,
                    result.get(Constants.SERVICE_IMPL_NAME));
            directoryGenerator.generateFile(packageName + "." + Constants.MODEL_NAME,
                    className + "_" + Constants.MODEL_NAME + Constants.JAVA_POSTFIX,
                    result.get(Constants.MODEL_NAME));
        }).exceptionally(e -> {
            log.error("error during file generating {}: {}", e.getMessage(), e);
            return null;
        });
    }
}
