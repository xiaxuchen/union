package com.originit.union.redis;

import com.originit.common.util.RedisCacheProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SystemRepository {

    private RedisCacheProvider provider;

    @Autowired
    public void setProvider(RedisCacheProvider provider) {
        this.provider = provider;
    }


}
