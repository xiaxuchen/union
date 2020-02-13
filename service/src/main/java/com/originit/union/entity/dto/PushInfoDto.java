package com.originit.union.entity.dto;

import com.originit.common.validator.annotation.EnumValue;
import com.originit.common.validator.group.CreateGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author super
 * @date 2020/2/11 17:19
 * @description 执念
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PushInfoDto {
    /**
     * 推送的信息，type为1表示文本消息，为2表示图文消息，content对应为文本内容和微信公众平台的media_id
     */
    @EnumValue(intValues = {1,2},groups = CreateGroup.class,message = "推送类型只能为指定值")
    private  Integer  type;

    @NotBlank(groups = {CreateGroup.class},message = "推送的内容不能为空")
    private String content;

    /**
     * 微信端的推送id，用于获取推送状态，删除推送等操作
     */
    @NotNull(groups = CreateGroup.class,message = "微信端的推送id不能为空")
    private Long pushId;

    /**
     * 推送的系统用户的id
     */
    @NotNull(groups = CreateGroup.class,message = "推送者的用户id不能为空")
    private Long pusher;
}
