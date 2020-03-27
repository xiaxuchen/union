package com.originit.union.api.quartz;

import com.originit.union.constant.SystemConstant;
import com.originit.union.service.ChatService;
import com.originit.union.service.RedisService;
import com.originit.union.service.WeChatUserService;
import com.originit.union.util.DateUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
@ConfigurationProperties(prefix = "system.chat")
@Data
public class ClearWaitTimeoutTimer extends QuartzJobBean {

    private Long waitTimeOut;

    private ChatService chatService;

    @Autowired
    public void setChatService(ChatService chatService) {
        this.chatService = chatService;
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        log.info("清除超时等待用户中...");
        chatService.clearWaitTimeout(waitTimeOut);
    }
}
