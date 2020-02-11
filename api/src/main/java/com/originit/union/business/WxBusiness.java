package com.originit.union.business;

import com.originit.union.business.bean.MaterialItemBean;
import com.originit.union.business.bean.TagListBean;
import com.originit.union.business.bean.UserInfoBean;
import com.originit.union.business.bean.UserListBean;
import com.originit.union.entity.dto.PushInfoDto;
import com.originit.union.entity.dto.UserBindDto;
import com.soecode.wxtools.exception.WxErrorException;

import java.io.IOException;
import java.util.List;

/**
 * @author super
 * @date 2020/2/3 15:05
 * @description 执念
 */
public interface WxBusiness {
    /**
     * @param openid
     * @return /获取用户信息
     * @throws WxErrorException
     */

      String getUserInfo(String openid) throws WxErrorException;

    /** 获取用户列表
     * @param token
     * @param tagList  标签
     * @param curPage   当前页数
     * @param pageSize  一页显示几条数据
     * @return  getUserList
     * @throws WxErrorException
     */
     UserListBean getUserList(String token, int tagList, int curPage, int pageSize) throws WxErrorException, IOException;

    /**
     * //获取标签列表
     * @return
     * @throws WxErrorException
     */
     List<TagListBean> getTagList() throws WxErrorException;

    /**       //获取图片素材列表
     * @return   List<MaterialItemBean>
     * @throws WxErrorException
     * @throws IOException
     */

     List<MaterialItemBean> getMaterialList() throws WxErrorException, IOException;

    /**
     * 获取微信数据库中的所有的用户的openId
     * @return
     * @throws WxErrorException
     * @throws IOException
     */
      List<String> getAllUserOpenIds() throws WxErrorException, IOException;


    /**
     *  获取会员的绑定信息的值，会筛选掉非会员用户（具体参考{@link UserBindDto}）
     * @param list   openid列表
     * @return  UserBindDto  返回openid和phone的值
     */
    List<UserBindDto> getUserBindDtos(List<String> list) throws WxErrorException, IOException;

    /**
     * 根据Excel表格获取用户的phone
     * @return phone的list
     */
    List<String>   getUseridByExclePhone(String filename);

    /**
     * 根据用户id获取用户信息
     * @param openidlist
     * @return  用户信息列表
     * @throws WxErrorException
     */
    List<UserInfoBean>  getUserListByid(List<String> openidlist) throws WxErrorException;


    /**
     * 推送信息到指定用户 （目前支持文字信息和图文信息）
     * @param openidList 用户列表
     * @param pushInfoDto   其中的，type为1表示文本消息，为2表示图文消息，content对应为文本内容和微信公众平台的media_id
     */
    void PushInfo(List<String> openidList, PushInfoDto pushInfoDto);
}
