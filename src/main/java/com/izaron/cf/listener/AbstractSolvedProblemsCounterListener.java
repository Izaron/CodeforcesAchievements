package com.izaron.cf.listener;

import com.izaron.cf.api.ApiMethods;
import com.izaron.cf.domain.Member;
import com.izaron.cf.domain.ProblemResult;
import com.izaron.cf.domain.RanklistRow;
import com.izaron.cf.domain.extra.ContestStandings;
import com.izaron.cf.domain.params.ContestStandingsParams;
import com.izaron.cf.mongo.types.MongoUser;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public abstract class AbstractSolvedProblemsCounterListener extends AbstractContestListener {

    @Autowired
    private ApiMethods apiMethods;

    protected abstract int getCountLimit();

    @Override
    protected void processContest(long contestId) {
        // Codeforces API query parameters
        ContestStandingsParams params = ContestStandingsParams.builder()
                .contestId(contestId)
                .showUnofficial(false)
                .build();

        ContestStandings standings = apiMethods.getContestStandings(params);

        for (RanklistRow row : standings.getRows()) {
            List<ProblemResult> problemResults = row.getProblemResults();
            int solvedProblems = (int) problemResults.stream().filter(this::isProblemSolved).count();

            List<Member> members = row.getParty().getMembers();
            members.forEach(member -> increaseProblemCounter(contestId, member.getHandle(), solvedProblems));
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
