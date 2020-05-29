package com.originit;

import com.originit.common.util.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.servlet.MultipartConfigElement;
import java.io.File;

/**
 * @author xxc、
 */

@EnableAsync
@EnableScheduling
@SpringBootApplication(exclude = MultipartAutoConfiguration.class)
@Slf4j
public class UnionApplication {
    @Autowired
    RedisLock lock;
    public static void main(String[] args) {
        SpringApplication.run(UnionApplication.class, args);
    }

    /**
     * 设置文件上传下载临时目录
     * @return
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        String location = System.getProperty("user.dir") + "/data/tmp";
        File file = new File(location);
        if(!file.exists()){
            file.mkdirs();
        }
        log.info("【初始化上传下载】临时文件路径:{},是否存在:{}", location, file.exists());
        factory.setLocation(location);
        return factory.createMultipartConfig();
    }
}
