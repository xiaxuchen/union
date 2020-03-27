package com.originit.union.entity.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TagInfoVO {

    /**
     * id 标签的id
     * name : vip用户
     * userTagId: 用户关联标签的关联id，用于删除关联关系
     * count: 用户数量
     */
    private Long id;
    private String name;
    private Long userTagId;
    private Integer count;

}
