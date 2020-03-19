package com.originit.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xxc、
 */
@Component
@Slf4j
public class RedisLock {


    private static class TimeSign {
        // 开始时间
        public Long start;
        // 超时时间
        public Long timeout;

        public TimeSign() { }

        public TimeSign(Long start, Long timeout) {
            this.start = start;
            this.timeout = timeout;
        }
    }

    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 保存当前线程获取的锁的信息
     */
    private ThreadLocal<Map<String,TimeSign>> threadLockMap = new ThreadLocal<>();

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public static final String REDIS_LOCK = "RedisLock:";


    /**
     * 5s 锁的超时时间
     */
    private static final long DEFAULT_WAIT_LOCK_TIME_OUT = 5;
    /**
     * 10s锁的有效时间
     */
    private static final long DEFAULT_EXPIRE = 10;

    /**
     *  lua脚本，用来释放分布式锁
      */
    private static String LUA_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";

    /**
     * 已获得锁的标记
     */
    private static String ALREADY_GOT_LOCK = "already_got_lock";

    private Map<String,TimeSign> getLockMap() {
        Map<String, TimeSign> map = null;
        map = threadLockMap.get();
        if (map == null) {
            map = new HashMap<>();
            threadLockMap.set(map);
        }
        return map;
    }

    /**
     * 检查sign的有效性
     * @param key 加锁的键
     */
    private boolean check (String key) {
        Map<String, TimeSign> map = getLockMap();
        TimeSign timeSign = map.get(key);
        // 当前没有锁或者锁过期了，返回没有
        if (timeSign == null || System.nanoTime() - timeSign.start >= timeSign.timeout) {
            map.remove(key);
            return false;
        }
        return true;
    }

    /**
     * 获取锁
     * @param key 锁的键
     * @return : 锁的值，用来判断是否是加锁的那把锁
     */
    public String lock(String key) {
        log.info("reentrant lock with key:{} lock again" + key);
        return lock(key,DEFAULT_WAIT_LOCK_TIME_OUT,TimeUnit.SECONDS);
    }

    /**
     * 释放锁
     * @param key 锁的键
     * @param lockValue 锁的当前值
     */
    public void unlock(String key,String lockValue) {
        // 加入可重入机制，在最外面的锁才能解锁
        if (lockValue.equals(ALREADY_GOT_LOCK)) {
            log.info("reentrant lock with key:{} release" + key);
            return;
        }
        try {
            String lockKey = generateLockKey(key);
            DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
            //放在和application.yml 同层目录下
            redisScript.setScriptText(LUA_SCRIPT);
            redisScript.setResultType(String.class);
            log.info("release lock:{key:{},uuid:{}}", key, lockValue);
            RedisConnection connection= redisTemplate.getConnectionFactory().getConnection();
            connection.eval(
                    LUA_SCRIPT.getBytes(),
                    ReturnType.INTEGER,
                    1,
                    lockKey.getBytes(),
                    lockValue.getBytes());
            getLockMap().remove(key);
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
        }
    }

    /**
     * 组装锁的key
     * @param key 锁的键
     */
    private String generateLockKey(String key) {
        return String.format(REDIS_LOCK + "%s", key);
    }

    /**
     * 获取锁
     * @param key 键
     * @param timeout 加锁超时时间
     * @param seconds 时间单位
     * @return : 加锁后的键 是否加锁成功
     * @date 2018/7/2 15:42
     */
    public String lock(String key, long timeout, TimeUnit seconds) {
        if (check(key)) {
            // 如果该线程已获得锁，返回特殊值
            return ALREADY_GOT_LOCK;
        }
        String lockKey = generateLockKey(key);
        long nanoWaitForLock = seconds.toNanos(timeout);
        long start = System.nanoTime();
        String lockValue = generateLockValue ();
        RedisConnection connection = null;
        try {
            connection = Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection();
            // 请求锁的超时时间
            while ((System.nanoTime() - start) < nanoWaitForLock) {
                log.info("request:{key:{}}", key);
                if (connection.set(lockKey.getBytes(), lockValue.getBytes(), Expiration.from(DEFAULT_EXPIRE,TimeUnit.SECONDS), RedisStringCommands.SetOption.SET_IF_ABSENT)) {
                    log.info("locked:{key:{},uuid:{}}", key, lockValue);
                    getLockMap().put(key,new TimeSign(System.nanoTime(),TimeUnit.SECONDS.toNanos(DEFAULT_EXPIRE)));
                    return lockValue;
                }
                //TODO 这里缩短了抢锁的时间 加随机时间防止活锁
                TimeUnit.MILLISECONDS.sleep(new Random().nextInt(100) + 5);
            }
        } catch (Exception e) {
            log.error("{}", e.getMessage(), e);
            // 如果过程中出现了问题就解锁
            unlock(key,lockValue);
        } finally {
            //一定要关闭连接，不然会导致连接一直不释放
            if (connection != null) {
                connection.close();
            }
        }
        return null;
    }

    /**
     * 生成加锁的值，以防误解了别人的锁
     * @return 加锁的值
     */
    private String generateLockValue() {
        return UUID.randomUUID().toString();
    }
}
