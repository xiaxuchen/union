package com.originit.union.business.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.originit.union.api.util.ExcelUtil;
import com.originit.union.business.bean.*;
import com.originit.union.business.WxBusiness;
import com.originit.union.api.protocol.CardCode;
import com.originit.union.api.protocol.CardInfo;
import com.originit.union.api.protocol.MaterialsList;
import com.originit.union.api.util.WXDateUtil;
import com.originit.union.entity.dto.UserBindDto;
import com.soecode.wxtools.api.IService;
import com.soecode.wxtools.exception.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
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
public class WxBusinessImpl implements WxBusiness {
    @Autowired
    private IService iService;
    /**
     * 通过openid获取会员卡code
     */
    private static  final  String GET_CARD_CODE ="https://api.weixin.qq.com/card/user/getcardlist?access_token=TOKEN";
    /**
     * 会员卡id
     */
    private static  final String card_id="p1U3TjhRfDRJoktXgL4_eLh6DDVY";
    /**
     *  通过会员卡id和会员code获取用户信息
     */
    private static  final  String  CARD_INFO="https://api.weixin.qq.com/card/membercard/userinfo/get?access_token=TOKEN";
    /**
     * 获取用户信息
     */
    private String USER_INFO="https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
    /**
     *获取用户列表
     */
    private String USER_LIST = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN";
    /**
     * 获取用户标签列表
     */
    String GET_TAG="https://api.weixin.qq.com/cgi-bin/tags/get?access_token=ACCESS_TOKEN";
    /**
     * post 获取素材列表
     */
    String  MATER_LIST="https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=ACCESS_TOKEN";

    /**
     *
     * @param openid  用户的openid
     * @return   用户的信息
     * @throws WxErrorException
     */
    @Override
    public String getUserInfo(String openid) throws WxErrorException {
        String user=iService.get(USER_INFO.replace("ACCESS_TOKEN",iService.getAccessToken()).replace("OPENID",openid),null);
        return user.toString();
    }

    /**
     * //获取用户列表
     * @param token
     * @param tagList  标签
     * @param curPage   当前页数
     * @param pageSize  一页显示几条数据
     * @return
     * @throws WxErrorException
     */

