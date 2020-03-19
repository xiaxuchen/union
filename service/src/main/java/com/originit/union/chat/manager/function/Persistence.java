package com.originit.union.chat.manager.function;

/**
 * 持久化接口，用于持久化
 * @param <T>
 */
public interface Persistence<T> {
    /**
     * 执行持久化
     * @return 返回持久化后的对象，包括id
     */
    T execute ();
}