package com.originit.union.entity.dto;

import lombok.Data;

import java.util.List;

/**
 * @author super
 * @date 2020/2/11 17:19
 * @description 执念
 */
@Data
public class PushInfoDto {
    /**
     * 推送的信息，type为1表示文本消息，为2表示图文消息，content对应为文本内容和微信公众平台的media_id
     */
   private  int  type;
   private String content;
}
