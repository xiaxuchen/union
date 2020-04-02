package com.originit.union.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.formula.functions.T;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 经理与其对应聊天用户关系
 */
@TableName("chat_user_agent")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatUserAgentEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * wechatMessageId
     */
    @TableId
    private Long chatUserAgentId;

    /**
     * 聊天经理的id
     */
    private Long userId;

    /**
     * 聊天微信用户的openId
     */
    private String openId;

    /**
    * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    private LocalDateTime gmtModified;
}
