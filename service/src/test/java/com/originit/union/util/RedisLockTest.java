package com.originit.union.util;

import com.originit.common.util.RedisLock;
import com.originit.union.constant.ChatConstant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisLockTest {

    Logger logger = LoggerFactory.getLogger(RedisLockTest.class);

    @Autowired
    RedisLock lock;

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void testLock() throws InterruptedException {
        Thread thread1 = new Thread(() -> {
            logger.info("线程1请求加锁");
            String lock = this.lock.lock(ChatConstant.USER_LOCK);
            logger.info("线程1加锁");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info("线程1解锁开始");
            this.lock.unlock(ChatConstant.USER_LOCK, lock);
            logger.info("线程1解锁结束");
        });
        thread1.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Thread thread = new Thread(() -> {
            logger.info("线程2请求加锁");
            String lock = this.lock.lock(ChatConstant.USER_LOCK);
            logger.info("线程2加锁");
            if (lock != null) {
                System.out.println("开始咯");
                logger.info("线程2解锁开始");
                this.lock.unlock(ChatConstant.USER_LOCK, lock);
                logger.info("线程2解锁结束");
            }
        });
        thread.start();
        thread.join();
        thread1.join();
    }
}
