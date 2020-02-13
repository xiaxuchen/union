package com.originit.union.api.controller;


import com.baomidou.mybatisplus.extension.service.IService;
import com.originit.union.business.WxBusiness;
import com.originit.union.service.UserService;
import com.soecode.wxtools.api.WxService;
import com.soecode.wxtools.exception.WxErrorException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.verify;

/**
 * @author super
 * @date 2020/2/7 3:24
 * @description 执念
 */

@WebAppConfiguration
@RunWith(MockitoJUnitRunner.class)
public class PushControllerTest {
    @Autowired
    WebApplicationContext context;
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mvc;
    private MockHttpSession session;
    // 这里是模拟对象
    @Mock
    private UserService userService;
    // 这里是将模拟的对象注入到spring中去
    @Autowired
    @InjectMocks // 被注入mock对象的类一般是被测试类
    private PushController pushController;

    @Before
    public void setupMockMvc(){
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.webAppContextSetup(context).build(); //初始化MockMvc对象
        session = new MockHttpSession();
    }
@Test
    public void test()  {
   /* pushController.wxBusiness =wxBusiness;
    System.out.println(pushController.test());*/
   pushController.test();
}
}
