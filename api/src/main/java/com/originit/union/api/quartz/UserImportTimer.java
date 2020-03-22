package com.originit.union.api.quartz;

import com.originit.union.constant.SystemConstant;
import com.originit.union.service.RedisService;
import com.originit.union.service.WeChatUserService;
import com.originit.union.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class UserImportTimer extends QuartzJobBean {

    private WeChatUserService userService;

    private RedisService redisService;

    @Autowired
    public void setUserService(WeChatUserService userService) {
        this.userService = userService;
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        log.info("importing users start:" + DateUtil.timeStampToStr(System.currentTimeMillis()/1000));
        final Integer[] preUserStatistic = userService.getUserStatistic();
        userService.importUsers();
        final Integer[] afterUserStatistic = userService.getUserStatistic();
        // 更新统计数据
        redisService.set(SystemConstant.ALL_USER_COUNT,afterUserStatistic[0]);
        redisService.set(SystemConstant.USER_BIND_COUNT,afterUserStatistic[1]);
        final int yesterdayAddition = afterUserStatistic[1] - preUserStatistic[1];
        redisService.set(SystemConstant.USER_YESTERDAY_ADDITION,yesterdayAddition);

        //更新当月的增量
        Integer nowCount = redisService.get(SystemConstant.USER_MONTH_ADDITION,Integer.class);
        if (nowCount == null || LocalDateTime.now().getDayOfMonth() == 1)
        {
            nowCount = yesterdayAddition;
        } else {
            nowCount += yesterdayAddition;
        }
        redisService.set(SystemConstant.USER_MONTH_ADDITION,nowCount);
    }
}
