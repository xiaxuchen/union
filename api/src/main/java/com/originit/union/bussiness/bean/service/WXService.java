package com.originit.union.bussiness.bean.service;

import com.originit.union.bussiness.bean.UserList;
import com.soecode.wxtools.exception.WxErrorException;

/**
 * @author super
 * @date 2020/2/3 15:05
 * @description 执念
 */
public interface WXService {
    public UserList getUserList(String token,int tagList,int curPage,int pageSize) throws WxErrorException;
}
