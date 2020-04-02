package com.originit.union.api.quartz;

import com.originit.common.util.RedisCacheProvider;
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

    @Autowired
    private WeChatUserService userService;

    @Autowired
    private RedisCacheProvider provider;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        log.info("importing users start...");
        final Integer[] preUserStatistic = userService.getUserStatistic();
        userService.importUsers();
        final Integer[] afterUserStatistic = userService.getUserStatistic();
        // 更新统计数据
        provider.set(SystemConstant.ALL_USER_COUNT,afterUserStatistic[0]);
        provider.set(SystemConstant.USER_BIND_COUNT,afterUserStatistic[1]);
        final int yesterdayAddition = afterUserStatistic[1] - preUserStatistic[1];
        provider.set(SystemConstant.USER_YESTERDAY_ADDITION,yesterdayAddition);

        //更新当月的增量
        Integer nowCount = (Integer) provider.get(SystemConstant.USER_MONTH_ADDITION);
        if (nowCount == null || LocalDateTime.now().getDayOfMonth() == 1)
        {
            nowCount = yesterdayAddition;
        } else {
            nowCount += yesterdayAddition;
        }
        provider.set(SystemConstant.USER_MONTH_ADDITION,nowCount);
        log.info("import users end");
    }
}
