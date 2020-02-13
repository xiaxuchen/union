package com.originit.union.api.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashSet;
import java.util.List;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    /**
     * 添加跨域
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedHeaders("*")
                .allowedMethods("*")
                .allowedOrigins("*");
    }

    /**
     * 配置fastJson策略
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        FastJsonConfig config = new FastJsonConfig();
        config.setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.WriteMapNullValue);
        converter.setFastJsonConfig(config);
        converters.add(converter);
    }

    @Bean
    public ConfigurableServletWebServerFactory webServerFactory () {
        final TomcatServletWebServerFactory tomcatServletWebServerFactory = new TomcatServletWebServerFactory();
        HashSet<ErrorPage> set = new HashSet<>();
        set.add(new ErrorPage(HttpStatus.NOT_FOUND,"/index.html"));
        tomcatServletWebServerFactory.setErrorPages(set);
        return tomcatServletWebServerFactory;
    }
}
