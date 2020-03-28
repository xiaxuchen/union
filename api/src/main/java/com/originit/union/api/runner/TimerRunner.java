package com.originit.union.api.runner;

import com.originit.union.api.quartz.ClearFileTimer;
import com.originit.union.api.quartz.ClearWaitTimeoutTimer;
import com.originit.union.api.quartz.UserImportTimer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

/**
 * 导入用户的执行器，应用启动后自动启动定时导入任务
 * @author xxc、
 */
@Component
@Slf4j
@ConfigurationProperties(prefix = "system.chat")
@Data
public class TimerRunner implements ApplicationRunner {

    private Integer waitTimeOut;

    private Scheduler scheduler;

    @Autowired
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
    @Override
    public void run(ApplicationArguments args) throws Exception {
       startImportTimer();
       startClearWaitTimeoutTimer();
       startClearFileTimer();
    }

    private void startClearWaitTimeoutTimer () throws SchedulerException {
        log.info("start clear user task");
        //任务名称
        String name = UUID.randomUUID().toString();
        //任务所属分组
        String group = ClearWaitTimeoutTimer.class.getName();
        //创建任务
        JobDetail jobDetail = JobBuilder.newJob(ClearWaitTimeoutTimer.class).withIdentity(name,group).build();
        //创建任务触发器,指定每天凌晨一点执行任务
        //创建触发器 每隔一段时间执行一次
        Date start = new Date(System.currentTimeMillis() + waitTimeOut * 1000);
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(name,group)
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds((int)waitTimeOut).repeatForever()).build();
        //将触发器与任务绑定到调度器内
        scheduler.scheduleJob(jobDetail, trigger);
    }

    private void startImportTimer() throws SchedulerException {
        log.info("start import user task");
        //任务名称
        String name = UUID.randomUUID().toString();
        //任务所属分组
        String group = UserImportTimer.class.getName();
        //创建任务
        JobDetail jobDetail = JobBuilder.newJob(UserImportTimer.class).withIdentity(name,group).build();
        //创建任务触发器,指定每天凌晨一点执行任务
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(name,group)
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 1 ? * *")).build();
        //将触发器与任务绑定到调度器内
        scheduler.scheduleJob(jobDetail, trigger);
    }

    private void startClearFileTimer () throws SchedulerException {
        log.info("start clear file task");
        //任务名称
        String name = UUID.randomUUID().toString();
        //任务所属分组
        String group = ClearFileTimer.class.getName();
        //创建任务
        JobDetail jobDetail = JobBuilder.newJob(ClearFileTimer.class).withIdentity(name,group).build();
        //创建任务触发器,指定每天凌晨两点执行任务
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(name,group)
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 2 ? * *")).build();
        //将触发器与任务绑定到调度器内
        scheduler.scheduleJob(jobDetail, trigger);
    }
}
