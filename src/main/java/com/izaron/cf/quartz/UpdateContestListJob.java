package com.izaron.cf.quartz;

import com.izaron.cf.api.ApiMethods;
import com.izaron.cf.domain.Contest;
import com.izaron.cf.event.UpdateContestsEvent;
import lombok.extern.log4j.Log4j2;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@DisallowConcurrentExecution
public class UpdateContestListJob implements Job {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private ApiMethods apiMethods;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        List<Contest> contests = apiMethods.getContestList();
        if (Objects.isNull(contests)) {
            return;
        }

        // Filter out unfinished contests
        contests = contests.stream()
                .filter(contest -> contest.getPhase() == Contest.Phase.FINISHED)
                .sorted(Comparator.comparingLong(Contest::getId))
                .collect(Collectors.toList());

        applicationEventPublisher.publishEvent(UpdateContestsEvent.of(this, contests));
    }
}
