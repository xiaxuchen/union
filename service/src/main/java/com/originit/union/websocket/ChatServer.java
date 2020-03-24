package com.originit.union.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.originit.common.page.Pager;
import com.originit.common.util.ExceptionUtil;
import com.originit.common.util.SpringUtil;
import com.originit.union.entity.vo.ChatMessageVO;
import com.originit.union.entity.vo.ChatUserVO;
import com.originit.union.service.ChatService;
import com.originit.union.util.DataUtil;
import com.soecode.wxtools.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

/**
 * @author xxc、
 */
@ServerEndpoint("/websocket/message/{userId}")
@Component
@Slf4j
public class ChatServer {

    /**静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。*/
    private volatile static AtomicInteger onlineCount = new AtomicInteger(0);
    /**concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。*/
    private static ConcurrentHashMap<Long,ChatServer> webSocketMap = new ConcurrentHashMap<>();
    /**与某个客户端的连接会话，需要通过它来给客户端发送数据*/
    private Session session;
    /**接收userId*/
    private Long userId = null;

    private static ChatService chatService;

    private static ExecutorService asyncServiceExecutor;

    /**
     * 发送当前等待的用户列表
     */
    public static final String WAITING_USER = "waiting-user";

    /**
     * 发送经理的聊天用户列表
     */
    public static final String AGENT_USER = "agent-user";

    /**
     * 推送等待的用户已接受
     */
    public static final String RECEIVED_USER = "received-user";

    /**
     * 发送新的消息
     */
    public static final String SEND_NEW_MESSAGE = "send-new-message";

