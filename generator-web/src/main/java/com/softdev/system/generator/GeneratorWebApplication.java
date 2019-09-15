package com.softdev.system.generator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class GeneratorWebApplication {
	public static void main(String[] args) {
		SpringApplication.run(GeneratorWebApplication.class,args);
		log.info("项目启动启动成功！访问地址: http://localhost:1234/generator");
	}
}
