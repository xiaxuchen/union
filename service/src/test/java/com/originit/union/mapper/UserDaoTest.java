package com.originit.union.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    public void selectUserByPhonesAndTags() {
        System.out.println(userDao.selectUserByPhonesAndTags(new Page<>(1, 2), Arrays.asList(), Arrays.asList(1)).getTotal());
    }
}