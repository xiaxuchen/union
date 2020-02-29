package com.originit.union.bussiness;

import com.originit.common.exceptions.RemoteAccessException;
import com.originit.union.entity.dto.PushInfoDto;
import com.soecode.wxtools.api.IService;
import com.soecode.wxtools.api.WxConsts;
import com.soecode.wxtools.bean.PreviewSender;
import com.soecode.wxtools.bean.SenderContent;
import com.soecode.wxtools.bean.WxOpenidSender;
import com.soecode.wxtools.bean.WxQrcode;
import com.soecode.wxtools.bean.result.QrCodeResult;
import com.soecode.wxtools.bean.result.SenderResult;
import com.soecode.wxtools.exception.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

@Component
public class MessageBusiness {

    private IService wxService;

    @Autowired
    public void setWxService(IService wxService) {
        this.wxService = wxService;
    }

    /**
     * 将字符串转换为消息类型字符
     * @param type
     * @return
     */
    public String getMsgType (Integer type) {
        switch (type) {
            case 1:{
                return WxConsts.MASS_MSG_MPNEWS;
            }
            case 0:{
                return WxConsts.MASS_MSG_TEXT;
            }
            default:{
                throw new IllegalArgumentException("type is illegal,must be 0 or 1");
            }
        }
    }

    private void prepareSender (String type,String content,SenderContent sender) {
        switch (type) {
            case WxConsts.MASS_MSG_MPNEWS:{
                sender.setMpnews(new SenderContent.Media(content));
                break;
            }
            case WxConsts.MASS_MSG_TEXT:{
                sender.setText(new SenderContent.Text(content));
                break;
            }
            default:{
                throw new IllegalArgumentException("type is illegal,must be mpnews or text");
            }
        }
    }

    /**
     * 预览消息,但该能力每日调用次数有限制（100次）
     * 异步调用
     * @param openId 预览的用户
     * @param content 预览的消息.如果是素材则为mediaId，如果为文本则为文本
     * @return 消息id
     */
    @Async
    public ListenableFuture<Long> preview(String openId, String type, String content) {
        PreviewSender sender = new PreviewSender();
        sender.setTouser(openId);
        sender.setMsgtype(type);
        prepareSender(type,content,sender);
        try {
            SenderResult result = wxService.sendAllPreview(sender);
            return AsyncResult.forValue(result.getMsg_id());
        } catch (WxErrorException e) {
            e.printStackTrace();
            return AsyncResult.forExecutionException(new RemoteAccessException(e.getError()));
        }
    }


    /**
     * 获取临时的二维码
     * @param eventKey 信息
     * @param expireTime 过期时间
     * @return 二维码地址
     */
    public String generateTempQRCode (String eventKey,int expireTime) {
        WxQrcode code = new WxQrcode();
        code.setAction_name("QR_STR_SCENE");
        code.setAction_info(new WxQrcode.WxQrActionInfo(new WxQrcode.WxQrActionInfo.WxScene(eventKey)));
        code.setExpire_seconds(expireTime);
        try {
            QrCodeResult result = wxService.createQrCode(code);
            return "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + result.getTicket();
        } catch (WxErrorException e) {
            e.printStackTrace();
            throw new RemoteAccessException(e.getError());
        }
    }

    /**
     * 推送消息给指定openid的用户
     * @param pushInfo 包含了用户的openid和推送消息的内容
     * @return 推送的id
     */
    public Long pushMessage (PushInfoDto pushInfo) {
        String msgType = getMsgType(pushInfo.getType());
        WxOpenidSender sender = new WxOpenidSender();
        sender.setTouser(pushInfo.getUsers());
        sender.setMsgtype(msgType);
        prepareSender(msgType,pushInfo.getContent(),sender);
        //群发文本内容
        try {
            SenderResult result = wxService.sendAllByOpenid(sender);
            return result.getMsg_id();
        } catch (WxErrorException e) {
            e.printStackTrace();
            throw new RemoteAccessException(e.getError());
        }
    }
}
