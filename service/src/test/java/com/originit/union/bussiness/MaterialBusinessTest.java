package com.originit.union.bussiness;

import com.originit.union.config.WechatConfig;
import com.originit.union.entity.vo.MaterialVO;
import com.soecode.wxtools.bean.PreviewSender;
import com.soecode.wxtools.bean.SenderContent;
import com.soecode.wxtools.bean.WxQrcode;
import com.soecode.wxtools.bean.result.QrCodeResult;
import com.soecode.wxtools.bean.result.SenderResult;
import com.soecode.wxtools.exception.WxErrorException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MaterialBusinessTest {

    @Test
    public void testDate() {
        System.out.println(new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA).format(new Date(new Timestamp(1582451540634L).getTime())));
    }

    @Autowired
    MaterialBusiness business;

    @Test
    public void getMaterialList() {
        List<MaterialVO> materialList = business.getMaterialList(0,20);
        for (MaterialVO materialItem : materialList) {
            System.out.println(materialItem);
        }
    }

    @Test
    public void preview() {
        PreviewSender sender = new PreviewSender();
        //设置openid或者微信号，优先级为wxname高
        sender.setTouser("o1U3Tjj8m_Kqq9tJzT7B10Uj4NoA");
        sender.setMsgtype("text");
        sender.setText(new SenderContent.Text("hahhahh"));
        Long a=null;
        try {
            SenderResult result = new WechatConfig().iService().sendAllPreview(sender);
            System.out.println(result.toString());
            a =  result.getMsg_id();
            System.out.println(a);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testQRCODe() {
        WxQrcode code = new WxQrcode();
        code.setAction_name("QR_STR_SCENE");
        code.setAction_info(new WxQrcode.WxQrActionInfo(new WxQrcode.WxQrActionInfo.WxScene("/push/preview?id=" + UUID.randomUUID().toString().replace("-",""))));
        code.setExpire_seconds(720);
        try {
            QrCodeResult result = new WechatConfig().iService().createQrCode(code);
            System.out.println("https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + result.getTicket());
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
    }

}