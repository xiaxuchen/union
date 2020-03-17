package com.originit.union.bussiness;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserBusinessTest {

    @Autowired
    private UserBusiness business;

    @Test
    public void fillUserInfo() {
    }

    @Test
    public void getPhone() {
        System.out.println(business.getPhone("o1U3TjoBfIKeo_dyR380-Z4Vw_vU"));
    }

    @Test
    public void batchGetAllUser() {
        System.out.println(this.getClass().getClassLoader().getResource("images").getPath());
    }

}