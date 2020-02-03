package com.originit.union.bussiness.bean.service.servicelmpl;

import com.alibaba.fastjson.JSONArray;
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

    private String USER_INFO="https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";//获取用户信息
    private String USER_LIST = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN";//获取用户列表
    String GET_TAG="https://api.weixin.qq.com/cgi-bin/tags/get?access_token=ACCESS_TOKEN";//获取标签列表
    @Override
    public UserList getUserList(String token,int tagList,int curPage,int pageSize) throws WxErrorException {
        List<UserInfo>  listuser=new ArrayList<UserInfo>();
        UserList userListEntity=new UserList();
        String s=iService.get(USER_LIST.replace("ACCESS_TOKEN",iService.getAccessToken()),null);
        //josn转化为map
        Map<String,Object> jsonToMap = JSONObject.parseObject(s);
        String b=((JSONObject) jsonToMap).getString("data");
        int  total= Integer.parseInt(jsonToMap.get("total").toString());
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
            userInfoEntity.setId(list.get(i));
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
//{"tags":[{"id":2,"name":"星标组","count":0},{"id":114,"name":"10","count":2}]}
    @Override
    public List<TagList> getTagList() throws WxErrorException {
     //   System.out.println(iService.get(GET_TAG.replace("ACCESS_TOKEN",iService.getAccessToken()),null));
        String taglisturl=iService.get(GET_TAG.replace("ACCESS_TOKEN",iService.getAccessToken()),null);
        Map<String,Object> jsonToMap = JSONObject.parseObject(taglisturl);
        //String taglist1=jsonToMap.get("tags").toString().replace("[","").replace("]","");
        List<Map> jsonToList = JSONArray.parseArray(jsonToMap.get("tags").toString(),Map.class);
        List<TagList> tagLists = new ArrayList<TagList>();
        for (int i=0;i<jsonToList.size();i++){
            int a= (int) jsonToList.get(i).get("id");
            TagList tagList=new TagList(a, (String) jsonToList.get(i).get("name"));
            tagLists.add(tagList);
            System.out.println("jsonToList："+tagLists.toString());
        }


      //  Map<String,Object> jsonToMap1 = JSONObject.parseObject(taglist1);
      //  System.out.println(jsonToMap1.toString());
          return  tagLists;
    }
}
