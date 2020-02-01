package com.originit.union.api.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author xxc、
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Interceptor {
}
