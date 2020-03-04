package com.originit.union.api.controller;

import com.originit.union.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class UserControllerTest {

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
    private UserController userController;

    @Before
    public void setupMockMvc(){
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.webAppContextSetup(wac).build(); //初始化MockMvc对象
        session = new MockHttpSession();
    }

    @Test
    public void login() throws Exception {
        String json="{\"author\":\"HAHAHAA\",\"title\":\"Spring\",\"url\":\"http://tengj.top/\"}";
        // 在这边，通过这个MockMvc可以通过url进行调用，这里就是测试登录方法，我们可以通过同样的方式去访问刚刚的方法，
        // 但是一般情况下依赖的下层接口是没有实现的，那咱们就没法直接调用，我们可以mock模拟对象注入，我要查一查，我下层测试是没有模拟的
        mvc.perform(MockMvcRequestBuilders.post("/manager/login")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .param("username","xxcisbest")
                .param("password","123456")
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

}