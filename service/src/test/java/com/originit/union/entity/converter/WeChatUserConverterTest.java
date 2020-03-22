package com.originit.union.entity.converter;

import com.originit.union.entity.TagEntity;
import com.originit.union.entity.domain.UserInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class WeChatUserConverterTest {

    @Test
    public void name() {
        final ArrayList<TagEntity> tagEntities = new ArrayList<>();
        final TagEntity tagEntity = new TagEntity();
        tagEntity.setName("vip");
        tagEntity.setId(10L);
        tagEntities.add(tagEntity);
        final UserInfo build = UserInfo.builder()
                .openId("sadsdfasdfwe")
                .name("xxc")
                .subscribeTime(LocalDateTime.now())
                .phone("17779911413")
                .sex(null)
                .tags(tagEntities)
                .headImg("jasjdjas")
                .pushCount(100)
                .build();
        System.out.println(WeChatUserConverter.INSTANCE.to(build));
    }
}