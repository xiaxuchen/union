package com.originit.union.api.util;

import net.sf.jsqlparser.expression.DateTimeLiteralExpression;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author super  //微信时间转Date
 * @date 2020/2/3 16:06
 * @description 执念
 */
public class WXDateUtil {
    public static String GetDateTimeWithTimeStamp(int subscribe_time)
    {
     ;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String subtime = sdf.format(new Date(subscribe_time*1000L));



        return subtime;
    }

}
