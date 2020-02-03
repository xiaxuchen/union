package com.originit.union.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用于存储推送的信息
 */
@Data
@TableName("push_info")
public class PushInfoEntity extends PK{
    /**
     * 推送人id
      */
    private Long pusher;

    /**
     * 推送类型
     */
    private Integer type;

    /**
     * 推送内容，可能为media_id
     */
    private String content;

    /**
     * 推送的id，这个是微信公众平台的id
     */
    private String pushId;


}
