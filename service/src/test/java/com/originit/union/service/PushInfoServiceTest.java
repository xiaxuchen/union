package com.originit.union.service;

import com.originit.union.entity.PushInfoEntity;
import com.originit.union.entity.dto.PushInfoDto;
import com.originit.union.entity.mapper.PushInfoMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PushInfoServiceTest {

    @Autowired
    PushInfoService pushInfoService;


    @Test
    public void testMapper() {
        final PushInfoDto dto = new PushInfoDto(11, "asdfa", 100L, 1223L);
        final PushInfoEntity entity = PushInfoMapper.INSTANCE.dto2Entity(dto);
        assertEquals(entity.getContent(),dto.getContent());
        assertEquals(entity.getPushId(),dto.getPushId());
        assertEquals(entity.getPusher(),dto.getPusher());
        assertEquals((Object) entity.getType(),(Object) dto.getType());
    }

    @Test
    public void addPushInfo() {
        pushInfoService.addPushInfo(Arrays.asList("o1U3TjoJvUlZAs5WvlSVczPoNswg","o1U3TjhkALhl4dKenlK0sGax3Xhg"), new PushInfoDto(3,"我的星好痛",10000L,1L));
    }
}