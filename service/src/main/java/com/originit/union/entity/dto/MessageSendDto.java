package com.originit.union.entity.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 客户经理发送消息的DTO
 * @author xxc、
 */
@Data
public class MessageSendDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String content;

    private Integer type;

    private String userId;
}
