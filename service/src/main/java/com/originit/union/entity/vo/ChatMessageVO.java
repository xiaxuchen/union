package com.originit.union.entity.vo;

import lombok.*;

import java.sql.Time;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class ChatMessageVO {

    private String userId;

    private String message;

    private Boolean isUser;

    private Integer type;

    private String time;
}
