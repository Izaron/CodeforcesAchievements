package com.izaron.cf.listener;

import com.izaron.cf.helper.Achievement;
import com.izaron.cf.event.UpdateContestsEvent;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class ExperiencedWarriorListener extends AbstractSolvedProblemsCounterListener {

    private int countLimit;

    public ExperiencedWarriorListener(@Value("${codeforces.achievements.experienced-warrior-count}") int countLimit) {
        this.countLimit = countLimit;
    }

    @Override
    protected int getCountLimit() {
        return countLimit;
    }

    @Override
    protected Achievement getAchievement() {
        return Achievement.EXPERIENCED_WARRIOR;
    }

    @Override
    @EventListener
    @Synchronized
    protected void eventPoll(UpdateContestsEvent event) {
        performUpdates(event);
    }
}
