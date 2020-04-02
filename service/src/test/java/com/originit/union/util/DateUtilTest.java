package com.originit.union.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class DateUtilTest {

    @Test
    public void timeStampToStr() {
        System.out.println(DateUtil.timeStampToStr(1572663395L));
    }

    @Test
    public void toLocalDateTime() {
        System.out.println(DateUtil.toLocalDateTime("2018-4-4"));
    }
}