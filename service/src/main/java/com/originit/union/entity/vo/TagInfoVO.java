package com.originit.union.entity.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TagInfoVO {

    /**
     * name : vip用户
     * wechatMessageId : 1
     * count: 用户数量
     */
    private Long id;
    private String name;
    private Integer count;

}
