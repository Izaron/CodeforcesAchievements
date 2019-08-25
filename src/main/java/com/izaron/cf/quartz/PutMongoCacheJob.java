package com.izaron.cf.quartz;

import com.izaron.cf.service.MongoCacheService;
import lombok.extern.log4j.Log4j2;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

@Log4j2
@DisallowConcurrentExecution
public class PutMongoCacheJob implements Job {

    @Autowired
    private MongoCacheService mongoCacheService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.debug("Putting cache to the Mongo database");
        mongoCacheService.putCache();
    }
}
