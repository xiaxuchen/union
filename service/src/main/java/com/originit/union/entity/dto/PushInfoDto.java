package com.originit.union.entity.dto;

import com.originit.common.validator.annotation.EnumValue;
import com.originit.common.validator.group.CreateGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * @author super
 * @date 2020/2/11 17:19
 * @description 执念
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PushInfoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(min = 2,groups = CreateGroup.class,message = "至少要有两个人才能推送")
    private List<String> users;
    /**
     * 推送的信息，type为1表示文本消息，为2表示图文消息，content对应为文本内容和微信公众平台的media_id
     */
    @EnumValue(intValues = {0,1},groups = CreateGroup.class,message = "推送类型只能为指定值")
    private  Integer  type;

    @NotBlank(groups = {CreateGroup.class},message = "推送的内容不能为空")
    private String content;

    /**
     * 微信端的推送id，用于获取推送状态，删除推送等操作
     */
    private Long pushId;

    /**
     * 推送的系统用户的id
     */
    private Long pusher;
}
