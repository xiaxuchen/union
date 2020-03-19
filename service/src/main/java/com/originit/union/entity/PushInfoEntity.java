package com.originit.union.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用于存储推送的信息
 * @author xxc、
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("push_info")
public class PushInfoEntity implements Serializable {

    @TableId
    private Long id;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    private LocalDateTime gmtModified;

    /**
     * 推送人id
      */
    private Long pusher;

    /**
     * 推送类型 1代表文本，2代表媒体类型
     */
    private Integer type;

    /**
     * 推送内容，可能为media_id
     */
    private String content;

    /**
     * 推送的id，这个是微信公众平台的id
     */
    private Long pushId;


}
