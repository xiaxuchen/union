package com.originit.union.api.wxinterceptor;

import com.originit.common.annotation.Interceptor;
import com.originit.union.constant.WeChatConstant;
import com.soecode.wxtools.bean.WxXmlMessage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Interceptor
public class PrintInterceptor implements WXInterceptor {
    @Override
    public int intercept(HttpServletRequest request, HttpServletResponse response) {
        return SHARED_OTHER;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        WxXmlMessage wxXmlMessage = (WxXmlMessage) request.getAttribute(WeChatConstant.ATTR_WEB_XML_MESSAGE);
        System.out.println(wxXmlMessage);
        Object prevent = request.getAttribute(WeChatConstant.PREVENT_DEFAULT_OUT);
        if (prevent == null) {
            PrintWriter writer = response.getWriter();
            writer.print("success");
            writer.close();
        }
    }
}
