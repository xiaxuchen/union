package com.originit.union.api.wxinterceptor;

import com.originit.common.annotation.Interceptor;
import com.originit.union.constant.WeChatConstant;
import com.soecode.wxtools.bean.WxXmlMessage;
import org.springframework.scheduling.annotation.Async;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Interceptor
public class PrintInterceptor implements WXInterceptor {

    @Override
    public int order() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int intercept(HttpServletRequest request, HttpServletResponse response) {
        return SHARED_OTHER;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,WxXmlMessage wxXmlMessage) throws IOException, InterruptedException {
        System.out.println(wxXmlMessage);
        Thread.sleep(100);
        Object prevent = request.getAttribute(WeChatConstant.PREVENT_DEFAULT_OUT);
        if (prevent == null) {
            PrintWriter writer = response.getWriter();
            writer.print("success");
            writer.close();
        }
    }
}
