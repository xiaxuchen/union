package com.originit.union.advice;

import com.originit.union.annotation.LockKey;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;

/**
 * Service的切面
 * @author xxc、
 */
@Aspect
@Component
@Slf4j
public class ServiceAdvice {

    private Validator validator;

    @Autowired
    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    /**
     * 给所有的Service添加参数验证
     */
    @Around("execution(* com.originit.union.service.*.*(..))")
    public Object validateService (ProceedingJoinPoint point) throws Throwable {
        // 1 获取方法的参数签名
        final Object[] args = point.getArgs();
        final Method method = ((MethodSignature) point.getSignature()).getMethod();
        final Parameter[] typeParameters = method.getParameters();
        // 2 获取参数上的注解是否有Valid、Validated，若有则生成对应的分组Class
        for (int i = 0; i < typeParameters.length; i++) {
            // 分组
            Class[] groups = null;
            final Parameter typeParameter = typeParameters[i];
            final Valid valid = typeParameter.getAnnotation(Valid.class);
            final Validated validated = typeParameter.getAnnotation(Validated.class);
            if (valid != null)
            {
                groups = new Class[0];
            } else if (validated != null) {
                groups =  validated.value();
            }
            // 3 当分组Class为null，则表示不验证，否则使用Hibernate Validator进行验证，若错误则抛出分发的状态异常
            if (groups != null) {
                final Set<ConstraintViolation<Object>> errors = validator.validate(args[i], groups);
                if (!errors.isEmpty()) {
                    throw new IllegalArgumentException(errors.iterator().next().getMessage());
                }
            }
        }
        return point.proceed();
    }

//    private ChatLock lock;

//    @Autowired
//    public void setLock(ChatLock lock) {
//        this.lock = lock;
//    }
//
//    @Around("@annotation(com.originit.union.annotation.LockKey)")
//    public Object redisAutoLock (ProceedingJoinPoint point) throws Throwable {
//        final Method method = ((MethodSignature) point.getSignature()).getMethod();
//        LockKey lockKey = method.getAnnotation(LockKey.class);
//        if (lockKey == null) {
//            lockKey = point.getTarget().getClass().getAnnotation(LockKey.class);
//        }
//        // 如果方法或类上都没有注解或有注解但为不加锁，则直接执行
//        if (lockKey == null || lockKey.noLock()) {
//            return point.proceed();
//        }
//        log.info("auto lock");
//        // 包装上加锁逻辑
//        return lock.commonLock(lockKey.value(), point::proceed);
//    }
}
