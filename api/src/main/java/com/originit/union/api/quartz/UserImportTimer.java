package com.originit.union.api.quartz;

import com.originit.union.bussiness.UserBusiness;
import com.originit.union.service.UserService;
import com.originit.union.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

@Component
@Slf4j
public class UserImportTimer extends QuartzJobBean {

    private UserService userService;

    private UserBusiness business;

    private ThreadPoolExecutor executor;

    @Autowired
    public void setExecutor(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    @Autowired
    public void setBusiness(UserBusiness business) {
        this.business = business;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("importing users start:" + DateUtil.timeStampToStr(System.currentTimeMillis()/1000));
        business.batchGetAllUser(users -> {
            userService.addOrUpdateUsers(users);
        });
    }
}
