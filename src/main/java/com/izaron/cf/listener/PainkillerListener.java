package com.izaron.cf.listener;

import com.izaron.cf.helper.Achievement;
import com.izaron.cf.api.ApiMethods;
import com.izaron.cf.domain.*;
import com.izaron.cf.domain.extra.ContestStandings;
import com.izaron.cf.domain.params.ContestStandingsParams;
import com.izaron.cf.domain.params.ContestStatusParams;
import com.izaron.cf.event.UpdateContestsEvent;
import com.izaron.cf.mongo.types.MongoUser;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Log4j2
public class PainkillerListener extends AbstractContestListener {

    @Autowired
    private ApiMethods apiMethods;

    @Override
    protected Achievement getAchievement() {
        return Achievement.PAINKILLER;
    }

    @Override
    protected void processContest(long contestId) {
        ContestStandingsParams params = ContestStandingsParams.builder()
                .contestId(contestId)
                .showUnofficial(false)
                .build();

        ContestStandings standings = apiMethods.getContestStandings(params);
        if (standings == null) {
            return;
        }

        // Find the maximal point value for a problem
        List<Problem> problems = standings.getProblems();
        Optional<Double> optional = problems.stream()
                .filter(problem -> !Objects.isNull(problem.getPoints()))
                .map(Problem::getPoints)
                .max(Comparator.comparing(Double::valueOf));
        if (optional.isEmpty()) {
            return;
        }

        double maxPoints = optional.get();
        long maxPointProblems = problems.stream()
                .filter(problem -> !Objects.isNull(problem.getPoints()))
                .filter(problem -> problem.getPoints().equals(maxPoints))
                .count();

        // Max point problem should be unique
        if (maxPointProblems > 1) {
            return;
        }

        for (RanklistRow row : standings.getRows()) {
            List<ProblemResult> problemResults = row.getProblemResults();

            int earliestSolved = -1;
            long lastSubmission = -1;
            for (int i = 0; i < problemResults.size(); i++) {
                ProblemResult problemResult = problemResults.get(i);
                if (problemResult.getPoints() > 0) {
                    long curLastSubmission = Optional.ofNullable(problemResult.getBestSubmissionTimeSeconds()).orElse(0L);
                    if (earliestSolved == -1 || lastSubmission > curLastSubmission) {
                        earliestSolved = i;
                        lastSubmission = curLastSubmission;
                    }
                }
            }

            if (earliestSolved != -1) {
                if (problems.get(earliestSolved).getPoints().equals(maxPoints)) {
                    for (Member member : row.getParty().getMembers()) {
                        award(contestId, member.getHandle(), maxPoints, lastSubmission);
                    }
                }
            }
        }
    }

    private void award(long contestId, String handle, double maxPoints, long timestamp) {
        MongoUser user = mongoCacheService.getUser(handle);
        if (user.hasAchievement(getAchievement())) {
            return;
        }

        // Assuring that the rating changed
        List<RatingChange> ratingChanges = apiMethods.getContestRatingChanges(contestId);
        if (ratingChanges == null ||
                ratingChanges.stream().noneMatch(change -> change.getHandle().equals(handle))) {
            return;
        }

        // Assuring that there were no tries on other tasks before solving the maximum
        ContestStatusParams params = ContestStatusParams.builder()
                .contestId(contestId)
                .build();
        List<Submission> submissions = apiMethods.getContestStatus(params);
        for (Submission submission : submissions) {
            if (submission.getAuthor().getMembers().stream().noneMatch(member -> handle.equals(member.getHandle()))) {
                continue;
            }
            if (submission.getProblem().getPoints() != maxPoints &&
                    submission.getRelativeTimeSeconds() < timestamp) {
                return;
            }
        }

        Map<String, Object> payload = user.getPayload(getAchievement());
        payload.put("contest", contestId);

        mongoCacheService.giveAchievement(handle, getAchievement());
    }

    @Override
//    @EventListener
    @Synchronized
    public void eventPoll(UpdateContestsEvent event) {
        performUpdates(event);
    }
}
