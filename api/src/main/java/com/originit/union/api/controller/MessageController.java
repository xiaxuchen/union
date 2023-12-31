package com.originit.union.api.controller;

import com.originit.common.page.Pager;
import com.originit.common.util.ExceptionUtil;
import com.originit.union.api.util.ShiroUtils;
import com.originit.union.entity.AgentStateEntity;
import com.originit.union.entity.MessageEntity;
import com.originit.union.entity.UserBindEntity;
import com.originit.union.entity.dto.AgentStateDto;
import com.originit.union.entity.dto.MessageSendDto;
import com.originit.union.entity.vo.ChatMessageVO;
import com.originit.union.entity.vo.ChatUserVO;
import com.originit.union.exception.chat.ChatException;
import com.originit.union.service.ChatService;
import com.originit.union.util.DateUtil;
import com.xxc.response.anotation.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息相关
 * @author xxc、
 */
@RestController
@RequestMapping("/message")
@ResponseResult
@Slf4j
public class MessageController {


    private ChatService chatService;

    @Autowired
    public void setChatService(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * 获取客户经理的所有用户列表
     * @return 用户列表
     */
    @GetMapping("/chatUserList")
    public List<ChatUserVO> getUserList () {
        Long userId = ShiroUtils.getUserInfo().getUserId();
        return chatService.getAgentUserVOs(userId);
    }

    /**
     * 获取经理与用户的最近历史消息
     * @param userId 微信用户的openId
     * @param lastId 获取的消息在此id的消息前
     * @return 历史消息列表
     */
    @GetMapping("/history/list")
    public List<ChatMessageVO> getHistoryMessages (@RequestParam String userId, @RequestParam(required = false) Long lastId){
        return chatService.getHistoryMessages(userId,ShiroUtils.getUserInfo().getUserId(),lastId);
    }

    private ChatMessageVO to (MessageEntity message) {
        return ChatMessageVO.builder()
                .id(message.getWechatMessageId())
                .isUser(message.getFromUser())
                .message(message.getContent())
                .type(message.getType())
                .build();
    }

//    /**
//     * 获取聊天用户的未读消息
//     * @param userId 用户的id
//     * @return 用户的所有未读消息
//     */
//    @GetMapping("/chatMessageList")
//    public List<ChatMessageVO> getMessages(@RequestParam String userId,@RequestParam(required = false) Long lastId) {
//        return chatService.getWaitMessages(userId,ShiroUtils.getUserInfo().getUserId(),lastId)
//                .stream().map(this::to).collect(Collectors.toList());
//    }


    @GetMapping("/user/waiting")
    public Pager<ChatUserVO> getWaitingUsers (@RequestParam Integer curPage,@RequestParam(required = false,defaultValue = "10") Integer pageSize) {
        return chatService.getWaitingUsers(curPage, pageSize);
    }

    /**
     * 接入用户
     * @param userList 需要接入的用户
     * @return 接入失败的列表
     */
    @PostMapping("/session")
    public List<String> receiveUser (@RequestBody List<String> userList) {
        List<String> notReceiveUser = new ArrayList<>();
        for (String user : userList) {
            try {
                chatService.receiveUser(user,ShiroUtils.getUserInfo().getUserId());
            } catch (Exception e) {
                log.error(ExceptionUtil.buildErrorMessage(e));
                notReceiveUser.add(user);
            }
        }
        return notReceiveUser;
    }

    /**
     * 反向接入用户
     * @param userList 接入的用户列表
     * @return
     */
    @PostMapping("/session/receivable")
    public List<String> receiveNotWaitUser (@RequestBody List<String> userList) {
        List<String> notReceiveUser = new ArrayList<>();
        for (String user : userList) {
            try {
                chatService.receiveNotWaitUser(user,ShiroUtils.getUserInfo().getUserId());
            } catch (Exception e) {
                notReceiveUser.add(user);
            }
        }
        return notReceiveUser;
    }

    /**
     * 关闭某用户的会话
     * @param openId 用户的id
     */
    @DeleteMapping("/session")
    public void disConnectUser (@RequestParam String openId) {
        chatService.disconnectUser(openId,ShiroUtils.getUserInfo().getUserId());
    }

    @PutMapping("/list/read")
    public void readMessage (@RequestParam List<Long> messageIds,@RequestParam String openId) {
        chatService.readMessage(messageIds,openId,ShiroUtils.getUserInfo().getUserId());
    }

    /**
     * 发送消息给用户
     * @param messageSendDto 消息
     * @return 消息的id
     */
    @PostMapping
    public Long sendMessage (@RequestBody MessageSendDto messageSendDto) {
        log.info("send message...");
        return chatService.sendMessageToUser(MessageEntity.builder()
                .openId(messageSendDto.getUserId())
                .content(messageSendDto.getContent())
                .type(messageSendDto.getType())
                .userId(ShiroUtils.getUserInfo().getUserId())
                .state(MessageEntity.STATE.WAIT)
                .fromUser(false)
                .gmtCreate(LocalDateTime.now())
                .build());
    }

    /**
     * 配置客户经理
     * @param dto 设置当前经理的状态
     */
    @PutMapping("/agent")
    public void configAgent(@RequestBody AgentStateDto dto) {
        chatService.configAgent(dto);
    }

    /**
     * 获取经理的设置
     * @param userId 用户id
     * @return
     */
    @GetMapping("/agent")
    public AgentStateEntity getAgentConfig (@RequestParam Long userId) {
        return chatService.getAgentState(userId);
    }

}
