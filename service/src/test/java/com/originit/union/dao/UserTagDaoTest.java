package com.originit.union.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserTagDaoTest {

    @Autowired
    UserTagDao userTagDao;

    @Test
    public void test() {
        System.out.println(userTagDao.selectCount(null));
    }
}