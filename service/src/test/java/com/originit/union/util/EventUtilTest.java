package com.originit.union.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class EventUtilTest {

    @Test
    public void getEventKeyParams() {
        System.out.println(EventUtil.getEventKeyParams("/push/preview?wechatMessageId=123").get("id"));
    }
}