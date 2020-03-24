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
 * 当前聊天用户的信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("chat_user")
public class ChatUserEntity implements Serializable {


    public interface STATE {
        /**
         * 没有要求客服服务
         */
        int NEVER = -1;

        /**
         * 排队中
         */
        int WAIT = 0;

        /**
         * 已接受
         */
        int RECEIVE = 1;
    }

    @TableId
    private Long chatUserId;

    /**
     * 用户的openId
     */
    private String openId;

    /**
     * 用户当前状态
     */
    private Integer state;

    /**
    * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    private LocalDateTime gmtModified;
}
