package com.originit.union.annotation;

import java.lang.annotation.*;

/**
 * Redis中加锁的键
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LockKey {
    String value() default "";
    boolean noLock() default false;
}
