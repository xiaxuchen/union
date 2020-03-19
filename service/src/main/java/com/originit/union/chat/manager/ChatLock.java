package com.originit.union.chat.manager;

import com.originit.common.exceptions.LockFailException;
import com.originit.common.util.RedisLock;
import com.originit.union.chat.data.ChatUser;
import com.originit.union.chat.manager.function.Callable;
import com.originit.union.constant.ChatConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 聊天的锁
 * @author xxc、
 */
@Component
@Slf4j
public class ChatLock {

    private RedisLock lock;

    @Autowired
    public void setLock(RedisLock lock) {
        this.lock = lock;
    }

    public <T> T commonLock (String key,Callable<T> callable) {
        String value = this.lock.lock(key);
        if (value == null) {
            throw new LockFailException();
        }
        try {
            return callable.call();
        } catch (Throwable e) {
            e.printStackTrace();
            log.error("lock execution error:{}",e);
            throw new LockFailException();
        } finally {
            this.lock.unlock(key,value);
        }
    }

    /**
     * 给用户相关操作加锁
     * @param callable 加锁中的回调
     * @param <T> 加锁的回调的返回值
     * @return 返回你回调的返回值
     */
    public <T> T lockUser (Callable<T> callable) {
        return commonLock(ChatConstant.USER_LOCK, callable);
    }

//    public <T> T lockSession (Callable<T> callable) {
//        return commonLock(ChatConstant, callable);
//    }

}
