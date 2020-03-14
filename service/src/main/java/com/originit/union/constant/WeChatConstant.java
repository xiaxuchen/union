package com.originit.union.constant;

public interface WeChatConstant {

    /**
     * 地区为中国
     */
    String LANG = "zh_CN";

    /**
     * 获取卡劵列表
     */
    String URL_GET_CARD_CODE ="https://api.weixin.qq.com/card/user/getcardlist?access_token=TOKEN";

    /**
     * 会员卡id
     */
    String CARD_ID = "p1U3TjhRfDRJoktXgL4_eLh6DDVY";

    /**
     * 获取用户卡劵详细信息
     */
    String URL_GET_CARD_INFO = "https://api.weixin.qq.com/card/membercard/userinfo/get?access_token=TOKEN";

    /**
     * TOKEN在字符串中的模板
     */
    String TOKEN_TEMPLATE = "TOKEN";

    /**
     * 电话号码所在的字段
     */
    String PHONE_FIELD = "membership_number";

    /**
     * 获取媒体
     */
    String GET_MEDIA = "https://api.weixin.qq.com/cgi-bin/material/get_material?access_token=ACCESS_TOKEN";

    /**
     * 请求的属性中保存webXmlMessage
     */
    String ATTR_WEB_XML_MESSAGE = "webXmlMessage";

    /**
     * 阻止默认的输出
     */
    String PREVENT_DEFAULT_OUT = "prevent_default_out";

    /**
     * 开启客服
     */
    String CLIENT_SERVE_START = "#1";

    /**
     * 结束客服
     */
    String CLIENT_SERVE_END = "#2";
}
