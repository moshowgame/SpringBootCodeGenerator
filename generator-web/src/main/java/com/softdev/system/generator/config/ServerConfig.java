package com.softdev.system.generator.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @Description 动态获取tomcat启动端口，控制台打印项目访问地址
 * @Author Gao Hang Hang
 * @Date 2019-12-27 14:37
 **/
@Component
@Slf4j
public class ServerConfig implements ApplicationListener<WebServerInitializedEvent> {

    private int serverPort;

    public int getPort() {
        return this.serverPort;
    }

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        this.serverPort = event.getWebServer().getPort();
        //log.info("Get WebServer port {}", serverPort);
        log.info("项目启动启动成功！访问地址: http://localhost:{}/generator", serverPort);
    }

}
