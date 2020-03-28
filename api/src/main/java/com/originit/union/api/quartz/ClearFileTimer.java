package com.originit.union.api.quartz;

import com.originit.union.service.ChatService;
import com.originit.union.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

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
