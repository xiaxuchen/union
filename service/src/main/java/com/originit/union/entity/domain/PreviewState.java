package com.originit.union.entity.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PreviewState implements Serializable {

    /**
     * 预览id
     */
    private String id;

    /**
     * 是否已发送
     */
    private Boolean success;

    /**
     * 预览的消息类型
     */
    private String type;

    /**
     * 预览的消息内容
     */
    private String content;
}
