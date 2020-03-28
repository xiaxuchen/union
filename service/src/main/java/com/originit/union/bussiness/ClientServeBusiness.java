package com.originit.union.bussiness;

import com.originit.common.exceptions.RemoteAccessException;
import com.originit.union.constant.WeChatConstant;
import com.originit.union.entity.MessageEntity;
import com.soecode.wxtools.api.IService;
import com.soecode.wxtools.api.WxConsts;
import com.soecode.wxtools.bean.KfSender;
import com.soecode.wxtools.bean.SenderContent;
import com.soecode.wxtools.bean.result.WxError;
import com.soecode.wxtools.exception.WxErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * @author xxc、
 */
@Component
@Slf4j
public class ClientServeBusiness {

    private IService wxService;

    @Autowired
    public void setWxService(IService wxService) {
        this.wxService = wxService;
    }

    /**
     * 发送客服经理的介绍
     * @param openId 用户的openId
     * @param name 客户经理的名称
     * @param des 客户经理的描述信息
     * @param headImg 客户经理的头像
     */
    public void sendAgentIntroduce(String openId,String name,String des,String headImg) {
        SenderContent.NewsList.News news = new SenderContent.NewsList.News();
        news.setTitle("客户经理:" + name);
        news.setDescription("客服简介:" + des);
        news.setPicurl(headImg);
        sendNewsMessage(openId,news);
    }


    /**
     * 发送文本消息
     * @param openId 用户的openId
     * @param text 文本消息
     */
    public void sendTextMessage (String openId,String text) {
        log.info("send text message:{},content:{}",openId,text);
        KfSender sender = new KfSender();
        sender.setTouser(openId);
        sender.setText(new SenderContent.Text(text));
        sender.setMsgtype(WxConsts.MASS_MSG_TEXT);
        try {
            final WxError wxError = wxService.sendMessageByKf(sender);
        } catch (WxErrorException e) {
            throw new RemoteAccessException("消息发送失败",e);
        }
    }

    /**
     * 发送图文消息
     * @param openId 用户的openId
     * @param news 图文消息
     */
    public void sendNewsMessage (String openId, SenderContent.NewsList.News news) {
        KfSender sender = new KfSender();
        sender.setTouser(openId);
        SenderContent.NewsList newsList = new SenderContent.NewsList(Arrays.asList(news));
        sender.setNews(newsList);
        sender.setMsgtype(WxConsts.MASS_MSG_NEWS);
        try {
            wxService.sendMessageByKf(sender);
        } catch (WxErrorException e) {
            throw new RemoteAccessException("发送图文消息失败",e);
        }
    }

    /**
     * 发送图片消息
     * @param openId 用户openId
     * @param mediaId 媒体id
     */
    public void sendImageMessage (String openId,String mediaId) {
        KfSender sender = new KfSender();
        sender.setTouser(openId);
        sender.setImage(new SenderContent.Media(mediaId));
        sender.setMsgtype(WxConsts.MASS_MSG_IMAGE);
        try {
            wxService.sendMessageByKf(sender);
        } catch (WxErrorException e) {
            throw new RemoteAccessException("发送图片失败",e);
        }
    }

    /**
     * 发送信息
     * @param type 信息类型
     * @param content 信息内容
     */
    public void sendMessage (String openId,Integer type,Object content) {
        switch (type) {
            case MessageEntity.TYPE.TEXT: {
                if (content instanceof String) {
                    sendTextMessage(openId, (String) content);
                    return;
                }
                break;
            }
            case MessageEntity.TYPE.IMAGE: {
                if (content instanceof String) {
                    sendImageMessage(openId, (String) content);
                    return;
                }
                break;
            }
        }
        throw new IllegalArgumentException("消息类型错误");
    }

    /**
     * 发送等待消息
     * @param openId 用户id
     */
    public void sendWaitMessage (String openId) {
        this.sendTextMessage(openId,"正在排队中，输入 "+ WeChatConstant.CLIENT_SERVE_END +" 可退出服务,请稍候...");
    }

    /**
     * 发送评价信息
     */
    public void sendAppraise () {

    }

    /**
     * 发送退出时的消息
     * @param openId 用户的openId
     */
    public void sendExitMessage(String openId) {
        this.sendTextMessage(openId,"服务已结束，谢谢您的使用");
    }

    /**
     * 发送清除的消息
     * @param openIds
     */
    public void sendClearMessage(List<String> openIds) {
        for (String openId : openIds) {
            this.sendTextMessage(openId, "客服正忙，请稍后重试");
        }
    }
}
