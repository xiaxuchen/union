package com.originit.union.api.quartz;

import com.originit.union.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * 因为有的时候上传文件之后，并不一定会使用该文件，当数据库中文件表中记录的文件超过一定时间引用数为0，则删除
 */
@Component
@Slf4j
public class ClearFileTimer extends QuartzJobBean {

    private FileService fileService;

    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) {
        log.info("清除超时的文件中...");
        fileService.clearTimeoutFile();
    }
}
