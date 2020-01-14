package com.softdev.system.generator.config;

import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
/**
*  2019-2-11 liutf WebMvcConfig 整合 cors 和 SpringMvc MessageConverter
*/
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

/*    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedHeaders("x-requested-with")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS", "TRACE")
                .maxAge(3600);
    }*/

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //FastJsonHttpMessageConverter
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();

        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        fastConverter.setSupportedMediaTypes(fastMediaTypes);

        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setCharset(StandardCharsets.UTF_8);
        fastConverter.setFastJsonConfig(fastJsonConfig);

        //StringHttpMessageConverter
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
        stringConverter.setDefaultCharset(StandardCharsets.UTF_8);
        stringConverter.setSupportedMediaTypes(fastMediaTypes);
        converters.add(stringConverter);
        converters.add(fastConverter);
    }
}