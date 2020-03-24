package com.originit.union.entity.dto;

import lombok.Data;

/**
 * 获取等待用户或是可回访用户的dto
 */
@Data
public class GetChatUserDto {

    /**
     * 当前是第几页
     */
    private Integer curPage;
    /**
     * 经理的id
     */
    private Long agentId;

    /**
     * 指定的标签的id
     */
    private Long tagId;

    /**
     * 搜索关键字
     */
    private String searchKey;

}
