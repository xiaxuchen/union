package com.originit.union.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PushInfoDaoTest {

    @Autowired
    PushInfoDao pushInfoDao;

    @Test
    public void selectAllPushCount() {
        System.out.println(pushInfoDao.selectAllPushCount(null, null));
    }
}