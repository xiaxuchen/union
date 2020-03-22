package com.originit.union.api.controller;

import com.originit.common.page.Pager;
import com.originit.union.chat.data.ChatUser;
import com.originit.union.api.util.ShiroUtils;
import com.originit.union.entity.MessageEntity;
import com.originit.union.entity.UserBindEntity;
import com.originit.union.entity.dto.MessageSendDto;
import com.originit.union.entity.vo.ChatMessageVO;
import com.originit.union.entity.vo.ChatUserVO;
import com.originit.union.service.ChatService;
import com.originit.union.util.DateUtil;
import com.xxc.response.anotation.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
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


    private ChatService messageService;

    @Autowired
    public void setMessageService(ChatService messageService) {
        this.messageService = messageService;
    }

    /**
     * 获取客户经理的所有用户列表
     * @return 用户列表
     */
    @GetMapping("/chatUserList")
    public List<ChatUserVO> getUserList () {
        Long userId = ShiroUtils.getUserInfo().getUserId();
        return messageService.getAgentUserVOs(userId);
    }

    private ChatUserVO reflectUser (ChatUser chatUser) {
        UserBindEntity userInfo = chatUser.getUserInfo();
        List<MessageEntity> messageList = chatUser.getMessageList();
        Long notRead = 0L;
        // 这里先暂时只支持文本消息
        ChatMessageVO lastMessage = null;
        String time = "";
        if (messageList != null && !messageList.isEmpty()) {
            MessageEntity message = messageList.get(messageList.size() - 1);
            notRead = messageList.stream().filter(m -> m.getState().equals(MessageEntity.STATE.WAIT)).count();
            lastMessage = ChatMessageVO.builder()
                    .isUser(message.getFromUser())
                    .message(message.getContent())
                    .type(message.getType())
                    .build();
            time = DateUtil.toDateTimeStr(message.getGmtCreate().toEpochSecond(ZoneOffset.of("+8")));
        }
        return ChatUserVO.builder()
                .id(userInfo.getOpenId())
                .name(userInfo.getName())
                .phone(userInfo.getPhone())
                .headImg(userInfo.getHeadImg())
                .notRead(notRead.intValue())
                .lastMessage(lastMessage)
                .time(time)
                .build();
    }

    /**
     * 获取经理与用户的最近历史消息
     * @param userId 微信用户的openId
     * @param lastId 获取的消息在此id的消息前
     * @return 历史消息列表
     */
    @GetMapping("/history/list")
    public List<ChatMessageVO> getHistoryMessages (@RequestParam String userId, @RequestParam(required = false) Long lastId){
        return messageService.getHistoryMessages(userId,ShiroUtils.getUserInfo().getUserId(),lastId)
                .stream().map(this::to).collect(Collectors.toList());
    }

    private ChatMessageVO to (MessageEntity message) {
        return ChatMessageVO.builder()
                .id(message.getWechatMessageId())
                .isUser(message.getFromUser())
                .message(message.getContent())
                .type(message.getType())
                .build();
    }

    /**
     * 获取聊天用户的未读消息
     * @param userId 用户的id
     * @return 用户的所有未读消息
     */
    @GetMapping("/chatMessageList")
    public List<ChatMessageVO> getMessages(@RequestParam String userId,@RequestParam(required = false) Long lastId) {
        return messageService.getWaitMessages(userId,ShiroUtils.getUserInfo().getUserId(),lastId)
                .stream().map(this::to).collect(Collectors.toList());
    }


    @GetMapping("/user/waiting")
    public Pager<ChatUserVO> getWaitingUsers (@RequestParam Integer curPage,@RequestParam(required = false,defaultValue = "10") Integer pageSize) {
        return messageService.getWaitingUsers(curPage, pageSize, 10);
    }

    /**
     * 接入该用户
     */
    @PostMapping("/session")
    public void receiveUser (@RequestBody List<String> userList) {
        for (String user : userList) {
            messageService.receiveUser(user,ShiroUtils.getUserInfo().getUserId());
        }
    }

    /**
     * 关闭某用户的会话
     * @param openId 用户的id
     */
    @DeleteMapping("/session")
    public void disConnectUser (@RequestParam String openId) {
        messageService.disconnectUser(openId,ShiroUtils.getUserInfo().getUserId());
    }

    @PutMapping("/list/read")
    public void readMessage (@RequestParam List<Long> messageIds) {
        messageService.readMessage(messageIds,ShiroUtils.getUserInfo().getUserId());
    }

    /**
     * 发送消息给用户
     * @param messageSendDto 消息
     * @return 消息的id
     */
    @PostMapping
    public Long sendMessage (@RequestBody MessageSendDto messageSendDto) {
        log.info("send message...");
        return messageService.sendMessage(MessageEntity.builder()
                .openId(messageSendDto.getUserId())
                .content(messageSendDto.getContent())
                .type(messageSendDto.getType())
                .userId(ShiroUtils.getUserInfo().getUserId().toString())
                .state(MessageEntity.STATE.WAIT)
                .fromUser(false)
                .gmtCreate(LocalDateTime.now())
                .build());
    }

}
