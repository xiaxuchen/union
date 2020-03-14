package com.originit.union.service;


public interface RedisService {
    /**
     * 存储数据
     */
    void set(String key, Object value);

    /**
     * 获取数据
     */
    <T> T get(String key,Class<T> type);

    /**
     * 设置超期时间
     */
    boolean expire(String key, long expire);

    /**
     * 删除数据
     */
    void remove(String key);

    /**
     * 自增操作
     * @param delta 自增步长
     */
    Long increment(String key, long delta);

}

