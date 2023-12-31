package com.originit.union.api.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.originit.union.api.interceptor.CorsInterceptor;
import com.originit.union.api.interceptor.ShiroAnnotationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.servlet.MultipartConfigElement;
import java.util.HashSet;
import java.util.List;

@Configuration
public class MvcConfig implements WebMvcConfigurer {


    private CorsInterceptor corsInterceptor;

    @Autowired
    public void setCorsInterceptor(CorsInterceptor corsInterceptor) {
        this.corsInterceptor = corsInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(corsInterceptor).addPathPatterns("/**");
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

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/union/**").addResourceLocations("classpath:/static/");
    }

    @Bean
    public ConfigurableServletWebServerFactory webServerFactory () {
        final TomcatServletWebServerFactory tomcatServletWebServerFactory = new TomcatServletWebServerFactory();
        HashSet<ErrorPage> set = new HashSet<>();
        ErrorPage error401Page=new ErrorPage(HttpStatus.UNAUTHORIZED,"/index.html");
        set.add(error401Page);
        set.add(new ErrorPage(HttpStatus.NOT_FOUND,"/index.html"));
        tomcatServletWebServerFactory.setErrorPages(set);
        return tomcatServletWebServerFactory;
    }

}
