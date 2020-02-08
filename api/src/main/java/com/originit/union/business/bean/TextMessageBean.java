package com.originit.union.business.bean;

import lombok.Data;

/**
 * @author super
 * @date 2020/2/5 19:05
 * @description 执念
 */
@Data
public class TextMessageBean {
    private String ToUserName;
    private String FromUserName;
    private long CreateTime;
    private String MsgType;
    private String Content;
    private String MsgId;
}
