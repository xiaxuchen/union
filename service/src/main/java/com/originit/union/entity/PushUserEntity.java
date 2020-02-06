package com.originit.union.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("push_user")
public class PushUserEntity extends PK {

    /**
     * 接受者的Id
     */
    private Long receiverId;

    /**
     * 推送者的Id
     */
    private Long pusherId;
}
