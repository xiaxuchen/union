package com.originit.union.WXbussiness.service;

import com.originit.union.WXbussiness.bean.MaterialItemBean;
import com.originit.union.WXbussiness.bean.TagListBean;
import com.originit.union.WXbussiness.bean.UserListBean;
import com.originit.union.entity.SysUserBindEnity;
import com.soecode.wxtools.exception.WxErrorException;

import java.io.IOException;
import java.util.List;

/**
 * @author super
 * @date 2020/2/3 15:05
 * @description 执念
 */
public interface WXService {
    //获取用户信息
    public  String getUserInfo(String openid) throws WxErrorException;
    //获取用户列表
    public UserListBean getUserList(String token, int tagList, int curPage, int pageSize) throws WxErrorException;
    //获取标签列表
    public List<TagListBean> getTagList() throws WxErrorException;
    //获取图片素材列表
    public List<MaterialItemBean> getMaterialList() throws WxErrorException, IOException;
    //会员用户手机绑定
    public  String setUserBind() throws WxErrorException, IOException;
}
