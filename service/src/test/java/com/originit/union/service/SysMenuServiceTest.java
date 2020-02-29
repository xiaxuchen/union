package com.originit.union.service;

import com.originit.union.entity.SysMenuEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class SysMenuServiceTest {

    @Autowired
    private SysMenuService service;

    @Test
    public void selectSysMenuByRoleId() {
        System.out.println(service.selectSysMenuByRoleId(1L));
    }
}