    /**
     * 更新等待的人数
     */
    public static final String UPDATE_WAITING_COUNT = "update-wait-count";

    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session,@PathParam("userId") Long userId) {
        // 初始化聊天service
        if (chatService == null) {
            chatService = SpringUtil.getBean(ChatService.class);
        }
        if (asyncServiceExecutor == null) {
            asyncServiceExecutor = SpringUtil.getBean(ExecutorService.class);
        }
        this.session = session;
        this.userId = userId;
        if(webSocketMap.containsKey(userId)){
            webSocketMap.remove(userId);
            webSocketMap.put(userId,this);
            //加入set中
        }else{
            webSocketMap.put(userId,this);
            //加入set中
            addOnlineCount();
            //在线数加1
        }

        log.info("用户连接:"+userId+",当前在线人数为:" + getOnlineCount());
        sendNeedInfo();
    }

    /**
     * 发送聊天连接后初始需要的消息，等待用户、当前用户列表等
     */
    public void sendNeedInfo () {
        final List<ChatUserVO> agentUserVOs = chatService.getAgentUserVOs(userId);
        final Pager<ChatUserVO> waitingUsers = chatService.getWaitingUsers(0, 10);
        // 发送当前等待的用户以及该用户经理的用户列表
        sendMessage(JSON.toJSONString(WebSocketMessage.builder()
                .type(WebSocketMessage.TYPE.MESSAGE)
                .eventName(AGENT_USER)
                .content(agentUserVOs)
                .build()));
        sendMessage(JSON.toJSONString(WebSocketMessage.builder()
                .type(WebSocketMessage.TYPE.MESSAGE)
                .eventName(WAITING_USER)
                .content(waitingUsers)
                .build()));
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if(webSocketMap.containsKey(userId)){
            webSocketMap.remove(userId);
            //从set中删除
            subOnlineCount();
        }
        log.info("用户退出:"+userId+",当前在线人数为:" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("用户消息:"+userId+",报文:"+message);
        //可以群发消息
        //消息保存到数据库、redis
        if(StringUtils.isNotBlank(message)){
            try {
                //解析发送的报文
                JSONObject jsonObject = JSON.parseObject(message);
                //追加发送人(防止串改)
                jsonObject.put("fromUserId",this.userId);
                String toUserId=jsonObject.getString("toUserId");
                //传送给对应toUserId用户的websocket
                if(StringUtils.isNotBlank(toUserId)&&webSocketMap.containsKey(toUserId)){
                    webSocketMap.get(toUserId).sendMessage(jsonObject.toJSONString());
                }else{
                    log.error("请求的userId:"+toUserId+"不在该服务器上");
                    //否则不在这个服务器上，发送到mysql或者redis
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误:"+this.userId+",原因:"+error.getMessage());
        error.printStackTrace();
    }
    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("message send fail,error is {}",ExceptionUtil.buildErrorMessage(e));
        }
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(WebSocketMessage message) throws IOException {
        this.session.getBasicRemote().sendText(JSON.toJSONString(message));
    }

    /**
     * 更新等待中的用户数量
     * @param count 当前的用户数量
     */
    public static void updateWaitingUserCount(int count) {
        sendAll(WebSocketMessage.builder()
                .type(WebSocketMessage.TYPE.EVENT)
                .eventName(UPDATE_WAITING_COUNT)
                .content(DataUtil.mapBuilder()
                        .append("count",count)
                        .append("time",System.currentTimeMillis())
                        .build())
                .build());
    }

    /**
     * 通知所有人一些用户已接受
     * @param openId 已接受的用户的openId
     */
    public static void receivedUser (String openId, Integer count) {
        sendAll(WebSocketMessage.builder()
                .type(WebSocketMessage.TYPE.EVENT)
                .eventName(RECEIVED_USER)
                .content(DataUtil
                        .mapBuilder().append("openId",openId)
                        .append("count",count)
                        .append("time",System.currentTimeMillis()).build())
                .build());
    }

    /**
     * 发送新的消息给指定的客户经理
     * @param message 消息
     */
    public static void sendNewMessage (ChatMessageVO message,Long userId) {
        try {
            sendInfo(WebSocketMessage.builder()
                    .type(WebSocketMessage.TYPE.EVENT)
                    .eventName(SEND_NEW_MESSAGE)
                    .content(message)
                    .build(),userId);
        } catch (Exception e) {
            log.error("send new message error,error is {}",ExceptionUtil.buildErrorMessage(e));
        }
    }

    /**
     * 发送给所有人
     * @param message JSON字符串
     */
    public static void sendAll (String message) {
        // 通知所有的用户
        webSocketMap.forEach(new BiConsumer<Long, ChatServer>() {
            @Override
            public void accept(Long aLong, ChatServer chatServer) {
                try {
                    chatServer.sendMessage(message);
                }catch (Exception e) {
                    // 若失败则记录日志
                    log.error("send fail,error is {}",ExceptionUtil.buildErrorMessage(e));
                }
            }
        });
    }

    public static void sendAll (WebSocketMessage message) {
        // 通知所有的用户
        webSocketMap.forEach(new BiConsumer<Long, ChatServer>() {
            @Override
            public void accept(Long aLong, ChatServer chatServer) {
                try {
                    chatServer.sendMessage(JSON.toJSONString(message));
                }catch (Exception e) {
                    // 若失败则记录日志
                    log.error("send fail,error is {}",ExceptionUtil.buildErrorMessage(e));
                }
            }
        });
    }

    /**
     * 发送自定义消息
     * */
    public static void sendInfo(WebSocketMessage message, Long userId) throws IOException {
        log.info("发送消息到:"+userId+"，报文:"+message);
        if(userId != null && webSocketMap.containsKey(userId)){
            webSocketMap.get(userId).sendMessage(JSON.toJSONString(message));
        }else{
            log.error("用户"+userId+",不在线！");
        }
    }

    /**
     * 发送自定义消息
     * */
    public static void sendInfo(String message, Long userId) throws IOException {
        log.info("发送消息到:"+userId+"，报文:"+message);
        if(userId != null && webSocketMap.containsKey(userId)){
            webSocketMap.get(userId).sendMessage(message);
        }else{
            log.error("用户"+userId+",不在线！");
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount.get();
    }

    public static synchronized void addOnlineCount() {
        ChatServer.onlineCount.getAndIncrement();
    }

    public static synchronized void subOnlineCount() {
        ChatServer.onlineCount.getAndDecrement();
    }
}
