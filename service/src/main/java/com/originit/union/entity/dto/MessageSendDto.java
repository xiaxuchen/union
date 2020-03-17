package com.originit.union.entity.dto;

import lombok.Data;

/**
 * 客户经理发送消息的DTO
 * @author xxc、
 */
@Data
public class MessageSendDto {

    private String content;

    private Integer type;

    private String userId;
}
