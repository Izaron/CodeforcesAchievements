package com.izaron.cf.listener;

import com.izaron.cf.api.ApiMethods;
import com.izaron.cf.domain.Member;
import com.izaron.cf.domain.Party;
import com.izaron.cf.domain.Submission;
import com.izaron.cf.domain.params.ContestStatusParams;
import com.izaron.cf.event.UpdateContestsEvent;
import com.izaron.cf.helper.Achievement;
import com.izaron.cf.helper.Language;
import com.izaron.cf.mongo.types.MongoUser;
import com.izaron.cf.service.MongoCacheService;
import lombok.Synchronized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LanguageCounterListenersUmbrella extends AbstractContestListener {

    @Autowired
    private ApiMethods apiMethods;

    private List<AbstractLanguageCounterListener> languageListeners;

    @PostConstruct
    void postConstruct() {
        languageListeners = new ArrayList<>();

        int studentCount = 10;
        int masterCount = 30;
        int wizardCount = 100;
        int exoticCount = 10;

        languageListeners = Arrays.asList(
            buildSingleListener(Language.CPP, studentCount, Achievement.CPP_STUDENT),
            buildSingleListener(Language.CPP, masterCount, Achievement.CPP_MASTER),
            buildSingleListener(Language.CPP, wizardCount, Achievement.CPP_WIZARD),

            buildSingleListener(Language.JAVA, studentCount, Achievement.JAVA_STUDENT),
            buildSingleListener(Language.JAVA, masterCount, Achievement.JAVA_MASTER),
            buildSingleListener(Language.JAVA, wizardCount, Achievement.JAVA_WIZARD),

            buildSingleListener(Language.PYTHON, studentCount, Achievement.PYTHON_STUDENT),
            buildSingleListener(Language.PYTHON, masterCount, Achievement.PYTHON_MASTER),
            buildSingleListener(Language.PYTHON, wizardCount, Achievement.PYTHON_WIZARD),

            buildSingleListener(Language.PASCAL, studentCount, Achievement.PASCAL_STUDENT),
            buildSingleListener(Language.PASCAL, masterCount, Achievement.PASCAL_MASTER),
            buildSingleListener(Language.PASCAL, wizardCount, Achievement.PASCAL_WIZARD),

            buildExoticListener(Set.of(
                Language.CPP,
                Language.JAVA,
                Language.PYTHON,
                Language.PASCAL,
                Language.APRIL_FOOLS_LANGUAGE,
                Language.UNKNOWN
            ), exoticCount, Achievement.EXOTIC_WEAPON)
        );
    }

    private AbstractLanguageCounterListener buildSingleListener(Language language, int count, Achievement achievement) {
        AbstractLanguageCounterListener listener = new SingleLanguageCounterListener() {
            @Override
            protected Language getLanguage() {
                return language;
            }

            @Override
            protected int getCount() {
                return count;
            }

            @Override
            protected Achievement getAchievement() {
                return achievement;
            }
        };

        listener.setMongoCacheService(mongoCacheService);

        return listener;
    }

    private AbstractLanguageCounterListener buildExoticListener(Set<Language> prohibitedLanguages,
                                                                int count, Achievement achievement) {
        AbstractLanguageCounterListener listener = new ExoticLanguageCounterListener() {
            @Override
            protected Set<Language> getProhibitedLanguages() {
                return prohibitedLanguages;
            }

            @Override
            protected int getCount() {
                return count;
            }

            @Override
            protected Achievement getAchievement() {
                return achievement;
            }
        };

        listener.setMongoCacheService(mongoCacheService);

        return listener;
    }

    @Override
    protected Achievement getAchievement() {
        // Empty
        return null;
    }

    @Override
    protected void processContest(long contestId) {
        // Empty
    }

    @Override
    @EventListener
    @Synchronized
    protected void eventPoll(UpdateContestsEvent event) {
        for (AbstractLanguageCounterListener listener : languageListeners) {
            listener.eventPoll(event);
        }
    }
    
    private abstract static class AbstractLanguageCounterListener extends AbstractContestListener {

        protected abstract int getCount();
        protected abstract void eventPoll(UpdateContestsEvent event);

        public void setMongoCacheService(MongoCacheService mongoCacheService) {
            this.mongoCacheService = mongoCacheService;
        }
    }

    private abstract class SingleLanguageCounterListener extends AbstractLanguageCounterListener {

        protected abstract Language getLanguage();

        @Override
        protected void processContest(long contestId) {
            ContestStatusParams params = ContestStatusParams.builder()
                    .contestId(contestId)
                    .build();

            // There is only one submission with OK per one solved task
            // no matter how many submissions a party has done
            List<Submission> submissions = apiMethods.getContestStatus(params).stream()
                    .filter(submission -> submission.getVerdict() == Submission.Verdict.OK)
                    .filter(submission -> Language.detect(submission.getProgrammingLanguage()) == getLanguage())
                    .collect(Collectors.toList());

            for (Submission submission : submissions) {
                Party party = submission.getAuthor();
                for (Member member : party.getMembers()) {
                    increaseCounter(contestId, member.getHandle());
                }
            }
        }

        private void increaseCounter(long contestId, String handle) {
            MongoUser user = mongoCacheService.getUser(handle);
            if (user.hasAchievement(getAchievement())) {
                return;
            }

            Map<String, Object> payload = user.getPayload(getAchievement());
            int currentCount = (int) payload.getOrDefault("count", 0);
            currentCount++;
            payload.put("count", currentCount);

            if (currentCount >= getCount()) {
                payload.put("contest", contestId);
                mongoCacheService.giveAchievement(handle, getAchievement());
            }

            mongoCacheService.notifyChangedUser(handle);
        }

        @Override
        protected void eventPoll(UpdateContestsEvent event) {
            performUpdates(event);
        }
    }

    private abstract class ExoticLanguageCounterListener extends AbstractLanguageCounterListener {

        protected abstract Set<Language> getProhibitedLanguages();

        @Override
        protected void processContest(long contestId) {
            ContestStatusParams params = ContestStatusParams.builder()
                    .contestId(contestId)
                    .build();

            // There is only one submission with OK per one solved task
            // no matter how many submissions a party has done
            Set<Language> prohibitedLanguages = getProhibitedLanguages();
            List<Submission> submissions = apiMethods.getContestStatus(params).stream()
                    .filter(submission -> submission.getVerdict() == Submission.Verdict.OK)
                    .filter(submission -> !prohibitedLanguages.contains(
                            Language.detect(submission.getProgrammingLanguage()))
                    )
                    .collect(Collectors.toList());

            for (Submission submission : submissions) {
                Party party = submission.getAuthor();
                Language language = Language.detect(submission.getProgrammingLanguage());
                for (Member member : party.getMembers()) {
                    increaseCounter(contestId, member.getHandle(), language);
                }
            }
        }

        private void increaseCounter(long contestId, String handle, Language language) {
            MongoUser user = mongoCacheService.getUser(handle);
            if (user.hasAchievement(getAchievement())) {
                return;
            }

            Map<String, Object> payload = user.getPayload(getAchievement());
            String counterKey = language.getAliasName() + "_count";
            int currentCount = (int) payload.getOrDefault(counterKey, 0);
            currentCount++;
            payload.put(counterKey, currentCount);

            if (currentCount >= getCount()) {
                payload.put("contest", contestId);
                payload.put("language", language.getAliasName());
                mongoCacheService.giveAchievement(handle, getAchievement());
            }

            mongoCacheService.notifyChangedUser(handle);
        }

        @Override
        protected void eventPoll(UpdateContestsEvent event) {
            performUpdates(event);
        }
    }
}
