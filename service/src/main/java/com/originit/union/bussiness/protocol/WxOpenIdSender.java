package com.originit.union.bussiness.protocol;

import com.soecode.wxtools.bean.WxOpenidSender;

public class WxOpenIdSender extends WxOpenidSender {

    private String clientmsgid;

    public String getClientmsgid() {
        return clientmsgid;
    }

    public void setClientmsgid(String clientmsgid) {
        this.clientmsgid = clientmsgid;
    }
}
