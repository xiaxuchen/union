package com.originit.union.chat.manager.function;

/**
 * 可调用的且有返回值的接口
 * @param <T>
 */
public interface Callable<T> {
    T call() throws Throwable;
}
