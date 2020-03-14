package com.originit.union.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("message")
public class MessageEntity {

    public interface TYPE{
        int TEXT = 0;
        int IMAGE = 1;
    }

    public interface STATE{
        int READ = 1;
        int WAIT = 0;
    }

    @TableId
    private Long id;

    private String content;

    private Integer type;

    /**
     * 用户的id
     */
    private String userId;

    /**
     * 客户经理的id
     */
    private String agentId;

    /**
     * 是否来自于用户
      */
    private Boolean fromUser;

    /**
     * 消息当前的状态
     */
    private Integer state;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
}
