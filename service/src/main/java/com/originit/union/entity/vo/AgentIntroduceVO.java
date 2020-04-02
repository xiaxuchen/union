package com.originit.union.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 经理介绍发送介绍时的VO
 * @author xxc、
 */
@Data
public class AgentIntroduceVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String headImg;

    private String des;
}
