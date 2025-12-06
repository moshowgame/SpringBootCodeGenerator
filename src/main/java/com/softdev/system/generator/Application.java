package com.softdev.system.generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zhengkai.blog.csdn.net
 */
@SpringBootApplication(scanBasePackages = "com.softdev.system.generator")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
