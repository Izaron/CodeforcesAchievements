package com.izaron.cf.config;

import com.izaron.cf.quartz.PutMongoCacheJob;
import com.izaron.cf.quartz.UpdateContestListJob;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;

@Configuration
@Profile("prod")
public class JobConfig {

    @Bean
    public CronTriggerFactoryBean updateContestListJobTrigger() {
        return buildTrigger(UpdateContestListJob.class, "0 2/15 * * * ?");
    }


    @Bean
    public CronTriggerFactoryBean putMongoCacheJobTrigger() {
        return buildTrigger(PutMongoCacheJob.class, "0/15 * * * * ?");
    }

    private static CronTriggerFactoryBean buildTrigger(Class<? extends Job> jobClazz, String cronExpression) {
        JobDetail jobDetail = JobBuilder.newJob()
                .ofType(jobClazz)
                .build();

        CronTriggerFactoryBean triggerFactoryBean = new CronTriggerFactoryBean();
        triggerFactoryBean.setJobDetail(jobDetail);
        triggerFactoryBean.setCronExpression(cronExpression);
        return triggerFactoryBean;
    }
}
