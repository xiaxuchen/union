package com.originit.union.api.controller;

import com.originit.union.WXbussiness.bean.MaterialItemBean;
import com.originit.union.WXbussiness.bean.TagListBean;
import com.originit.union.WXbussiness.bean.UserListBean;
import com.originit.union.WXbussiness.service.WXService;
import com.originit.union.api.protocol.CardCode;
import com.originit.union.api.protocol.CardInfo;
import com.soecode.wxtools.api.IService;
import com.soecode.wxtools.exception.WxErrorException;
import com.xxc.response.anotation.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value="/push")
@ResponseResult
public class PushController {
    private static  final  String mycardcode="851357382948";//318346353200
    private String opendid1="o1U3Tjj8m_Kqq9tJzT7B10Uj4NoA";//夏
    private  String openid="o1U3TjoBfIKeo_dyR380-Z4Vw_vU";
    private static  final  String GET_CARDCODE="https://api.weixin.qq.com/card/user/getcardlist?access_token=TOKEN";
    private static  final String card_id="p1U3TjhRfDRJoktXgL4_eLh6DDVY";
    private static  final  String  CARD_INFO="https://api.weixin.qq.com/card/membercard/userinfo/get?access_token=TOKEN";
    @Autowired
    private IService iService;
    @Autowired
    WXService  wxService;
    @RequestMapping("/userList")
    @ResponseBody
    /*public UserListBean getAllRole(@RequestParam String token, @RequestParam int tagList,@RequestParam int curPage,@RequestParam int pageSize) throws WxErrorException {
        return wxService.getUserList(token,tagList,curPage,pageSize);
    }*/
    public UserListBean getAllRole(@RequestParam String token, @RequestParam int tagList, @RequestParam int curPage, @RequestParam int pageSize) throws WxErrorException {
        return wxService.getUserList(token,tagList,curPage,pageSize);
    }
    @RequestMapping ("/tagList")
    @ResponseBody
    public List<TagListBean> getTagList() throws WxErrorException, IOException {
       return  wxService.getTagList();
    }
    @RequestMapping("/materials")
    @ResponseBody
    public List<MaterialItemBean> getMaterialList() throws WxErrorException, IOException {
        return  wxService.getMaterialList();
    }
    @RequestMapping("/test")
    @ResponseBody
    public String getCard() throws WxErrorException, IOException {
    String url=CARD_INFO.replace("TOKEN",iService.getAccessToken());
    System.out.println((String) iService.post(url,new CardInfo(card_id,"851357382948").toJson()));
        return (String) iService.post(url,new CardInfo(card_id,mycardcode).toJson());
    }
    @RequestMapping("/test1")
    @ResponseBody
    public String getCode() throws WxErrorException, IOException {
        String url=GET_CARDCODE.replace("TOKEN",iService.getAccessToken());
        System.out.println((String) iService.post(url,new CardCode(opendid1,card_id).toJson()));
        return (String) iService.post(url,new CardCode("o1U3Tjj8m_Kqq9tJzT7B10Uj4NoA",card_id).toJson());
    }
    @RequestMapping("/testUser")
    @ResponseBody
    public String  getUserInfo(@RequestParam String openid) throws WxErrorException {
        return wxService.getUserInfo(openid);
    }
    @RequestMapping("/userbind")
    @ResponseBody
    public String setUserBind () throws WxErrorException, IOException {
       // System.out.println(wxService.setUserBind().toString());
    return  wxService.setUserBind();
}

}


