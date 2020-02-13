package com.originit.union.api.controller;

import com.originit.union.api.quartz.GoodStockCheckTimer;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.quartz.*;
import org.quartz.core.QuartzScheduler;
import org.quartz.impl.StdScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

/**
 * 测试定时任务Controller
 */
@RestController
public class TestController {

    @Autowired
    Scheduler scheduler;

    final Logger logger = LoggerFactory.getLogger(TestController.class);

    @RequestMapping("/test")
    public void test () throws Exception {
        buildCreateGoodTimer();
    }

    public void buildCreateGoodTimer() throws Exception
    {
        //设置开始时间为1分钟后
        long startAtTime = System.currentTimeMillis() + 1000 * 10;
        //任务名称
        String name = UUID.randomUUID().toString();
        //任务所属分组
        String group = GoodStockCheckTimer.class.getName();
        //创建任务
        JobDetail jobDetail = JobBuilder.newJob(GoodStockCheckTimer.class).withIdentity(name,group).build();
        //创建任务触发器
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(name,group).withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(3).repeatForever()).startAt(new Date(startAtTime)).build();
        //将触发器与任务绑定到调度器内
        scheduler.scheduleJob(jobDetail, trigger);
    }

}
