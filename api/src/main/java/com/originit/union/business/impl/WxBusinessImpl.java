package com.originit.union.business.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.originit.union.api.util.ExcelUtil;
import com.originit.union.api.util.MyStringUtil;
import com.originit.union.business.bean.*;
import com.originit.union.business.WxBusiness;
import com.originit.union.api.protocol.CardCode;
import com.originit.union.api.protocol.CardInfo;
import com.originit.union.api.protocol.MaterialsList;
import com.originit.union.api.util.WXDateUtil;
import com.originit.union.entity.dto.PushInfoDto;
import com.originit.union.entity.dto.UserBindDto;
import com.soecode.wxtools.api.IService;
import com.soecode.wxtools.api.WxConsts;
import com.soecode.wxtools.api.WxService;
import com.soecode.wxtools.bean.SenderContent;
import com.soecode.wxtools.bean.WxOpenidSender;
import com.soecode.wxtools.bean.WxUserList;
import com.soecode.wxtools.bean.result.SenderResult;
import com.soecode.wxtools.bean.result.WxBatchGetMaterialResult;
import com.soecode.wxtools.bean.result.WxUserListResult;
import com.soecode.wxtools.bean.result.WxUserTagResult;
import com.soecode.wxtools.exception.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

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
     * post 获取用户信息列表
     */
    String USER_INFO="https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";

    /**
     *
     * @param openid  用户的openid
     * @return   用户的信息
     * @throws WxErrorException
     */
    @Override
    public String getUserInfo(String openid) throws WxErrorException {
        WxUserList.WxUser user = iService.getUserInfoByOpenId(new WxUserList.WxUser.WxUserGet(openid, WxConsts.LANG_CHINA));
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
    public UserListBean getUserList(String token, int tagList, int curPage, int pageSize) throws WxErrorException, IOException {
        List<UserInfoBean>  listuser=new ArrayList<UserInfoBean>();
        UserListBean userListBeanEntity =new UserListBean();
        String nextopenid=null;
        WxUserListResult result = iService.batchGetUserOpenId(nextopenid);
        nextopenid=result.getNext_openid();
        WxUserListResult result2 = iService.batchGetUserOpenId(nextopenid);
        System.out.println("result2"+result2.getData());
        System.out.println("nextopenid:"+result.getNext_openid());
        String openid= result.getData().toString().replace("WxOpenId [openid=[","").replace("]","").replace(" ","");
        //string转化为list
        List<String> openidList = Arrays.asList(openid.split(","));
        int  total= result.getTotal();
        System.out.println("total:"+total);
        int totalPage=0;
        if ( total%pageSize==0){
            totalPage=total/pageSize;}
        else  {totalPage=total/pageSize+1;}
        System.out.println("totalPage:"+totalPage);
        long startTime = System.currentTimeMillis(); // 获取开始时间
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
        System.out.println("程序开始执行时间："+startTime);
        List<WxUserList.WxUser.WxUserGet> list = new ArrayList<>();
        for (int i=(curPage-1)*pageSize;i<=(curPage-1)*pageSize+pageSize-1;i++){
            WxUserList.WxUser.WxUserGet userGet = new WxUserList.WxUser.WxUserGet(openidList.get(i), WxConsts.LANG_CHINA);
            list.add(userGet);
        }
        WxUserList userList = iService.batchGetUserInfo(list);
        for (int i=0;i<userList.getUser_info_list().size();i++){
            UserInfoBean userInfoBeanEntity =new UserInfoBean();
            userInfoBeanEntity.setOpenid(userList.getUser_info_list().get(i).getOpenid());
            userInfoBeanEntity.setName(userList.getUser_info_list().get(i).getNickname());
            userInfoBeanEntity.setSex((userList.getUser_info_list().get(i).getSex()));
            userInfoBeanEntity.setHeadImg(userList.getUser_info_list().get(i).getHeadimgurl());
            userInfoBeanEntity.setPhone("123");
            userInfoBeanEntity.setSubscribeTime( WXDateUtil.GetDateTimeWithTimeStamp(Integer.parseInt(userList.getUser_info_list().get(i).getSubscribe_time()) ));
            TagListBean tag=new TagListBean(1,"VIP用户");
            userInfoBeanEntity.setTagListBean(tag);
            listuser.add(userInfoBeanEntity);
        }
        userListBeanEntity.setTotal(listuser.size());
        userListBeanEntity.setUserInfoBean(listuser);
        long endTime = System.currentTimeMillis(); // 获取结束时间
        System.out.println("程序结束执行时间："+endTime);
        System.out.println(df.format(new Date()));// new Date()为获取当前系统时间
        System.out.println("程序总运行时间： " + (endTime - startTime) + "ms");
        return userListBeanEntity;
    }
    //获取标签列表
    @Override
    public List<TagListBean> getTagList() throws WxErrorException {
        List<TagListBean> tagListBeans = new ArrayList<TagListBean>();
        WxUserTagResult result = iService.queryAllUserTag();
        for (int i=0;i<result.getTags().size();i++){
            TagListBean tagListBean =new TagListBean(result.getTags().get(i).getId(),result.getTags().get(i).getName());
            tagListBeans.add(tagListBean);
        }
        return tagListBeans;
    }

    /**
     * //获取素材列表
     * @return
     * @throws WxErrorException
     * @throws IOException
     */
    @Override
    public List<MaterialItemBean> getMaterialList()  {
        List<MaterialItemBean> materialItemBeans = new ArrayList<MaterialItemBean>();
        try {
            //图文信息
            WxBatchGetMaterialResult result = iService.batchGetMeterial("news", 0, 20);
            System.out.println("name:"+result.getItem().get(1));
            for (int i=0;i<result.getItem_count();i++){
                MaterialItemBean materialItemBean = new MaterialItemBean();
                materialItemBean.setMedia_id(result.getItem().get(1).getMedia_id());
                materialItemBean.setName(result.getItem().get(i).getContent().getNews_item().get(0).getTitle());
                materialItemBean.setUrl(result.getItem().get(i).getContent().getNews_item().get(0).getUrl());
                materialItemBean.setUpdate_time(WXDateUtil.GetDateTimeWithTimeStamp(Integer.parseInt(result.getItem().get(i).getUpdate_time())));
                materialItemBeans.add(materialItemBean);
            }
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return materialItemBeans;
    }

    @Override
    public List<String> getAllUserOpenIds() throws WxErrorException {
        String nextopenid=null;
        WxUserListResult result = iService.batchGetUserOpenId(nextopenid);
        String openid= result.getData().toString().replace("WxOpenId [openid=[","").replace("]","").replace(" ","");
        //string转化为list
        List<String> openidList = Arrays.asList(openid.split(","));
        System.out.println(openidList.size());
        return openidList;
    }

    @Override
    public List<UserBindDto> getUserBindDtos(List<String> openidList) throws WxErrorException, IOException {
        List<String>  codeList= new ArrayList<>();
        List<UserBindDto> userBindDtos= new ArrayList<>();
        System.out.println("listsize是："+openidList.size());
          for (int i=0;i<=openidList.size() ;i++){
           String user=iService.get(USER_INFO.replace("ACCESS_TOKEN",iService.getAccessToken()).replace("OPENID",openidList.get(i)),null);
            //josn转化为map
            Map<String,Object> jsonToMap3 = JSONObject.parseObject(user.toString());
              openidList.add((String) jsonToMap3.get("openid"));
            String url= GET_CARD_CODE.replace("TOKEN",iService.getAccessToken());
            String cardCode=iService.post(url,new CardCode(openidList.get(i),card_id).toJson());
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
                    userBindDto.setOpenid(openidList.get(i));
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
        System.out.println("phone"+excelUserBeanList.get(1).getUserphone());
        List<String>  phonelist=new ArrayList<String>();
        for (int i=0;i<excelUserBeanList.size(); i++){
            phonelist.add(excelUserBeanList.get(i).getUserphone());
        }
        return phonelist;
    }

    @Override
    public  List<UserInfoBean>  getUserListByid(List<String> openidList) throws WxErrorException {
        List<UserInfoBean> listuser = new ArrayList<UserInfoBean>();       List<WxUserList.WxUser.WxUserGet> list = new ArrayList<>();
        for (int i=0;i<openidList.size();i++){
            WxUserList.WxUser.WxUserGet userGet = new WxUserList.WxUser.WxUserGet(openidList.get(i), WxConsts.LANG_CHINA);
            list.add(userGet);
        }
        WxUserList userList = iService.batchGetUserInfo(list);
        for (int i=0;i<userList.getUser_info_list().size();i++){
            UserInfoBean userInfoBeanEntity =new UserInfoBean();
            userInfoBeanEntity.setOpenid(userList.getUser_info_list().get(i).getOpenid());
            userInfoBeanEntity.setName(userList.getUser_info_list().get(i).getNickname());
            userInfoBeanEntity.setSex((userList.getUser_info_list().get(i).getSex()));
            userInfoBeanEntity.setHeadImg(userList.getUser_info_list().get(i).getHeadimgurl());
            TagListBean tag=new TagListBean(1,"VIP用户");
            userInfoBeanEntity.setPhone("123");
            userInfoBeanEntity.setSubscribeTime( WXDateUtil.GetDateTimeWithTimeStamp(Integer.parseInt(userList.getUser_info_list().get(i).getSubscribe_time()) ));
            userInfoBeanEntity.setTagListBean(tag);
            listuser.add(userInfoBeanEntity);
        }
        return listuser;
    }

    @Override
    public Long PushInfo(List<String> openidList, PushInfoDto pushInfoDto) {
        WxOpenidSender sender = new WxOpenidSender();
        SenderResult result = null;
        sender.setTouser(openidList);
        if (pushInfoDto.getType()==1){
            sender.setMsgtype("text");
            //群发文本内容
            sender.setText(new SenderContent.Text(pushInfoDto.getContent()));
        }else  if (pushInfoDto.getType()==2){
            sender.setMsgtype("mpnews");
            //群发文本内容
            sender.setText(new SenderContent.Text(pushInfoDto.getContent()));
        }
        try {
             result = iService.sendAllByOpenid(sender);
            System.out.println(result.toString());

        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        return  result.getMsg_id();
    }
}
