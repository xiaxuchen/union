package com.originit.union.chat;

import com.originit.common.util.FileUDUtil;
import com.originit.union.bussiness.ClientServeBusiness;
import com.originit.union.bussiness.MaterialBusiness;
import com.originit.union.chat.data.AgentState;
import com.originit.union.chat.data.ChatUser;
import com.originit.union.entity.AgentInfoEntity;
import com.originit.union.service.AgentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * 管理用户与经理之间的会话的接入、断开转接等
 * @author xxc、
 */
@Component
@Primary
public class ChatSessionManager {

    private ChatUserManager chatUserManager;

    private ReentrantLock lock = new ReentrantLock();

    private ClientServeBusiness clientServeBusiness;

    private MaterialBusiness materialBusiness;

    private AgentInfoService agentInfoService;

    @Autowired
    public void setMaterialBusiness(MaterialBusiness materialBusiness) {
        this.materialBusiness = materialBusiness;
    }

    /**
     * 客户经理对应的用户
     */
    private ConcurrentSkipListMap<Long, List<String>> sessions;

    private ConcurrentSkipListMap<Long, AgentState> agentMap;

    @Autowired
    public void setAgentInfoService(AgentInfoService agentInfoService) {
        this.agentInfoService = agentInfoService;
    }

    @Autowired
    public void setClientServeBusiness(ClientServeBusiness clientServeBusiness) {
        this.clientServeBusiness = clientServeBusiness;
    }

    @Autowired
    public void setChatUserManager(ChatUserManager chatUserManager) {
        this.chatUserManager = chatUserManager;
    }

    public ChatSessionManager() {
        sessions = new ConcurrentSkipListMap<>();
        this.agentMap = new ConcurrentSkipListMap<>();
    }

    /**
     * 获取用户信息以及消息列表
     * @param messageCount 每个用户获取的消息条数
     * @param count 用户的数量
     * @return 用户信息以及消息列表
     */
    public List<ChatUser> getUserList (Long id,int count, int messageCount) {
        lock.lock();
        try {
            List<String> users = sessions.get(id);
            if (users == null || users.isEmpty() || count == 0) {
                return Collections.emptyList();
            }
            // 截取count个
            users = users.subList(ChatUtil.getFromSize(count,users.size()),users.size());
            return users.stream().map(s -> chatUserManager.getUser(s,messageCount)).collect(Collectors.toList());
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取经理信息
     * @param agentId 用户的openId
     * @return 客户经理的状态管理
     */
    private AgentState getAgentState (Long agentId) {
        AgentState agentState = agentMap.get(agentId);
        if (agentState == null) {
            agentState = agentInfoService.getAgentStateById(agentId);
            agentMap.put(agentId,agentState);
        }
        return agentState;
    }

    /**
     * 接受一个用户
     * @param openId 用户的openId
     * @param id 经理的id
     */
    public void receiveUser (String openId,Long id) {
        lock.lock();
        try {
            chatUserManager.changeStatus(openId, ChatUser.STATE.RECEIVED, id);
            List<String> users = sessions.get(id);
            if (users == null) {
                users = new ArrayList<>();
            }
            users.add(openId);
            sessions.put(id,users);
            // 接入完成用户通知
            clientServeBusiness.sendTextMessage(openId, "客服经理接入成功");
            AgentState agentState = getAgentState(id);
            clientServeBusiness.sendAgentIntroduce(openId,agentState.getInfo().getName(),
                    agentState.getInfo().getDes(),getHeadImgUrl(agentState.getHeadImg()));
        } finally {
            lock.unlock();
        }
    }

    /**
     * TODO 修改成请求这个服务器
     * 获取头像的url
     * @param code 头像code
     * @return url
     */
    private String getHeadImgUrl (String code) {
        File file = FileUDUtil.getFile(code);
        if (file == null) {
            file = FileUDUtil.getDefaultHeadImg();
        }
        return materialBusiness.uploadTempMaterial(file).getUrl();
    }

    /**
     * 关闭一个用户的连接
     * @param openId 用户的openId
     * @param id 经理的id
     */
    public void disConnectUser (String openId,Long id) {
        lock.lock();
        try {
            chatUserManager.changeStatus(openId, ChatUser.STATE.NEVER, id);
            List<String> users = sessions.get(id);
            // 从中删除指定用户
            if (users != null) {
                users.removeIf(s -> s.equals(openId));
            }
            clientServeBusiness.sendTextMessage(openId, "服务已断开，谢谢您的使用");
        } finally {
            lock.unlock();
        }
    }

    /**
     * 从一个客户经理转接到另一个客户经理
     * @param openId 用户的id
     * @param from 当前客户经理
     * @param to 转接的客户经理
     */
    public void dispatchToOther (String openId,Long from,Long to) {
        lock.lock();
        try {
            disConnectUser(openId,from);
            receiveUser(openId,to);
        } finally {
            lock.unlock();
        }
    }
}
