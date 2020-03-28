package com.originit.union.entity.converter;

import com.originit.union.entity.domain.UserInfo;
import com.originit.union.entity.vo.UserInfoVO;
import com.originit.union.util.DateUtil;
import com.soecode.wxtools.bean.WxUserList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

/**
 * 微信用户相关的转换类
 * @author xxc、
 */
@Mapper(imports = {TagConverter.class,DateUtil.class})
public interface WeChatUserConverter {
    WeChatUserConverter INSTANCE = Mappers.getMapper(WeChatUserConverter.class);

    @Mappings({
            @Mapping(target = "sex",expression = "java(WeChatUserConverter.convertSex(user.getSex()))"),
            @Mapping(source = "openid",target = "id"),
            @Mapping(source = "headimgurl",target = "headImg"),
            @Mapping(source = "nickname",target = "name")
    })
    UserInfoVO to(WxUserList.WxUser user);

    @Mappings({
            @Mapping(target = "sex",expression = "java(WeChatUserConverter.convertSex(user.getSex()))"),
            @Mapping(source = "id",target = "id"),
            @Mapping(source = "headImg",target = "headImg"),
            @Mapping(source = "name",target = "name"),
            @Mapping(source = "phone",target = "phone"),
            @Mapping(source = "pushCount",target = "pushCount"),
            @Mapping(target = "subscribeTime",expression = "java(DateUtil.toTimeMillions(user.getSubscribeTime()))"),
            @Mapping(source = "tags",target = "tags")
    })
    UserInfoVO to (UserInfo user);

    /**
     * 将openId返回，而不是userId
     * @param user
     * @return
     */
    @Mappings({
            @Mapping(target = "sex",expression = "java(WeChatUserConverter.convertSex(user.getSex()))"),
            @Mapping(source = "openId",target = "id"),
            @Mapping(source = "headImg",target = "headImg"),
            @Mapping(source = "name",target = "name"),
            @Mapping(source = "phone",target = "phone"),
            @Mapping(source = "pushCount",target = "pushCount"),
            @Mapping(target = "subscribeTime",expression = "java(DateUtil.toTimeMillions(user.getSubscribeTime()))"),
            @Mapping(source = "tags",target = "tags")
    })
    UserInfoVO toPushUser (UserInfo user);

    /**
     * 转换性别
     * @param sex
     * @return
     */
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
