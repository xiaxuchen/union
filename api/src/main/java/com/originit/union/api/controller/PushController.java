package com.originit.union.api.controller;

import com.originit.union.business.bean.MaterialItemBean;
import com.originit.union.business.bean.TagListBean;
import com.originit.union.business.bean.UserListBean;
import com.originit.union.business.WxBusiness;
import com.originit.union.entity.dto.PushInfoDto;
import com.originit.union.service.PushInfoService;
import com.originit.union.service.UserService;
import com.soecode.wxtools.api.IService;
import com.soecode.wxtools.bean.*;
import com.soecode.wxtools.bean.result.*;
import com.soecode.wxtools.exception.WxErrorException;
import com.xxc.response.anotation.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.*;

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
    private String USER_LIST = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN";
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
    private PushInfoService pushInfoService;
    @Autowired
    public void setPushInfoService(PushInfoService pushInfoService) {
        this.pushInfoService = pushInfoService;
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
    public UserListBean getAllRole(@RequestParam String token, @RequestParam int tagList, @RequestParam int curPage, @RequestParam int pageSize) throws WxErrorException, IOException {
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

    /**
     * 获取素材信息
     * @return
     * @throws WxErrorException
     * @throws IOException
     */
    @RequestMapping("/materials")
    @ResponseBody
    public List<MaterialItemBean> getMaterialList() throws WxErrorException, IOException {

        return  wxBusiness.getMaterialList();
    }
    @RequestMapping("/test")
    @ResponseBody
    public String test()  {
        PreviewSender sender = new PreviewSender();
        //设置openid或者微信号，优先级为wxname高
        sender.setTouser(openid);
        sender.setMsgtype("mpnews");
        sender.setMpnews(new SenderContent.Media("wSlKNDaFbswh6rGTiNma__oVrm6SkiUbCm54Hthk2DI"));
        Long a=null;
        try {
            SenderResult result = iService.sendAllPreview(sender);
            System.out.println(result.toString());
             a =  result.getMsg_id();
        } catch (WxErrorException e) {
            e.printStackTrace();
        }

        return null;
    }


    @RequestMapping("/getUserInfoByExcle")
    @ResponseBody
    public List<String> getUserInfoById() throws WxErrorException {
        //1 根据上传的Excel获取相关的phone值
        String filename="C:/Users/Super丶执念/Desktop/会员信息.xlsx";

      //  String  filename="static/会员导入信息模板.xlsx";
       List<String>  phonelist =wxBusiness.getUseridByExclePhone(filename);
       //2 根据获取的phonelist查找用户的openid
  //     List<String> openidlist= userService.getUseridByphone(phonelist);
       /* List<String> list=new ArrayList<>();
        list.add(0,"o1U3TjoBfIKeo_dyR380-Z4Vw_vU");
        list.add(1,"o1U3TjjBpjnPviGDxd7HSKjlH0y0");
        return   wxBusiness.getUserListByid(list);*/

       return  phonelist;
    }

    /**
     *添加用户推送信息
     * @param openidList 用户列表
     * @param pushInfoDto  推送的信息，type为1表示文本消息，为2表示图文消息，content对应为文本内容和微信公众平台的media_id
     * @throws WxErrorException
     * @throws IOException
     */
    @RequestMapping("/push")
    @ResponseBody
    public void addPushInfo(List<String> openidList, PushInfoDto pushInfoDto)  {
        if (pushInfoDto.getType()!=1||pushInfoDto.getType()!=2) {
            Long pushId = wxBusiness.PushInfo(openidList, pushInfoDto);
            pushInfoDto.setPushId(pushId);
            pushInfoService.addPushInfo(openidList, pushInfoDto);
        }
    }

}


