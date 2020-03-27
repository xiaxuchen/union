package com.originit.union.api.controller;

import com.originit.common.page.Pager;
import com.originit.union.entity.dto.GetChatUserDto;
import com.originit.union.entity.vo.ChatUserVO;
import com.originit.union.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;

@Controller
@Slf4j
public class WsChatController {

    private ChatService chatService;

    @Autowired
    public void setChatService(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * 筛选等待的用户
     * @param dto 查询的对象
     * @return 筛选后等待的用户
     */
    @MessageMapping("/waitUser/users")
    @SendToUser("/waitUser/users")
    public Pager<ChatUserVO> getWaitingUsers (@Payload GetChatUserDto dto) {
        log.info("get waiting user now，{}",dto.toString());
        return chatService.searchWaitingUser(dto,20);
    }

    /**
     * 搜索可接入的用户
     * @param dto 搜索用的dto
     * @return
     */
    @MessageMapping("/receivableUser/users")
    @SendToUser("/receivableUser/users")
    public Pager<ChatUserVO> getReceviableUser (@Payload GetChatUserDto dto) {
        log.info("get waiting user now，{}",dto.toString());
        return chatService.searchReceivableUsers(dto,20);
    }

    /**
     * 获取刷新页面后初始的等待用户
     * @return 等待的用户
     */
    @SubscribeMapping("/waiting/users")
    public Pager<ChatUserVO> getInitWaitingUsers () {
        return chatService.getWaitingUsers(1, 20);
    }

    /**
     * 获取当前经理对话的用户的信息
     * @return 对话的用户
     */
    @SubscribeMapping("/chatting/users")
    public List<ChatUserVO> getInitChatUsers (Principal principal) {
        log.info("获取初始的聊天用户信息");
        return chatService.getAgentUserVOs(Long.valueOf(principal.getName()));
    }

}
