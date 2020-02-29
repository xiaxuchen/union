package com.originit.union.api.controller;


import com.originit.union.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

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
    public void searchUserList() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/push/userList")
                .param("username","xxcisbest")
                .param("password","123456")
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}
