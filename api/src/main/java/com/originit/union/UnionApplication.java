package com.originit.union;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.originit.union.mapper")
@SpringBootApplication
public class UnionApplication {
    public static void main(String[] args) {
        SpringApplication.run(UnionApplication.class, args);
    }

}
