package com.originit.union.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("push_user")
public class PushUserEntity {

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
     * 接受者的Id
     */
    private Long receiverId;

    /**
     * 推送的Id
     */
    private Long pushId;
}
