package com.originit.union.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("user_agent")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserAgentEntity {

    private Long id;

    /**
     * 用户的电话号码
     */
    private String userPhone;

    /**
     * 经理的id
     */
    private Long agent;

}
