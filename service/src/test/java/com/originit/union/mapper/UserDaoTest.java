package com.originit.union.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.originit.union.entity.UserBindEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserDaoTest {

    @Autowired
    UserDao userDao;

    public ArrayList<UserBindEntity> getList() {
        final UserBindEntity userBindEntity1 = new UserBindEntity();
        userBindEntity1.setOpenId("asfdlka;sdfjas;");
        final UserBindEntity userBindEntity2 = new UserBindEntity();
        userBindEntity2.setOpenId("asfasdlfkj;aidofjaif");
        final ArrayList<UserBindEntity> userList = new ArrayList<>();
        userList.add(userBindEntity1);
        userList.add(userBindEntity2);
        return userList;
    }

    @Test
    public void insertUsersIfNotExist() {

        final ArrayList<UserBindEntity> list = getList();
        final LambdaQueryWrapper<UserBindEntity> qw = new QueryWrapper<UserBindEntity>().lambda();
        List<String> ids = new ArrayList<>();
        for (UserBindEntity userBindEntity : list) {
            ids.add(userBindEntity.getOpenId());
        }
        qw.in(UserBindEntity::getOpenId, ids);
        userDao.delete(qw);
        // 1、先清除已有的

        // 2、 插入一遍
        Assert.assertEquals(userDao.insertUsersIfNotExist(list),2);
        // 3、再插入一遍
        Assert.assertEquals(userDao.insertUsersIfNotExist(list),0);

        // 4、清除
        userDao.delete(qw);
    }
}