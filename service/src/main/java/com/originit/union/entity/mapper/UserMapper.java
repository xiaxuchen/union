package com.originit.union.entity.mapper;

import com.originit.union.entity.vo.UserInfoVO;
import com.originit.union.util.DateUtil;
import com.soecode.wxtools.bean.WxUserList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(imports = {DateUtil.class})
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mappings({
            @Mapping(target = "sex",expression = "java(UserMapper.convertSex(user.getSex()))"),
            @Mapping(source = "openid",target = "id"),
            @Mapping(source = "headimgurl",target = "headImg"),
            @Mapping(source = "nickname",target = "name")
    })
    UserInfoVO to(WxUserList.WxUser user);

    public static String convertSex(Integer sex) {
        if (sex == null) {
            return "未知";
        }
        switch (sex) {
            case 0: return "未知";
            case 1: return "男";
            case 2: return "女";
            default: return "未知";
        }
    }

}
