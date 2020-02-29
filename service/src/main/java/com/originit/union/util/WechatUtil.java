package com.originit.union.util;

import com.originit.common.util.SpringUtil;
import com.originit.union.constant.WeChatConstant;
import com.soecode.wxtools.api.IService;
import com.soecode.wxtools.exception.WxErrorException;

public class WechatUtil {

    /**
     * 直接替换TOKEN
     */
    public static String  replaceToken (String url) throws WxErrorException {
        IService wxService = SpringUtil.getBean(IService.class);
        return url.replace(WeChatConstant.TOKEN_TEMPLATE,wxService.getAccessToken());
    }
}
