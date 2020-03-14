package com.originit.union.api.controller;

import com.originit.common.page.Pager;
import com.originit.union.api.chat.ChatDoor;
import com.originit.union.api.chat.data.ChatUser;
import com.originit.union.api.chat.data.Message;
import com.originit.union.api.util.ShiroUtils;
import com.originit.union.entity.UserBindEntity;
import com.originit.union.entity.vo.ChatMessageVO;
import com.originit.union.entity.vo.ChatUserVO;
import com.originit.union.entity.vo.RefreshChatVO;
import com.originit.union.util.DateUtil;
import com.originit.union.util.PagerUtil;
import com.xxc.response.anotation.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
public class MessageController {


    @Autowired
    private ChatDoor chatDoor;

    /**
     * 获取最新的服务器端的聊天信息
     */
//    @GetMapping("/refreshServe")
//    public RefreshChatVO refreshServe () {
//        Long userId = ShiroUtils.getUserInfo().getUserId();
//        List<ChatUser> userList = chatDoor.getUserList(userId, -1);
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

    @GetMapping("/chatUserList")
    public List<ChatUserVO> getUserList () {
        Long userId = ShiroUtils.getUserInfo().getUserId();
        List<ChatUser> userList = chatDoor.getUserList(userId, 0);
        return userList.stream().map(this::reflectUser).collect(Collectors.toList());
    }

    private ChatUserVO reflectUser (ChatUser chatUser) {
        UserBindEntity userInfo = chatUser.getUserInfo();
        List<Message> messageList = chatUser.getMessageList();
        Long notRead = 0L;
        // 这里先暂时只支持文本消息
        ChatMessageVO lastMessage = null;
        String time = "";
        if (messageList != null || !messageList.isEmpty()) {
            notRead = messageList.stream().filter(message -> message.getStatus().equals(Message.STATUS.WAIT)).count();
            Message message = messageList.get(messageList.size() - 1);
            lastMessage = ChatMessageVO.builder()
                    .isUser(message.getFromUser())
                    .message(message.getContent().toString())
                    .type(message.getType())
                    .build();
            time = DateUtil.getTime(message.getSendTime().toEpochSecond(ZoneOffset.of("+8")));
        }
        return ChatUserVO.builder()
                .id(userInfo.getOpenId())
                .name(userInfo.getName())
                .phone(userInfo.getPhone())
                .headImg(userInfo.getHeadImg())
                .notRead(notRead.intValue())
                // TODO 这里的消息需要改一下，如果需要支持其他类型的消息的话
                .lastMessage(lastMessage)
                .time(time)
                .build();
    }

    @GetMapping("/chatMessageList")
    public List<ChatMessageVO> getMessages(@RequestParam String userId,@RequestParam int count) {
        return chatDoor.getUser(userId,-1).getMessageList().stream().map(message -> ChatMessageVO.builder()
                .isUser(message.getFromUser())
                .message(message.getContent().toString())
                .type(message.getType())
                .build()).collect(Collectors.toList());
    }


    @GetMapping("/user/waiting")
    public Pager<ChatUserVO> getWaitingUsers (@RequestParam Integer curPage,@RequestParam(required = false,defaultValue = "10") Integer pageSize) {
        Pager<ChatUserVO> pager = new Pager<>();
        pager.setTotal((long) chatDoor.getWaitingCount());
        pager.setData(chatDoor.getWaitingUsers(curPage,pageSize, 10).stream().map(this::reflectUser).collect(Collectors.toList()));
        return pager;
    }

    /**
     * 接入该用户
     * @param openId 用户id
     */
    @PostMapping("/session")
    public void receiveUser (@RequestBody List<String> userList) {
        for (String user : userList) {
            chatDoor.receiveUser(user,ShiroUtils.getUserInfo().getUserId());
        }
    }

    /**
     * 关闭某用户的会话
     * @param openId 用户的id
     */
    @DeleteMapping("/session")
    public void disConnectUser (@RequestParam String openId) {
        chatDoor.disConnectUser(openId,ShiroUtils.getUserInfo().getUserId());
    }

    public void forwardOtherAgent () {

    }
}
