package com.originit.union.api.controller;

import com.originit.union.business.bean.MaterialItemBean;
import com.originit.union.business.bean.TagListBean;
import com.originit.union.business.bean.UserInfoBean;
import com.originit.union.business.bean.UserListBean;
import com.originit.union.business.WxBusiness;
import com.originit.union.api.protocol.CardCode;
import com.originit.union.api.protocol.CardInfo;
import com.originit.union.service.UserService;
import com.soecode.wxtools.api.IService;
import com.soecode.wxtools.exception.WxErrorException;
import com.xxc.response.anotation.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value="/push")
@ResponseResult
public class PushController {
    private static  final  String mycardcode="851357382948";
    /*
        夏openid
     */
    private String opendid1="o1U3Tjj8m_Kqq9tJzT7B10Uj4NoA";
    /*
        执念openid
     */
    private  String openid="o1U3TjoBfIKeo_dyR380-Z4Vw_vU";
    private static  final  String GET_CARDCODE="https://api.weixin.qq.com/card/user/getcardlist?access_token=TOKEN";
    private static  final String card_id="p1U3TjhRfDRJoktXgL4_eLh6DDVY";
    private static  final  String  CARD_INFO="https://api.weixin.qq.com/card/membercard/userinfo/get?access_token=TOKEN";
    @Autowired
    private IService iService;

    WxBusiness wxBusiness;
    @Autowired
    public void setWxBusiness(WxBusiness wxBusiness) {
        this.wxBusiness = wxBusiness;
    }

    private UserService userService;
    @Autowired
    public void setUserService(UserService userService){
        this.userService=userService;
    }
    /**
     *
     * @param token
     * @param tagList
     * @param curPage  当前页
     * @param pageSize  一页显示几条数据
     * @return  用户信息列表
     * @throws WxErrorException
     */
    @RequestMapping("/userList")
    @ResponseBody
    public UserListBean getAllRole(@RequestParam String token, @RequestParam int tagList, @RequestParam int curPage, @RequestParam int pageSize) throws WxErrorException {
        return wxBusiness.getUserList(token,tagList,curPage,pageSize);
    }

    /**
     * //获取标签列表
     * @return
     * @throws WxErrorException
     */
    @RequestMapping ("/tagList")
    @ResponseBody
    public List<TagListBean> getTagList() throws WxErrorException {
       return  wxBusiness.getTagList();
    }
    @RequestMapping("/materials")
    @ResponseBody
    public List<MaterialItemBean> getMaterialList() throws WxErrorException, IOException {
        return  wxBusiness.getMaterialList();
    }
    @RequestMapping("/test")
    @ResponseBody
    public String getCard() throws WxErrorException, IOException {
    String url=CARD_INFO.replace("TOKEN",iService.getAccessToken());
    System.out.println(iService.post(url,new CardInfo(card_id,"851357382948").toJson()));
        return iService.post(url,new CardInfo(card_id,mycardcode).toJson());
    }
    @RequestMapping("/test1")
    @ResponseBody
    public String getCode() throws WxErrorException, IOException {
        String url=GET_CARDCODE.replace("TOKEN",iService.getAccessToken());
        System.out.println(iService.post(url,new CardCode(opendid1,card_id).toJson()));
        return iService.post(url,new CardCode("o1U3Tjj8m_Kqq9tJzT7B10Uj4NoA",card_id).toJson());
    }
    @RequestMapping("/getUserInfoByExcle")
    @ResponseBody
    public List<UserInfoBean> getUserInfoById() throws WxErrorException {
        //1 根据上传的Excel获取相关的phone值
        String filename="C:/Users/Super丶执念/Desktop/会员信息.xlsx";
       List<String>  phonelist =wxBusiness.getUseridByExclePhone(filename);
       //2 根据获取的phonelist查找用户的openid
       List<String> openidlist= userService.getUseridByphone(phonelist);
       /* List<String> list=new ArrayList<>();
        list.add(0,"o1U3TjoBfIKeo_dyR380-Z4Vw_vU");
        list.add(1,"o1U3TjjBpjnPviGDxd7HSKjlH0y0");
        return   wxBusiness.getUserListByid(list);*/

       return  null;
    }


}


