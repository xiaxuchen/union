package com.originit.union.mapper;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.originit.union.entity.MessageEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageDaoTest {

    @Autowired
    MessageDao messageDao;

    @Test
    public void update() {
        messageDao.update(null,new UpdateWrapper<MessageEntity>().lambda().set(MessageEntity::getState,MessageEntity.STATE.READ).eq(MessageEntity::getId,10));
    }
}