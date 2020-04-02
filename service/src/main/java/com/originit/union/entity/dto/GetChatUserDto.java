package com.originit.union.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 获取等待用户或是可回访用户的dto
 */
@Data
public class GetChatUserDto implements Serializable {

    private static final long serialVersionUID = 1L;

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
