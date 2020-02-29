package com.originit.union.entity.mapper;

import com.originit.union.entity.vo.MaterialVO;
import com.soecode.wxtools.bean.result.WxBatchGetMaterialResult;
import org.mapstruct.factory.Mappers;

public interface WeChatMapper {

    WeChatMapper INSTANCE = Mappers.getMapper(WeChatMapper.class);

    MaterialVO to(WxBatchGetMaterialResult.MaterialItem item);
}
