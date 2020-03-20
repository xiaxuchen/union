package com.originit.union.chat.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.originit.common.exceptions.DataConflictException;
import com.originit.common.exceptions.DataNotFoundException;
import com.originit.common.page.Pager;
import com.originit.common.util.RedisCacheProvider;
import com.originit.union.annotation.LockKey;
import com.originit.union.bussiness.ClientServeBusiness;
import com.originit.union.chat.ChatUtil;
import com.originit.union.chat.data.ChatUser;
import com.originit.union.chat.manager.SessionManager;
import com.originit.union.chat.manager.UserManager;
import com.originit.union.chat.manager.function.AgentStateSetter;
import com.originit.union.constant.ChatConstant;
import com.originit.union.entity.AgentInfoEntity;
import com.originit.union.entity.UserBindEntity;
import com.originit.union.mapper.AgentInfoDao;
import com.originit.union.mapper.UserDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xxc、
 */
@Component
@Primary
@Slf4j
public class UserSessionManagerImpl implements UserManager, SessionManager {

    private RedisCacheProvider provider;

    private UserDao userDao;

    private ClientServeBusiness clientServeBusiness;

    private AgentInfoDao agentInfoDao;

    @Autowired
    public void setAgentInfoDao(AgentInfoDao agentInfoDao) {
        this.agentInfoDao = agentInfoDao;
    }

    @Autowired
    public void setClientServeBusiness(ClientServeBusiness clientServeBusiness) {
        this.clientServeBusiness = clientServeBusiness;
    }

    @Autowired
    public void setProvider(RedisCacheProvider provider) {
        this.provider = provider;
    }

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    @LockKey(ChatConstant.USER_LOCK)
    public boolean changeState(String userId, Integer state, Long agentId) {
        Integer userState = getUserState(userId);
        // 如果状态相同就返回false,表示没有修改
        if (userState.equals(state)) {
            return false;
        }
        // 状态不同就具体处理
        switch (userState) {
            case ChatUser.STATE.NEVER: {
                UserBindEntity userBindEntity = selectUserByOpenId(userId);
                ChatUser chatUser = ChatUser.builder()
                        .userInfo(userBindEntity).build();
                if (state == ChatUser.STATE.WAIT) {
                    // 将用户信息插入等待列表，获取到索引
                    Long index = provider.listPush(ChatConstant.USER_WAIT_LIST_KEY,chatUser);
                    // 将索引放入map中，方便通过id查找
                    provider.hset(ChatConstant.USER_WAIT_MAP_KEY,userId,index);
                } else  if (state == ChatUser.STATE.RECEIVED) {
                    // 将响应的用户放入map中，方便通过id查找
                    provider.hset(ChatConstant.USER_WAIT_MAP_KEY,userId,chatUser);
                }
                break;
            }
            case ChatUser.STATE.RECEIVED: {
                ChatUser chatUser = null;
                switch (state) {
                    case ChatUser.STATE.WAIT: {
                        // 获取原来的用户
                        chatUser = (ChatUser) provider.hget(ChatConstant.USER_RESPOND_MAP_KEY, userId);
                        // 将用户信息插入等待列表，获取到索引
                        Long index = provider.listPush(ChatConstant.USER_WAIT_LIST_KEY,chatUser);
                        // 清除客户经理
                        chatUser.setReceiveAgent(null);
                        // 将索引放入map中，方便通过id查找
                        provider.hset(ChatConstant.USER_WAIT_MAP_KEY,userId,index);
                    }
                    case ChatUser.STATE.NEVER: {
                        if (chatUser == null) {
                            chatUser = (ChatUser) provider.hget(ChatConstant.USER_RESPOND_MAP_KEY, userId);
                        }
                        if (chatUser != null) {
                            // 将响应的用户从列表中移除
                            provider.hdel(ChatConstant.USER_RESPOND_MAP_KEY,userId);
                            // 将用户和经理断开连接
                            removeSession(userId,agentId);
                            clientServeBusiness.sendExitMessage(userId);
                        }
                        break;
                    }
                }
                break;
            }
            case ChatUser.STATE.WAIT: {
                Long index = null;
                switch (state) {
                    case ChatUser.STATE.RECEIVED: {
                        // 获取原来的用户的索引
                        index = (Long) provider.hget(ChatConstant.USER_WAIT_MAP_KEY, userId);
                        // 通过索引获取原用户信息
                        ChatUser chatUser = (ChatUser) provider.listGet(ChatConstant.USER_WAIT_LIST_KEY,index);
                        // 设置经理id
                        chatUser.setReceiveAgent(agentId);
                        // 将索引放入接收的map中
                        provider.hset(ChatConstant.USER_RESPOND_MAP_KEY,userId,chatUser);
                        sendAgentIntroduce(userId,agentId);
                    }
                    case ChatUser.STATE.NEVER: {
                        // 如果没有就获取到索引
                        if (index == null) {
                            index = (Long) provider.hget(ChatConstant.USER_WAIT_MAP_KEY, userId);
                        }
                        // 将响应的用户从列表中移除
                        provider.hdel(ChatConstant.USER_WAIT_MAP_KEY,userId);
                        provider.listDel(ChatConstant.USER_WAIT_LIST_KEY,index);
                        break;
                    }
                }
                break;
            }
        }
        return true;
    }

