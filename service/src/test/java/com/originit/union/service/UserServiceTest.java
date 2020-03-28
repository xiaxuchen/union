package com.originit.union.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.concurrent.ThreadPoolExecutor;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    WeChatUserService userService;

    @Autowired
    ThreadPoolExecutor executor;

    @Test
    public void addOrUpdateUsers() {
    }


    //    /**
//     * 测试当传入空列表时是否抛异常
//     */
//    @Test(expected = IllegalArgumentException.class)
//    public void testAddUserIfNotExist1() {
//        userService.addUserIfNotExist(new ArrayList<>());
//    }
//
//    public List<String> clear () {
//        final List<String> openIds = Arrays.asList("afasfdasfaf", "afashjfahsf", "hsdiufuasfd", "dafasdfasfa");
//        // 先清除原先的
//        final QueryWrapper<UserBindEntity> qw = new QueryWrapper<>();
//        qw.in("open_id",openIds);
//        userService.remove(qw);
//        return openIds;
//    }
//
//    /**
//     * 测试正常情况
//     */
//    @Test
//    public void testAddUserIfNotExist() {
//        final List<String> openIds = clear();
//        userService.addUserIfNotExist(openIds);
//        userService.addUserIfNotExist(openIds);
//        clear();
//    }
//
//    @Test
//    public void getOpenidListWithoutPhone() {
//        final List<String> clear = clear();
//        final int size = userService.getOpenIdWithoutPhone().size();
//        userService.addUserIfNotExist(clear);
//        Assert.assertTrue(userService.getOpenIdWithoutPhone().size() - size >= clear.size());
//        clear();
//    }
//
//    @Test
//    public void updateUserBind() {
//        final List<String> id = clear();
//        userService.addUserIfNotExist(id);
//        userService.updateUserBind(id.stream().map(s -> UserBindDto.builder().openid(s).phone(s).build()).collect(Collectors.toList()));
//        id.forEach(s -> Assert.assertEquals(userService.getOne(new QueryWrapper<UserBindEntity>().lambda().eq(UserBindEntity::getId,s)).getPhone(),s));
//        clear();
//    }
//
//    /**
//     * 测试不正常情况
//     */
//    @Test(expected = IllegalArgumentException.class)
//    public void updateUserBind1() {
//        userService.updateUserBind(new ArrayList<>());
//    }
//
//    @Test
//    public void getUseridByphone() {
//        assertEquals(userService.getUserIdByPhone(Arrays.asList("18679990891","15607998858","18507993375","18879157697")).size(),4);
//    }
//
//    @Test
//    public void getAllUserBindInfo() {
//        final int size = userService.getAllUserBindInfo(1, 10).getData().size();
//        assertTrue(size >= 0 && size <= 10);
//    }

    @Test
    public void getUserInfoList() {
    }

    @Test
    public void addOrUpdateAllUser() {
    }

}