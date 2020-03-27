package com.originit.union.api.config;

import com.originit.union.api.protocol.UnionPrincipal;
import com.originit.union.api.util.ShiroUtils;
import com.originit.union.entity.SysUserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat")
        .setAllowedOrigins("*")
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                        log.info("执行校验");
                        final String token = ((ServletServerHttpRequest) request).getServletRequest()
                                .getParameter("token");
                        if (token == null) {
                            return false;
                        }
                        if (ShiroUtils.getUserInfo(token) == null) {
                            return false;
                        }
                        return true;
                    }

                    @Override
                    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

                    }
                })
                .setHandshakeHandler(new DefaultHandshakeHandler(){
                    @Override
                    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                        log.info("执行用户认证");
                        final String token = ((ServletServerHttpRequest) request).getServletRequest()
                                .getParameter("token");
                        if (token != null) {
                            final SysUserEntity userInfo = ShiroUtils.getUserInfo(token);
                            UnionPrincipal userPrincipal=new UnionPrincipal
                                    (userInfo,userInfo.getUserId().toString());
                            return userPrincipal;
                        }
                        return null;
                    }
                })
        .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 消息的前缀为/message
        registry.setApplicationDestinationPrefixes("/message");
        // 用户的前缀为/user
        registry.setUserDestinationPrefix("/user");
        // 开启客户经理、聊天记录的消息代理
        registry.enableSimpleBroker("/chatUser","/receivableUser","/waitUser","/user");
    }

}