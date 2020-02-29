package com.originit.union.api.runner;

import com.originit.union.api.quartz.UserImportTimer;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 导入用户的执行器，应用启动后自动启动定时导入任务
 * @author xxc、
 */
@Component
@Slf4j
public class UserImportRunner implements ApplicationRunner {

    private Scheduler scheduler;

    @Autowired
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("start import user task");
        //任务名称
        String name = UUID.randomUUID().toString();
        //任务所属分组
        String group = UserImportTimer.class.getName();
        //创建任务
        JobDetail jobDetail = JobBuilder.newJob(UserImportTimer.class).withIdentity(name,group).build();
        //创建任务触发器,指定每天凌晨一点执行任务
        //创建触发器 每3秒钟执行一次
//        Date start = new Date(System.currentTimeMillis()+3000);
//        Trigger trigger = TriggerBuilder.newTrigger()
//                .withIdentity("trigger1", "group3")
//                .startAt(start)
//                .withSchedule(SimpleScheduleBuilder.simpleSchedule()).build();
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trigger2","group2")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 1 ? * *")).build();
        //将触发器与任务绑定到调度器内
        scheduler.scheduleJob(jobDetail, trigger);
    }
}
