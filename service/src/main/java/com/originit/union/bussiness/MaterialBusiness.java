package com.originit.union.bussiness;

import com.originit.common.exceptions.RemoteAccessException;
import com.originit.union.entity.vo.MaterialVO;
import com.originit.union.util.DateUtil;
import com.soecode.wxtools.api.IService;
import com.soecode.wxtools.api.WxConsts;
import com.soecode.wxtools.bean.WxNewsInfo;
import com.soecode.wxtools.bean.result.WxBatchGetMaterialResult;
import com.soecode.wxtools.exception.WxErrorException;
import org.apache.shiro.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MaterialBusiness {

    IService wxService;

    @Autowired
    public void setWxService(IService wxService) {
        this.wxService = wxService;
    }

    /**
     * 获取图文列表
     */
    public List<MaterialVO> getMaterialList(int cur,int size)  {
        Assert.isTrue(size <= 20,"每一页不能超过20个");
        try {
            //图文信息
            WxBatchGetMaterialResult result = wxService.batchGetMeterial(WxConsts.MATERIAL_NEWS, cur,size);
            return result.getItem().stream().map(materialItem -> {
                System.out.println(materialItem.getContent());
                final WxNewsInfo wxNewsInfo = materialItem.getContent().getNews_item().get(0);
                MaterialVO vo = new MaterialVO();
                vo.setId(materialItem.getMedia_id());
                vo.setPic(wxNewsInfo.getThumb_url());
                vo.setTitle(wxNewsInfo.getTitle());
                vo.setUpdateTime(DateUtil.timeStampToStr(Long.parseLong(materialItem.getContent().getUpdate_time())));
                return vo;
            }).collect(Collectors.toList());
        } catch (WxErrorException e) {
            e.printStackTrace();
            throw new RemoteAccessException(e.getError());
        }
    }
}