    /**
     * 发送经理的介绍
     * @param userId 用户id
     * @param agentId 经理id
     */
    private void sendAgentIntroduce (String userId,Long agentId) {
        AgentInfoEntity agentInfoEntity = agentInfoDao.selectOne(new QueryWrapper<AgentInfoEntity>().lambda()
                .select(AgentInfoEntity::getDes, AgentInfoEntity::getName)
                .eq(AgentInfoEntity::getSysUserId, agentId));
        clientServeBusiness.sendAgentIntroduce(userId,agentInfoEntity.getName(),agentInfoEntity.getDes(),"");
        log.info("send agent introduce to connect userId:{} and agentId:{}",userId,agentId);
    }

    @Override
    @LockKey(ChatConstant.USER_LOCK)
    public Integer getUserState(String userId) {
        if (provider.hHasKey(ChatConstant.USER_WAIT_MAP_KEY,userId)) {
            return ChatUser.STATE.WAIT;
        }
        if (provider.hHasKey(ChatConstant.USER_RESPOND_MAP_KEY,userId)){
            return ChatUser.STATE.RECEIVED;
        } else {
            return ChatUser.STATE.NEVER;
        }
    }

    @Override
    public ChatUser getUser(String userId) {
       return getUser(userId,null);
    }

    @Override
    @LockKey(ChatConstant.USER_LOCK)
    public List<ChatUser> getChattingUsers(Long agentId, int count) {
        String key = ChatConstant.SESSION_LIST_KEY_PREFIX + agentId;
        long size = provider.listSize(key);
        if (size == 0) {
            return Collections.emptyList();
        }
        return provider.listSubList(key,ChatUtil.getFromSize(count,size),size).stream().map(o -> getUser(o.toString())).collect(Collectors.toList());
    }

    @Override
    public Pager<ChatUser> getWaitingUsers(int curPage, int pageSize) {
        long size = provider.listSize(ChatConstant.USER_WAIT_LIST_KEY);
        int start,end;
        start = (curPage - 1) * pageSize;
        end = start + pageSize;
        if (end > size) {
            end = (int) size;
        }
        log.info("get wait users [{}:{}]",start,end);
        return new Pager<>(provider.listSubList(ChatConstant.USER_WAIT_LIST_KEY,start,end)
                .stream().map(o -> (ChatUser)o).collect(Collectors.toList()),size);
    }

    @LockKey(ChatConstant.USER_LOCK)
    public ChatUser getUser(String userId,Integer state) {
        ChatUser chatUser = null;
        if (state == null) {
            chatUser = (ChatUser) provider.hget(ChatConstant.USER_WAIT_MAP_KEY,userId);
            if (chatUser == null){
                chatUser = (ChatUser) provider.hget(ChatConstant.USER_RESPOND_MAP_KEY,userId);
            }
            if (chatUser == null) {
                chatUser = ChatUser.builder()
                        .userInfo(selectUserByOpenId(userId))
                        .build();
            }
            return chatUser;
        }
        switch (state) {
            case ChatUser.STATE.RECEIVED: {
                chatUser = (ChatUser) provider.hget(ChatConstant.USER_WAIT_MAP_KEY,userId);
                break;
            }
            case ChatUser.STATE.WAIT: {
                chatUser = (ChatUser) provider.hget(ChatConstant.USER_RESPOND_MAP_KEY,userId);
                break;
            }
        }
        return chatUser;
    }

    /**
     * 是否改用户处于该状态
     * @param userId 用户id
     * @param state 用户状态
     * @return 是否存在
     */
    @LockKey(ChatConstant.USER_LOCK)
    public boolean existUser (String userId,Integer state) {
        switch (state) {
            case ChatUser.STATE.RECEIVED: {
                return provider.hHasKey(ChatConstant.USER_RESPOND_MAP_KEY,userId);
            }
            case ChatUser.STATE.WAIT: {
                return provider.hHasKey(ChatConstant.USER_WAIT_MAP_KEY,userId);
            }
        }
        return false;
    }

    @Override
    @LockKey(ChatConstant.USER_LOCK)
    public void disconnect(String userId, Long agentId) throws DataConflictException {
        changeState(userId,ChatUser.STATE.NEVER,agentId);
    }

    /**
     * 从session中将用户移除
     * @param userId
     * @param agentId
     */
    public void removeSession (String userId,Long agentId) {
        String key = ChatConstant.SESSION_LIST_KEY_PREFIX + agentId;
        // 删除经理列表中的指定用户，如果删除成功(代表原本有)了才能进行下一步
        if (!provider.listRemove(key, userId)) {
            // 如果没有删除成功，就是没接入
            throw new DataConflictException("该用户未接入");
        }
    }

    @Override
    @LockKey(ChatConstant.USER_LOCK)
    public void connect(String userId, Long agentId) {
        String key = ChatConstant.SESSION_LIST_KEY_PREFIX + agentId;
        // 如果该用户还在等待
        if (existUser(userId,ChatUser.STATE.WAIT)) {
            provider.listPush(key,userId);
            changeState(userId,ChatUser.STATE.RECEIVED,agentId);
            return;
        }
        throw new DataConflictException("当前用户已被其他人接入");
    }

    @Override
    @LockKey(ChatConstant.USER_LOCK)
    public void updateAgent(Long agentId, AgentStateSetter setter) throws DataNotFoundException {

    }

    @Override
    public void run() {

    }

    @Override
    @LockKey(ChatConstant.USER_LOCK)
    public List<String> getUserIds(Long agentId) {
        String key = ChatConstant.SESSION_LIST_KEY_PREFIX + agentId;
        return provider.listAll(key).stream().map(Object::toString).collect(Collectors.toList());
    }

    private UserBindEntity selectUserByOpenId (String openId) {
        return userDao.selectOne(new
                QueryWrapper<UserBindEntity>().lambda()
                .eq(UserBindEntity::getOpenId, openId));
    }
}
