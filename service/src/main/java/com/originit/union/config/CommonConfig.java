package com.originit.union.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 配置线程池等公共的bean
 * @author xxc、
 */
@Configuration
@Slf4j
public class CommonConfig {

    @Bean
    public ThreadPoolExecutor asyncServiceExecutor() {
        log.info("start asyncServiceExecutor");
        int poolSize = Runtime.getRuntime().availableProcessors() * 2;
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(512);
        return new ThreadPoolExecutor(poolSize,
                poolSize,
                3 * 60,
                TimeUnit.SECONDS,
                queue,
                new ThreadFactory() {

                    private AtomicInteger nextId = new AtomicInteger(1);
                    @Override
                    public Thread newThread(Runnable r) {
                        String threadName = "asyncTask" + nextId.incrementAndGet();
                        Thread thread = new Thread(null,r,threadName,0);
                        return thread;
                    }
                },new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