    @Override
    public UserListBean getUserList(String token, int tagList, int curPage, int pageSize) throws WxErrorException {
        List<UserInfoBean>  listuser=new ArrayList<UserInfoBean>();
        UserListBean userListBeanEntity =new UserListBean();
        String s=iService.get(USER_LIST.replace("ACCESS_TOKEN",iService.getAccessToken()),null);
        //josn转化为map
        Map<String,Object> jsonToMap = JSONObject.parseObject(s);
        String b=((JSONObject) jsonToMap).getString("data");
        int  total= Integer.parseInt(jsonToMap.get("total").toString());
        System.out.println("total:"+total);
        int totalPage=0;
        if ( total%pageSize==0){
            totalPage=total/pageSize;}
        else  {totalPage=total/pageSize+1;}
        System.out.println("totalPage:"+totalPage);
        //josn转化为map
        Map<String,Object> jsonToMap2 = JSONObject.parseObject(b);
        String c=((JSONObject) jsonToMap2).getString("openid");
        //整理字符串格式
        String d= c.replace("[","").replace("]","").replace("\"","");
        //string转化为list
        List<String> list = Arrays.asList(d.split(","));
        for (int i=(curPage-1)*pageSize;i<=(curPage-1)*pageSize+pageSize-1;i++){
            String user=iService.get(USER_INFO.replace("ACCESS_TOKEN",iService.getAccessToken()).replace("OPENID",list.get(i)),null);
           // System.out.println(user);
            //json转化为map
            Map<String,Object> jsonToMap3 = JSONObject.parseObject(user);
            UserInfoBean userInfoBeanEntity =new UserInfoBean();
            userInfoBeanEntity.setOpenid(list.get(i));
            userInfoBeanEntity.setId(String.valueOf(i));
            userInfoBeanEntity.setName((String) jsonToMap3.get("nickname"));
            userInfoBeanEntity.setSex((Integer) jsonToMap3.get("sex"));
            userInfoBeanEntity.setHeadImg((String) jsonToMap3.get("headimgurl"));
            userInfoBeanEntity.setPhone("123");
            System.out.println("tagid_list:"+jsonToMap3.get("tagid_list"));
            userInfoBeanEntity.setSubscribeTime( WXDateUtil.GetDateTimeWithTimeStamp((int) jsonToMap3.get("subscribe_time")));
            TagListBean tag=new TagListBean(1,"VIP用户");
            userInfoBeanEntity.setTagListBean(tag);
          //  listuser.add(userInfoBeanEntity);
            listuser.add(userInfoBeanEntity);
        }
        userListBeanEntity.setTotal(listuser.size());
        userListBeanEntity.setUserInfoBean(listuser);
        return userListBeanEntity;
    }
    //获取标签列表
    @Override
    public List<TagListBean> getTagList() throws WxErrorException {
     //   System.out.println(iService.get(GET_TAG.replace("ACCESS_TOKEN",iService.getAccessToken()),null));
        String tagListUrl=iService.get(GET_TAG.replace("ACCESS_TOKEN",iService.getAccessToken()),null);
        //json转化为map
        Map<String,Object> jsonToMap = JSONObject.parseObject(tagListUrl);
        //String taglist1=jsonToMap.get("tags").toString().replace("[","").replace("]","");
        //json转化为List
        List<Map> jsonToList = JSONArray.parseArray(jsonToMap.get("tags").toString(),Map.class);
        List<TagListBean> tagListBeans = new ArrayList<TagListBean>();
        for (int i=0;i<jsonToList.size();i++){
            int a= (int) jsonToList.get(i).get("id");
            TagListBean tagListBean =new TagListBean(a, (String) jsonToList.get(i).get("name"));
            tagListBeans.add(tagListBean);
            System.out.println("jsonToList："+ tagListBeans.toString());
        }
          return tagListBeans;
    }
     //获取素材列表
    @Override
    public List<MaterialItemBean> getMaterialList() throws WxErrorException, IOException {
        MaterialsList materialsList=new MaterialsList("image",0,3);
        //      System.out.println(materialsList.toJson());
        String s=iService.post(MATER_LIST.replace("ACCESS_TOKEN",iService.getAccessToken()),materialsList.toJson());
        Map<String,Object> jsonToMap = JSONObject.parseObject(s);
        MaterialListBean materialListBean =new MaterialListBean();
        materialListBean.setItem_count((int) jsonToMap.get("item_count"));
        materialListBean.setTotal_count((int) jsonToMap.get("total_count"));
        List<Map> jsonToList = JSONArray.parseArray(jsonToMap.get("item").toString(),Map.class);
        List<MaterialItemBean> materialItemBeans = new ArrayList<MaterialItemBean>();
        for (int i=0;i<jsonToList.size();i++){
            MaterialItemBean materialItemBean = new MaterialItemBean();
            materialItemBean.setMedia_id((String) jsonToList.get(i).get("media_id"));
            materialItemBean.setName((String) jsonToList.get(i).get("name"));
            materialItemBean.setTags(null);
            materialItemBean.setUpdate_time(WXDateUtil.GetDateTimeWithTimeStamp((int)jsonToList.get(i).get("update_time")));
            materialItemBean.setUrl((String) jsonToList.get(i).get("url"));
            materialItemBeans.add(materialItemBean);
        }
        materialListBean.setMaterialItemBeans(materialItemBeans);
        System.out.println(materialListBean.toString());
        return materialItemBeans;
    }

