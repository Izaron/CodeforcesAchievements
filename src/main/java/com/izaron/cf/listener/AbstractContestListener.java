package com.izaron.cf.listener;

import com.izaron.cf.helper.Achievement;
import com.izaron.cf.domain.Contest;
import com.izaron.cf.event.UpdateContestsEvent;
import com.izaron.cf.service.MongoCacheService;
import com.izaron.cf.mongo.types.MongoContest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractContestListener {

    @Autowired
    protected MongoCacheService mongoCacheService;

    protected abstract Achievement getAchievement();
    protected abstract void processContest(long contestId);
    protected abstract void eventPoll(UpdateContestsEvent event);

    protected void performUpdates(UpdateContestsEvent event) {
        log.debug("Processing updated contests");

        for (Contest contest : event.getContests()) {
            long contestId = contest.getId();
            MongoContest mongoContest = mongoCacheService.getContest(contestId);
            if (!mongoContest.hasProcessedAchievement(getAchievement())) {
                performUpdate(contestId);
            }
        }
    }

    private void performUpdate(long contestId) {
        log.debug("Adding achievement {} to contest {}", getAchievement(), contestId);
        processContest(contestId);

        // Add achievement marker
        MongoContest mongoContest = mongoCacheService.getContest(contestId);
        mongoContest.addProcessedAchievement(getAchievement());
        mongoCacheService.notifyChangedContest(contestId);
    }
}
