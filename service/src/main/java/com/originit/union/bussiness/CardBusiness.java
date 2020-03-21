package com.originit.union.bussiness;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.originit.common.exceptions.RemoteAccessException;
import com.originit.union.bussiness.protocol.CardCode;
import com.originit.union.constant.WeChatConstant;
import com.soecode.wxtools.api.IService;
import com.soecode.wxtools.exception.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Map;

/**
 * @author xxc、
 */
@Component
public class CardBusiness {

    private IService wxService;

    @Autowired
    public void setWxService(IService iService) {
        this.wxService = iService;
    }

    /**
     * 获取卡的code
     * @param openId 用户的openId
     * @param cardId 卡劵的id
     * @return 用户的卡劵code
     */
    public String getCardCode (String openId, String cardId) {
        Assert.notNull(cardId,"卡劵id不能为空");
        try {
            String url = WeChatConstant.URL_GET_CARD_CODE.replace("TOKEN",wxService.getAccessToken());
            String cardInfo = wxService.post(url,new CardCode(openId,WeChatConstant.CARD_ID).toJson());
            Map<String,Object> cardsMap = JSONObject.parseObject(cardInfo);
            //获取card列表
            JSONArray cardList= (JSONArray) cardsMap.get("card_list");
            if (cardList == null || cardList.size() == 0) {
                return null;
            }
            for (Object obj : cardList) {
                // 获取卡信息
                JSONObject card = (JSONObject) obj;
                Object curCardId = card.get("card_id");
                if (curCardId == null) {
                    continue;
                }
                // 如果是指定卡则返回code
                if (cardId.equals(curCardId)) {
                    return (String) card.get("code");
                }
            }
            return null;
        } catch (IOException | WxErrorException e) {
            throw new RemoteAccessException("获取token异常",e);
        }
    }
}
