package com.originit.union.bussiness.bean.service;

import com.originit.union.bussiness.bean.TagList;
import com.originit.union.bussiness.bean.UserList;
import com.soecode.wxtools.exception.WxErrorException;

import java.util.List;

/**
 * @author super
 * @date 2020/2/3 15:05
 * @description 执念
 */
public interface WXService {
    //获取用户列表
    public UserList getUserList(String token,int tagList,int curPage,int pageSize) throws WxErrorException;
    //获取标签列表
    public List<TagList> getTagList() throws WxErrorException;
}