    @Override
    public List<String> getAllUserOpenIds() throws WxErrorException {
        String s=iService.get(USER_LIST.replace("ACCESS_TOKEN",iService.getAccessToken()),null);
        //json转化为map
        Map<String,Object> jsonToMap = JSONObject.parseObject(s);
        String b=((JSONObject) jsonToMap).getString("data");
        //json转化为map
        Map<String,Object> jsonToMap2 = JSONObject.parseObject(b);
        String c=((JSONObject) jsonToMap2).getString("openid");
        //整理字符串格式
        String d= c.replace("[","").replace("]","").replace("\"","");
        //string转化为list
        List<String> list = Arrays.asList(d.split(","));
        System.out.println("openid:"+list);

        //i=opendidlist
       /* for (int i=0;i<=openidlist.size();i++){
            String user=iService.get(USER_INFO.replace("ACCESS_TOKEN",iService.getAccessToken()).replace("OPENID",list.get(i)),null);
            //josn转化为map
            Map<String,Object> jsonToMap3 = JSONObject.parseObject(user);
            openidlist.add((String) jsonToMap3.get("openid"));
        }*/
        return list;
    }

    @Override
    public List<UserBindDto> getUserBindDtos(List<String> list) throws WxErrorException, IOException {
        List<String>  codeList= new ArrayList<>();
        List<UserBindDto> userBindDtos= new ArrayList<>();
        System.out.println("listsize是："+list.size());
        int listsize=list.size();
        //i=list
          for (int i=9100;i<=listsize ;i++){
            String user=iService.get(USER_INFO.replace("ACCESS_TOKEN",iService.getAccessToken()).replace("OPENID",list.get(i)),null);
            //josn转化为map
            Map<String,Object> jsonToMap3 = JSONObject.parseObject(user);
            list.add((String) jsonToMap3.get("openid"));
            String url= GET_CARD_CODE.replace("TOKEN",iService.getAccessToken());
            String cardCode=iService.post(url,new CardCode(list.get(i),card_id).toJson());
            Map<String,Object> jsonToMap4 = JSONObject.parseObject(cardCode);
            //整理字符串格式
            String e= jsonToMap4.get("card_list").toString().replace("[","").replace("]","");
            if (e.isEmpty()==false){
                Map<String,Object> jsonToMap5 = JSONObject.parseObject(e);
                codeList.add((String) jsonToMap5.get("code"));
                System.out.println(jsonToMap5.get("code"));
                String a=  iService.post(CARD_INFO.replace("TOKEN",iService.getAccessToken()), new CardInfo(card_id, (String) jsonToMap5.get("code")).toJson());
                //josn转化为map
                Map<String,Object> jsonToMap6 = JSONObject.parseObject(a);
                if (jsonToMap6.get("membership_number").toString().isEmpty()==false){
                    UserBindDto userBindDto = new UserBindDto();
                    userBindDto.setOpenid(list.get(i));
                    userBindDto.setPhone((String) jsonToMap6.get("membership_number"));
                    userBindDtos.add(userBindDto);
                    System.out.println(userBindDto.toString());
                }
            }
        }
        return userBindDtos;
    }

    @Override
    public List<String> getUseridByExclePhone(String filename) {
        List<ExcelUserBean> excelUserBeanList=new ArrayList<ExcelUserBean>();
     //   String filename="C:/Users/Super丶执念/Desktop/会员信息.xlsx";
        excelUserBeanList= ExcelUtil.importXLS(filename);
        List<String>  phonelist=new ArrayList<String>();
        for (int i=0;i<=excelUserBeanList.size(); i++){
            phonelist.add(excelUserBeanList.get(i).getUserphone());
        }
        return phonelist;
    }

