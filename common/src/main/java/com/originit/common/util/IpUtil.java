package com.originit.common.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class IpUtil {

    /**
     * 获取本地的公网ip
     * @return IP地址
     */
    public static String getLocalIP() {
        String ip = "http://pv.sohu.com/cityjson?ie=utf-8";

        String inputLine = "";
        String read = "";
        try {
            URL url = new URL(ip);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            while ((read = in.readLine()) != null) {
                inputLine += read;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        inputLine = inputLine.substring(inputLine.indexOf('{') - 1,inputLine.length() - 1);
        ip = (String) JSON.parseObject(inputLine).get("cip");
        log.info("若无法显示经理图片，请看这里是否与服务器ip一致: {}",ip);
        return ip;
    }
}
