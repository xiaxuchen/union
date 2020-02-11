package com.originit.union.api.controller;


import com.baomidou.mybatisplus.extension.service.IService;
import com.originit.union.business.WxBusiness;
import com.soecode.wxtools.api.WxService;
import com.soecode.wxtools.exception.WxErrorException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

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
@RunWith(MockitoJUnitRunner.class)
public class PushControllerTest {
@Autowired
WxBusiness wxBusiness;
@Test
    public void test() throws WxErrorException, IOException {
    long startTime = System.currentTimeMillis(); // 获取开始时间

           SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
            System.out.println("程序开始执行时间："+startTime);
            wxBusiness.getUserList("1",1,1,50);
    long endTime = System.currentTimeMillis(); // 获取结束时间
             System.out.println("程序结束执行时间："+endTime);
            System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
            System.out.println("程序总运行时间： " + (endTime - startTime) + "ms");
}
}
