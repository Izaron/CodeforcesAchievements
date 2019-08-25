package com.izaron.cf.listener;

import com.izaron.cf.helper.Achievement;
import com.izaron.cf.api.ApiMethods;
import com.izaron.cf.domain.Member;
import com.izaron.cf.domain.ProblemResult;
import com.izaron.cf.domain.RanklistRow;
import com.izaron.cf.domain.extra.ContestStandings;
import com.izaron.cf.domain.params.ContestStandingsParams;
import com.izaron.cf.event.UpdateContestsEvent;
import com.izaron.cf.mongo.types.MongoUser;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class FirstBloodListener extends AbstractContestListener {

    @Autowired
    private ApiMethods apiMethods;

    @Override
    protected Achievement getAchievement() {
        return Achievement.FIRST_BLOOD;
    }

    @Override
    protected void processContest(long contestId) {
        ContestStandingsParams params = ContestStandingsParams.builder()
                .contestId(contestId)
                .showUnofficial(false)
                .build();

        ContestStandings standings = apiMethods.getContestStandings(params);

        for (RanklistRow row : standings.getRows()) {
            if (haveSolvedTask(row)) {
                List<Member> members = row.getParty().getMembers();
                for (Member member : members) {
                    processUser(contestId, member.getHandle());
                }
            }
        }
    }

    private boolean haveSolvedTask(RanklistRow row) {
        for (ProblemResult result : row.getProblemResults()) {
            if (result.getPoints() > 0) {
                return true;
            }
        }
        return false;
    }

    private void processUser(long contestId, String handle) {
        MongoUser user = mongoCacheService.getUser(handle);
        if (user.hasAchievement(getAchievement())) {
            return;
        }

        Map<String, Object> payload = user.getPayload(getAchievement());
        payload.put("contest", contestId);

        mongoCacheService.giveAchievement(handle, getAchievement());
    }

    @Synchronized
//    @EventListener
    @Override
    protected void eventPoll(UpdateContestsEvent event) {
        performUpdates(event);
    }
}
