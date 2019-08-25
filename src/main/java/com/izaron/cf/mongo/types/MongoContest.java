package com.izaron.cf.mongo.types;

import com.izaron.cf.helper.Achievement;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Document(collection = "contest")
public class MongoContest implements MongoBaseDocument<Long> {

    @Id private Long id;
    private Set<Achievement> achievementsProcessed = ConcurrentHashMap.newKeySet();

    public static MongoContest of(Long id) {
        MongoContest contest = new MongoContest();
        contest.setId(id);
        contest.setAchievementsProcessed(new HashSet<>());
        return contest;
    }

    public void addProcessedAchievement(Achievement achievement) {
        achievementsProcessed.add(achievement);
    }

    public boolean hasProcessedAchievement(Achievement achievement) {
        return achievementsProcessed.contains(achievement);
    }
}
