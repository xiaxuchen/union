package com.originit.union.api.config;

import com.soecode.wxtools.api.IService;
import com.soecode.wxtools.api.WxService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WxServiceConfig {

    @Bean
    public IService iService () {
        return new WxService();
    }
}
