package com.originit.union.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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

@Aspect
@Component
public class ServiceAdvice {

    @Autowired
    Validator validator;

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
}
