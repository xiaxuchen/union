package com.originit.union.config;

import com.soecode.wxtools.api.IService;
import com.soecode.wxtools.api.WxService;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class WechatConfig {

    @Bean
    @Profile("dev")
    public IService iService () {
        String proxyHost = "127.0.0.1";
        String proxyPort = "1080";

        System.setProperty("http.proxyHost", proxyHost);
        System.setProperty("http.proxyPort", proxyPort);

        // 对https也开启代理
        System.setProperty("https.proxyHost", proxyHost);
        System.setProperty("https.proxyPort", proxyPort);
        return new WxService(){
            {
                this.httpClient = HttpClients.createSystem();
            }
        };
    }

    @Bean
    @Profile("prod")
    public IService iservice () {
        return new WxService();
    }
}
