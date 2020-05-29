package com.originit.union.api.wxinterceptor;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.originit.common.annotation.Interceptor;
import com.originit.union.constant.WeChatConstant;
import com.originit.union.entity.PushInfoEntity;
import com.originit.union.service.PushService;
import com.originit.union.util.DateUtil;
import com.soecode.wxtools.api.WxConsts;
import com.soecode.wxtools.bean.WxXmlMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * .推送结果的拦截器
 * 当发送推送之后，不会立刻收到推送，而是由微信服务器去排队发送，
 * 中间需要一点时间，当推送发送成功或者失败，都会发送一个推送结果事件告知，
 * 我们通过该拦截器去拦截事件并记录推送结果
 */
@Interceptor
@Slf4j
public class PushResultInterceptor implements WXInterceptor{

    private PushService pushService;

    @Autowired
    public void setPushService(PushService pushService) {
        this.pushService = pushService;
    }

    @Override
    public int order() {
        return -1;
    }

    @Override
    public int intercept(HttpServletRequest request, HttpServletResponse response) throws Exception {
        WxXmlMessage message = (WxXmlMessage) request.getAttribute(WeChatConstant.ATTR_WEB_XML_MESSAGE);
        if (message.getMsgType().equals(WxConsts.XML_MSG_EVENT) && message.getEvent().equals(WxConsts.EVT_MASS_SEND_JOB_FINISH)) {
            return WXInterceptor.FORRBIDE_OTHER;
        }
        return WXInterceptor.NOT_INTEREST;
    }

    @Override
    @Async
    public void handle(HttpServletRequest request, HttpServletResponse response,WxXmlMessage message) throws Exception {
        if (message != null)
            log.info("【推送反馈】信息:{}",message.toString());
        // 更新推送消息
        pushService.update(new UpdateWrapper<PushInfoEntity>().lambda()
                .set(PushInfoEntity::getSendCount,message.getSentCount())
                .set(PushInfoEntity::getErrorCount,message.getErrorCount())
                .set(PushInfoEntity::getStatus,PushInfoEntity.STATUS.SENT)
                .set(PushInfoEntity::getGmtModified, DateUtil.toLocalDateTime(message.getCreateTime()))
                .eq(PushInfoEntity::getPushId,message.getMsgId()));
    }

}
