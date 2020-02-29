package com.originit;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author xxc、
 */
@MapperScan("com.originit.union.mapper")
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
@SpringBootApplication
public class UnionApplication {
    public static void main(String[] args) {
        SpringApplication.run(UnionApplication.class, args);
    }

}
