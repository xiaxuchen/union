package com.originit.union.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 用于传递刷新的数据
 * @author xxc、
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshChatVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<ChatUserVO> userList;

    private List<ChatMessageVO> messageList;

}
