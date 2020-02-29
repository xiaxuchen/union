package com.originit.union.api.processor;

import com.originit.union.api.controller.CoreController;
import com.originit.union.api.wxinterceptor.WXInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * 将spring容器中的WxInterceptor加入到CoreController中
 * @author xxc、
 */
@Component
class WxInterceptorPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof WXInterceptor)
        {
            CoreController.addInterceptor((WXInterceptor) bean);
        }
        return bean;
    }
}
