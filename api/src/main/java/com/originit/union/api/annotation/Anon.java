package com.originit.union.api.annotation;


import java.lang.annotation.*;

/**
 * 无需登录即可访问
 * @author xxc、
 */
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Anon {
}
