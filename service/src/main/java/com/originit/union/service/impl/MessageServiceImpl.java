package com.originit.union.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.originit.common.page.Pager;
import com.originit.common.util.SpringUtil;
import com.originit.union.annotation.LockKey;
import com.originit.union.chat.data.ChatUser;
import com.originit.union.chat.manager.MessageManager;
import com.originit.union.chat.manager.SessionManager;
import com.originit.union.chat.manager.UserManager;
import com.originit.union.constant.ChatConstant;
import com.originit.union.entity.MessageEntity;
import com.originit.union.entity.UserBindEntity;
import com.originit.union.entity.vo.ChatMessageVO;
import com.originit.union.entity.vo.ChatUserVO;
import com.originit.union.mapper.MessageDao;
import com.originit.union.service.MessageService;
import com.originit.union.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author xxc、
 */
@Service
@LockKey(ChatConstant.USER_LOCK)
public class MessageServiceImpl extends ServiceImpl<MessageDao,MessageEntity>  implements MessageService {

    private MessageManager messageManager;

    private SessionManager sessionManager;

    private UserManager userManager;

    @Autowired
    public void setMessageManager(MessageManager messageManager) {
        this.messageManager = messageManager;
    }

    @Autowired
    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Autowired
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    @Override
    public Long sendMessage(MessageEntity messageEntity) {
        if (messageEntity.getFromUser()) {
            messageManager.sendMessageForServe(messageEntity.getUserId(),messageEntity);
        } else {
            messageManager.sendMessageToUser(messageEntity.getUserId(),
                    Long.valueOf(messageEntity.getAgentId()),messageEntity);
        }
        return messageEntity.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public Pager<ChatUserVO> getWaitingUsers(int curPage, int pageSize, int messageCount) {
        // 获取到等待用户的列表
        Pager<ChatUser> waiterPager = userManager.getWaitingUsers(curPage, pageSize);
        // 获取每个用户最后的一条未读信息
        List<ChatUserVO> vos = waiterPager.getData().stream().map(chatUser -> {
            String openId = chatUser.getUserInfo().getOpenId();
            IPage<MessageEntity> iPage = getLastWaitMessage(openId);
            ChatUserVO vo = to(chatUser);
            if (!iPage.getRecords().isEmpty()) {
                MessageEntity message = iPage.getRecords().get(0);
                vo.setLastMessage(to(message));
                vo.setTime(DateUtil.timeStampToStr(message.getGmtCreate()
                        .toEpochSecond(ZoneOffset.UTC)));
                vo.setNotRead((int) iPage.getTotal());
            }
            return vo;
        }).collect(Collectors.toList());
        // 转换成pager
        return new Pager<>(vos,waiterPager.getTotal());
    }

    @Override
    public void receiveUser(String openId, Long id) {
        sessionManager.connect(openId,id);
    }

    @Override
    public void disConnectUser(String openId, Long id) {
        sessionManager.disconnect(openId,id);
    }

    @Override
    @LockKey(ChatConstant.USER_LOCK)
    public void dispatchToOther(String openId, Long from, Long to) {
        sessionManager.disconnect(openId,from);
        sessionManager.connect(openId,to);
    }

    @Override
    public ChatUser getUser(String openId, int messageCount) {
        return userManager.getUser(openId);
    }

    @Override
    public Integer getUserStatus(String openId) {
        return userManager.getUserState(openId);
    }


    @Override
    public void readMessage(List<Long> messageIds,Long agentId) {
        messageManager.messageRead(messageIds,agentId);
    }

    @Override
    public List<MessageEntity> getHistoryMessages(String userId, Long agentId, Long messageId) {
        return messageManager.getHistoryMessages(userId,agentId,messageId);
    }

    @Override
    public List<MessageEntity> getWaitMessages(String userId, Long agentId, Long messageId) {
        return messageManager.getWaitMessages(userId,agentId,messageId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ChatUserVO> getAgentUserVOs(Long agentId) {
        final List<ChatUserVO> vos = new ArrayList<>();
        sessionManager.getUserIds (agentId).forEach(id -> {
            IPage<MessageEntity> iPage = getLastWaitMessage(id);
            ChatUser user = userManager.getUser(id);
            ChatUserVO vo = to(user);
            // 有未读记录
            if (!iPage.getRecords().isEmpty()) {
                MessageEntity message = iPage.getRecords().get(0);
                vo.setLastMessage(to(message));
                vo.setTime(DateUtil.timeStampToStr(message.getGmtCreate()
                        .toEpochSecond(ZoneOffset.UTC)));
                vo.setNotRead((int)iPage.getTotal());
            }
            vo.setAgentId(agentId);
            vos.add(vo);
        });
        return vos;
    }

    /**
     * 获取最后一条未读的消息
     * @param userId 用户id
     */
    public IPage<MessageEntity> getLastWaitMessage (String userId) {
        // 查询该用户经理和用户对话中最近的一条未读记录
        final IPage<MessageEntity> iPage = baseMapper.selectPage(new Page<>(0, 1),
                new QueryWrapper<MessageEntity>().lambda()
                        .eq(MessageEntity::getState, MessageEntity.STATE.WAIT)
                        .eq(MessageEntity::getUserId, userId)
                        .orderByDesc(MessageEntity::getGmtCreate));
        return iPage;
    }

    private ChatUserVO to(ChatUser user) {
        UserBindEntity info = user.getUserInfo();
        return ChatUserVO.builder()
                .headImg(info.getHeadImg())
                .id(info.getOpenId())
                .phone(info.getPhone())
                .name(info.getName())
                .build();
    }

    private ChatMessageVO to (MessageEntity message) {
        return ChatMessageVO.builder()
                .isUser(message.getFromUser())
                .message(message.getContent())
                .type(message.getType())
                .userId(message.getUserId())
                .type(message.getType())
                .id(message.getId())
                .time(DateUtil.timeStampToStr(message.getGmtCreate()
                        .toEpochSecond(ZoneOffset.UTC)))
                .build();
    }

    private MessageService getAop () {
        return SpringUtil.getBean(MessageService.class);
    }
}
