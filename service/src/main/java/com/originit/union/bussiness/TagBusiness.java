package com.originit.union.bussiness;

import com.originit.common.exceptions.RemoteAccessException;
import com.soecode.wxtools.api.IService;
import com.soecode.wxtools.exception.WxErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 标签管理相关的微信端操作
 */
@Component
@Slf4j
public class TagBusiness {

    IService iService;

    @Autowired
    public void setIService(IService iService) {
        this.iService = iService;
    }

    /**
     * 创建标签
     * @param name 标签名
     * @return 标签id
     */
    public int createTag (String name) {
        try {
            return iService.createUserTag(name).getTag().getId();
        } catch (WxErrorException e) {
            log.error("创建标签失败:{}",e.getError().toString());
            throw new RemoteAccessException(e);
        }
    }

    /**
     * 删除标签
     * @param id 标签的id
     */
    public void deleteTag (int id) {
        try {
            iService.deleteUserTag(id);
        } catch (WxErrorException e) {
            log.error("删除标签失败:{}",e.getError().toString());
            throw new RemoteAccessException(e);
        }
    }

    /**
     * 更新标签
     * @param wechatTagId 标签的id
     * @param tagName 标签的名称
     */
    public void updateTag(Integer wechatTagId, String tagName) {
        try {
            iService.updateUserTagName(wechatTagId,tagName);
        } catch (WxErrorException e) {
            log.error("修改标签失败:{}",e.getError().toString());
            throw new RemoteAccessException(e);
        }
    }
}
