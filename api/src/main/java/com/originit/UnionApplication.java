package com.originit;

import com.originit.common.util.RedisLock;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author xxc、
 */

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class UnionApplication {
    @Autowired
    RedisLock lock;
    public static void main(String[] args) {
        SpringApplication.run(UnionApplication.class, args);
    }

}