    @Override
    public  List<UserInfoBean>  getUserListByid(List<String> openidlist) throws WxErrorException {
        List<UserInfoBean>  listuser=new ArrayList<UserInfoBean>();
        for (int i=0;i<openidlist.size();i++){
            System.out.println(openidlist.get(1));
            String user=iService.get(USER_INFO.replace("ACCESS_TOKEN",iService.getAccessToken()).replace("OPENID",openidlist.get(i)),null);
            //json转化为map
            Map<String,Object> jsonToMap3 = JSONObject.parseObject(user);
            UserInfoBean userInfoBeanEntity =new UserInfoBean();
            userInfoBeanEntity.setOpenid(openidlist.get(i));
            userInfoBeanEntity.setId(String.valueOf(i));
            userInfoBeanEntity.setName((String) jsonToMap3.get("nickname"));
            userInfoBeanEntity.setSex((Integer) jsonToMap3.get("sex"));
            userInfoBeanEntity.setHeadImg((String) jsonToMap3.get("headimgurl"));
            userInfoBeanEntity.setPhone("123");
          //  System.out.println("tagid_list:"+jsonToMap3.get("tagid_list"));
            userInfoBeanEntity.setSubscribeTime( WXDateUtil.GetDateTimeWithTimeStamp((int) jsonToMap3.get("subscribe_time")));
            TagListBean tag=new TagListBean(1,"VIP用户");
            userInfoBeanEntity.setTagListBean(tag);
            //  listuser.add(userInfoBeanEntity);
            listuser.add(userInfoBeanEntity);
        }
        return  listuser;
    }
/*//用户绑定
    @Override
    public List<String> setUserBind() throws WxErrorException, IOException {
        String s=iService.get(USER_LIST.replace("ACCESS_TOKEN",iService.getAccessToken()),null);
        //josn转化为map
        Map<String,Object> jsonToMap = JSONObject.parseObject(s);
        String b=((JSONObject) jsonToMap).getString("data");
        int  total= Integer.parseInt(jsonToMap.get("total").toString());
        //josn转化为map
        Map<String,Object> jsonToMap2 = JSONObject.parseObject(b);
        String c=((JSONObject) jsonToMap2).getString("openid");
        //整理字符串格式
        String d= c.replace("[","").replace("]","").replace("\"","");
        //string转化为list
        List<String> list = Arrays.asList(d.split(","));
        List<String> openidlist=new ArrayList<String>();
        List<String>  codelist=new ArrayList<String>();
        List<UserBindEntity> userBindBeanList=new ArrayList<UserBindEntity>();
        //i=opendidlist
        for (int i=0;i<=100;i++){
            String user=iService.get(USER_INFO.replace("ACCESS_TOKEN",iService.getAccessToken()).replace("OPENID",list.get(i)),null);
            //josn转化为map
            Map<String,Object> jsonToMap3 = JSONObject.parseObject(user);
            openidlist.add((String) jsonToMap3.get("openid"));
            String url=GET_CARD_CODE.replace("TOKEN",iService.getAccessToken());
            String cardcode=iService.post(url,new CardCode(openidlist.get(i),card_id).toJson());
            Map<String,Object> jsonToMap4 = JSONObject.parseObject(cardcode);
            //整理字符串格式
            String e= jsonToMap4.get("card_list").toString().replace("[","").replace("]","");
            if (e.isEmpty()==false){
                Map<String,Object> jsonToMap5 = JSONObject.parseObject(e);
                codelist.add((String) jsonToMap5.get("code"));
                System.out.println(jsonToMap5.get("code"));
                String a=  iService.post(CARD_INFO.replace("TOKEN",iService.getAccessToken()), new CardInfo(card_id, (String) jsonToMap5.get("code")).toJson());
                //josn转化为map
                Map<String,Object> jsonToMap6 = JSONObject.parseObject(a);
                if (jsonToMap6.get("membership_number").toString().isEmpty()==false){
                    UserBindEntity userBindBean = new UserBindEntity();
                    userBindBean.setOpenId(openidlist.get(i));
                    userBindBean.setPhone((String) jsonToMap6.get("membership_number"));
                    userBindBeanList.add(userBindBean);
                    System.out.println(userBindBean.toString());
                }
            }
        }
        System.out.println(userBindBeanList.toString());
        return userBindBeanList.toString();
    }*/

}
