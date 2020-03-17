package com.originit.union.api.controller;

import com.originit.common.page.Pager;
import com.originit.union.chat.data.ChatUser;
import com.originit.union.api.util.ShiroUtils;
import com.originit.union.entity.MessageEntity;
import com.originit.union.entity.UserBindEntity;
import com.originit.union.entity.dto.MessageSendDto;
import com.originit.union.entity.vo.ChatMessageVO;
import com.originit.union.entity.vo.ChatUserVO;
import com.originit.union.service.MessageService;
import com.originit.union.util.DateUtil;
import com.xxc.response.anotation.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
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


    private MessageService messageService;

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * 获取最新的服务器端的聊天信息
     */
//    @GetMapping("/refreshServe")
//    public RefreshChatVO refreshServe () {
//        Long userId = ShiroUtils.getUserInfo().getUserId();
//        List<ChatUser> userList = messageService.getUserList(userId, -1);
//        List<ChatMessageVO> messages = userList.stream().flatMap(chatUser -> {
//            return chatUser.getMessageList().stream().filter(message -> {
//                return message.getStatus().equals(Message.STATUS.WAIT);
//            });
//        }).map(message -> {
//            return ChatMessageVO.builder()
//                    .userId(message.getUserOpenId())
//                    .type(message.getType())
//                    .message(message.getContent().toString())
//                    .isUser(message.getFromUser())
//                    .time(DateUtil.getTime(message.getSendTime().toEpochSecond(ZoneOffset.of("+8"))))
//                    .build();
//        }).collect(Collectors.toList());
//        List<ChatUserVO> users = userList.stream().filter(chatUser -> {
//            return chatUser.getMessageList().stream().anyMatch(message -> message.getStatus().equals(Message.STATUS.WAIT));
//        }).map(this::reflectUser).collect(Collectors.toList());
//        RefreshChatVO refreshChatVO = new RefreshChatVO();
//        refreshChatVO.setMessageList(messages);
//        refreshChatVO.setUserList(users);
//        return refreshChatVO;
//    }

    /**
     * 获取客户经理的所有用户列表
     * @return 用户列表
     */
    @GetMapping("/chatUserList")
    public List<ChatUserVO> getUserList () {
        Long userId = ShiroUtils.getUserInfo().getUserId();
        List<ChatUser> userList = messageService.getUserList(userId, MessageService.ALL,MessageService.ALL);
        return userList.stream().map(this::reflectUser).collect(Collectors.toList());
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
            time = DateUtil.getTime(message.getGmtCreate().toEpochSecond(ZoneOffset.of("+8")));
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

    public List<ChatMessageVO> getMoreMessage () {

    }

    /**
     * 获取聊天用户的未读消息
     * @param userId 用户的id
     * @return 用户的所有未读消息
     */
    @GetMapping("/chatMessageList")
    public List<ChatMessageVO> getMessages(@RequestParam String userId,@RequestParam(required = false) Long lastId) {
        List<MessageEntity> messageList = messageService.getUser(userId, -1).getMessageList();
        List<ChatMessageVO> vos = messageList.stream()
                .sorted(Comparator.comparing(MessageEntity::getGmtCreate))
                .map(message -> ChatMessageVO.builder()
                .id(message.getId())
                .isUser(message.getFromUser())
                .message(message.getContent())
                .type(message.getType())
                .build()).collect(Collectors.toList());
        int start = 0;
        if (lastId != null) {
            for (int i = 0; i < vos.size(); i++) {
                if (vos.get(i).getId().equals(lastId)) {
                    start = i + 1;
                }
            }
        }
        for (int i = start; i < messageList.size(); i++) {
            if (messageList.get(i).getState() == MessageEntity.STATE.WAIT) {
                start = i;
                break;
            }
        }
        return vos.subList(start,vos.size());
    }


    @GetMapping("/user/waiting")
    public Pager<ChatUserVO> getWaitingUsers (@RequestParam Integer curPage,@RequestParam(required = false,defaultValue = "10") Integer pageSize) {
        Pager<ChatUserVO> pager = new Pager<>();
        pager.setTotal(messageService.getWaitingCount());
        pager.setData(messageService.getWaitingUsers(curPage,pageSize, 10).stream().map(this::reflectUser).collect(Collectors.toList()));
        return pager;
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
        messageService.disConnectUser(openId,ShiroUtils.getUserInfo().getUserId());
    }

    @PutMapping("/list/read")
    public void readMessage (@RequestParam List<Long> messageIds,@RequestParam String userId) {
        messageService.readMessage(messageIds,userId);
    }

    /**
     * 发送消息给用户
     * @param messageSendDto 消息
     * @return 消息的id
     */
    @PostMapping
    public Long sendMessage (@RequestBody MessageSendDto messageSendDto) {
        log.info("send message...");
        return messageService.sendMessage( MessageEntity.builder()
                .userId(messageSendDto.getUserId())
                .content(messageSendDto.getContent())
                .type(messageSendDto.getType())
                .agentId(ShiroUtils.getUserInfo().getUserId().toString())
                .state(MessageEntity.STATE.WAIT)
                .fromUser(false)
                .gmtCreate(LocalDateTime.now())
                .build());
    }
}
