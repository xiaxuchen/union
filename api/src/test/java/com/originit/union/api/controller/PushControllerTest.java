package com.originit.union.api.controller;


import com.baomidou.mybatisplus.extension.service.IService;
import com.soecode.wxtools.api.WxService;
import com.soecode.wxtools.exception.WxErrorException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;

/**
 * @author super
 * @date 2020/2/7 3:24
 * @description 执念
 */
@RunWith(MockitoJUnitRunner.class)
public class PushControllerTest {
    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);

    }
@Test
    public void shorthand() throws WxErrorException, IOException {
    List<String> list=new ArrayList<String>();
    list.add("A");
    list.add("b");
        IService iService=Mockito.spy(IService.class);
    WxService wxService=Mockito.spy(WxService.class);
    wxService.getAccessToken();

     //   iService.count();
        iService.getBaseMapper();
        /*WXServicelmpl spy=Mockito.spy(WXServicelmpl.class);
        Mockito.when(spy.getUserInfo("asd")).thenReturn("aaa");*/

    }
}
