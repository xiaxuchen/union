package com.originit.union.api.wxinterceptor;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.originit.common.annotation.Interceptor;
import com.originit.union.constant.WeChatConstant;
import com.originit.union.entity.PushInfoEntity;
import com.originit.union.service.PushInfoService;
import com.originit.union.util.DateUtil;
import com.soecode.wxtools.api.WxConsts;
import com.soecode.wxtools.bean.WxXmlMessage;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * .推送结果的拦截器
 */
@Interceptor
public class PushResultInterceptor implements WXInterceptor{

    private PushInfoService pushInfoService;

    @Autowired
    public void setPushInfoService(PushInfoService pushInfoService) {
        this.pushInfoService = pushInfoService;
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
    public void handle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        WxXmlMessage message = (WxXmlMessage) request.getAttribute(WeChatConstant.ATTR_WEB_XML_MESSAGE);
        // 更新推送消息
        pushInfoService.update(new UpdateWrapper<PushInfoEntity>().lambda()
                .set(PushInfoEntity::getSendCount,message.getSentCount())
                .set(PushInfoEntity::getErrorCount,message.getErrorCount())
                .set(PushInfoEntity::getStatus,PushInfoEntity.STATUS.SENT)
                .set(PushInfoEntity::getGmtModified, DateUtil.toLocalDateTime(message.getCreateTime()))
                .eq(PushInfoEntity::getPushId,message.getMsgId()));
    }
}
