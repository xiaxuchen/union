package com.originit.union.api.chat.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message<T> {

    public interface TYPE{
        int TEXT = 0;
        int IMAGE = 1;
    }

    public interface STATUS{
        int READ = 1;
        int WAIT = 0;
    }

    private T content;

    private Integer type;

    private Boolean fromUser;

    private String userOpenId;

    private Long agentId;

    private Integer status;

    private LocalDateTime sendTime;
}
