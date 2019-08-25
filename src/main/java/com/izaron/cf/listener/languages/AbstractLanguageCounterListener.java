package com.izaron.cf.listener.languages;

import com.izaron.cf.api.ApiMethods;
import com.izaron.cf.domain.Member;
import com.izaron.cf.domain.Party;
import com.izaron.cf.domain.ProblemResult;
import com.izaron.cf.domain.Submission;
import com.izaron.cf.domain.params.ContestStatusParams;
import com.izaron.cf.listener.AbstractContestListener;
import com.izaron.cf.mongo.types.MongoUser;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public abstract class AbstractLanguageCounterListener extends AbstractContestListener {

    @Autowired
    private ApiMethods apiMethods;

    protected abstract int getCountLimit();

    @Override
    protected void processContest(long contestId) {
        ContestStatusParams params = ContestStatusParams.builder()
                .contestId(contestId)
                .build();
        List<Submission> submissions = apiMethods.getContestStatus(params);
        submissions.sort(Comparator.comparing(Submission::getCreationTimeSeconds));
        Collections.reverse(submissions);

        for (Submission submission : submissions) {
            Party party = submission.getAuthor();
            for (Member member : party.getMembers()) {
                String handle = member.getHandle();
            }
        }
    }

    private boolean isProblemSolved(ProblemResult problemResult) {
        return problemResult.getPoints() > 0;
    }

    private void increaseProblemCounter(long contestId, String handle, int solvedProblems) {
        MongoUser user = mongoCacheService.getUser(handle);
        if (user.hasAchievement(getAchievement())) {
            return;
        }

        Map<String, Object> payload = user.getPayload(getAchievement());
        int currentCount = (int) payload.getOrDefault("count", 0);
        currentCount += solvedProblems;
        payload.put("count", currentCount);

        if (currentCount >= getCountLimit()) {
            payload.put("contest", contestId);
            mongoCacheService.giveAchievement(handle, getAchievement());
        }

        // notify changes
        mongoCacheService.notifyChangedUser(user.getHandle());
    }
}
