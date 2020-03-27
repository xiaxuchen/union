package com.originit.union.api.controller;

import com.originit.union.api.wxinterceptor.WXInterceptor;
import com.originit.union.constant.WeChatConstant;
import com.soecode.wxtools.api.IService;
import com.soecode.wxtools.api.WxConfig;
import com.soecode.wxtools.api.WxConsts;
import com.soecode.wxtools.api.WxMessageRouter;
import com.soecode.wxtools.bean.*;
import com.soecode.wxtools.exception.WxErrorException;
import com.soecode.wxtools.util.xml.XStreamTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 将类标注为控制器，是的spring boot能够自动扫描此类
 * 同时通过RequestMapping注解配置器URl映射,当前映射路径为/core
 */
@Controller
@RequestMapping("/core")
public class CoreController {

    private static Set<WXInterceptor> interceptors = new CopyOnWriteArraySet<>();

    public static void addInterceptor (WXInterceptor interceptor) {
        interceptors.add(interceptor);
    }

    @Autowired
    IService iService;

    /**
     * 校验微信公众号服务器
     * 使用GetMapping让方法只能被get方式调用，同时没有设置value值，则调用路径为localhost:8080/core
     * 使用ResponseBody返回字符串
     */
    @GetMapping
    @ResponseBody
    public String check(@RequestParam String signature, @RequestParam String timestamp, @RequestParam String nonce, @RequestParam String echostr, HttpServletRequest request) {
        //直接调用IService的checkSignature方法即可校验
        if (iService.checkSignature(signature, timestamp, nonce, echostr)) {
            final ServletContext context = request.getServletContext();
            context.setAttribute("signature",signature);
            context.setAttribute("timestamp",timestamp);
            context.setAttribute("nonce",nonce);
            context.setAttribute("appId",WxConfig.getInstance().getAppId());
            return echostr;
        }
        return null;
    }

    /**
     * 接入jssdk
     * @param url 使用jssdk的页面的URL
     * @param apis 需要的jssdk接口
     * @return 微信jssdk配置对象的json字符串给前台
     */
    @ResponseBody
    @GetMapping("/config")
    public WxJsapiConfig config(@RequestParam String url, @RequestParam("apis[]") List<String> apis) throws WxErrorException {
        //直接使用iService调用createJsapiConfig方法即可
        WxJsapiConfig jsapiConfig = iService.createJsapiConfig(url, apis);
        //由于该方法不会设置appid，故需通过WxConfig对象获取
        jsapiConfig.setAppid(WxConfig.getInstance().getAppId());
        return jsapiConfig;
    }

    @PostMapping
    public void handle(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        try {
            // 微信服务器推送过来的是XML格式。
            WxXmlMessage wxXmlMessage = XStreamTransformer.fromXml(WxXmlMessage.class, request.getInputStream());
            request.setAttribute(WeChatConstant.ATTR_WEB_XML_MESSAGE, wxXmlMessage);
            List<WXInterceptor> handleInterceptors = new ArrayList<>();
            boolean isForbidden = false;
            for (WXInterceptor interceptor : interceptors) {
                switch (interceptor.intercept(request,response))
                {
                    case WXInterceptor.FORRBIDE_OTHER: {
                        // 只能是单个拦截器可用
                        if (!isForbidden) {
                            handleInterceptors.add(interceptor);
                            isForbidden = true;
                        } else {
                            // 如果已经有了不共享的拦截器，则抛出异常
                            throw new IllegalStateException("has multi forbidden Interceptor");
                        }
                        break;
                    }
                    case WXInterceptor.SHARED_OTHER: {
                        // 可以多个拦截器共享
                        handleInterceptors.add(interceptor);
                        break;
                    }
                    default:{}
                }
            }
            for (WXInterceptor handleInterceptor : handleInterceptors) {
                handleInterceptor.handle(request,response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void autoLogin(String id,HttpSession session) {
//        if (session.getAttribute("user") == null)
//        {
//            session.setAttribute("user",userBindMapper.selectOneByOpenId(id));
//        }
//    }

    private void sendKfMessage(WxXmlMessage wx, String openId) {
        WxMessageRouter router = new WxMessageRouter(iService);
        router.rule().event(WxConsts.EVT_CLICK).handler((wxMessage, context, iService) -> new WxXmlOutTextMessage()).end();
        final WxXmlOutMessage outMessage = router.route(wx);
        if(outMessage != null)
        {
            KfSender sender = new KfSender();
            sender.setTouser(openId);
            sender.setMsgtype(WxConsts.CUSTOM_MSG_TEXT);
            sender.setText(new SenderContent.Text("您好!很高兴为您服务!"));
            try {
                iService.sendMessageByKf(sender);
            } catch (WxErrorException e) {
                e.printStackTrace();
            }
        }
    }

//    @GetMapping("/bind")
//    public String bind(String code,HttpSession session) throws WxErrorException {
//        WxOAuth2AccessTokenResult oAuth2AccessTokenResult = iService.oauth2ToGetAccessToken(code);
//        final String openid = oAuth2AccessTokenResult.getOpenid();
//        if(session.getAttribute("id") == null) {
//            session.setAttribute("id", openid);
//            //自动登录,由于网页授权和微信平台的请求不是同一源(session不是同一个)故重复登录
//            autoLogin(openid,session);
//        }
//        final UserBind bind = userBindMapper.selectOneByOpenId(openid);
//        if(bind == null) {
//            return "/bind";
//        }
//        return "/index";
//    }
}
