package com.originit.union.bussiness.bean.service.servicelmpl;

import com.alibaba.fastjson.JSONObject;
import com.originit.union.api.util.WXDateUtil;
import com.originit.union.bussiness.bean.TagList;
import com.originit.union.bussiness.bean.UserInfo;
import com.originit.union.bussiness.bean.UserList;
import com.originit.union.bussiness.bean.service.WXService;
import com.soecode.wxtools.api.IService;
import com.soecode.wxtools.exception.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author super
 * @date 2020/2/3 15:06
 * @description 执念
 */
@Service
public class WXServicelmpl implements WXService {
    @Autowired
    private IService iService;

    private String USER_INFO="https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
    private String USER_LIST = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN";//获取用户列表
    @Override
    public UserList getUserList(String token,int tagList,int curPage,int pageSize) throws WxErrorException {
        List<UserInfo>  listuser=new ArrayList<UserInfo>();
        UserList userListEntity=new UserList();
        String s=iService.get(USER_LIST.replace("ACCESS_TOKEN",iService.getAccessToken()),null);
        //josn转化为map
        Map<String,Object> jsonToMap = JSONObject.parseObject(s);
        String b=((JSONObject) jsonToMap).getString("data");
        int  total= (int) jsonToMap.get("total");
        System.out.println(total);
        int totalPage=0;
        if ( total%pageSize==0)
            totalPage=total/pageSize;
        else  totalPage=total/pageSize+1;
        System.out.println(totalPage);
        //josn转化为map
        Map<String,Object> jsonToMap2 = JSONObject.parseObject(b);
        String c=((JSONObject) jsonToMap2).getString("openid");
        //整理字符串格式
        String d= c.replace("[","").replace("]","").replace("\"","");
        //string转化为list
        List<String> list = Arrays.asList(d.split(","));
        for (int i=(curPage-1)*pageSize;i<=(curPage-1)*pageSize+pageSize-1;i++){
            String user=iService.get(USER_INFO.replace("ACCESS_TOKEN",iService.getAccessToken()).replace("OPENID",list.get(i)),null);
            System.out.println(user);
            //josn转化为map
            Map<String,Object> jsonToMap3 = JSONObject.parseObject(user);
            UserInfo userInfoEntity=new UserInfo();
            userInfoEntity.setId(i);
            userInfoEntity.setName((String) jsonToMap3.get("nickname"));
            userInfoEntity.setSex((Integer) jsonToMap3.get("sex"));
            userInfoEntity.setHeadImg((String) jsonToMap3.get("headimgurl"));
            userInfoEntity.setPhone("123");
            userInfoEntity.setSubscribeTime( WXDateUtil.GetDateTimeWithTimeStamp((int) jsonToMap3.get("subscribe_time")));
            TagList tag=new TagList(1,"VIP用户");
            userInfoEntity.setTagList(tag);
          //  listuser.add(userInfoEntity);
            listuser.add(userInfoEntity);
        }
        userListEntity.setTotal(listuser.size());
        userListEntity.setUserInfo(listuser);
        return userListEntity;
    }
}
