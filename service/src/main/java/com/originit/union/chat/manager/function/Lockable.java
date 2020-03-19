package com.originit.union.chat.manager.function;

public interface Lockable {

    /**
     * 将一段调用加锁
     * @param callable 外部调用类内部的多个加锁方法时，在外套上这个锁，使得整个操作都是原子的
     * @param <T>
     * @return
     */
    <T> T lock(Callable<T> callable);
}
