package com.izaron.cf.mongo.types;

import com.izaron.cf.helper.Achievement;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Document(collection = "achievement")
public class MongoAchievement implements MongoBaseDocument<Achievement> {

    @Id private Achievement achievement;
    private Set<String> users = ConcurrentHashMap.newKeySet();
    private int awardCount;

    public static MongoAchievement of(Achievement achievement) {
        MongoAchievement mongoAchievement = new MongoAchievement();
        mongoAchievement.setAchievement(achievement);
        mongoAchievement.setAwardCount(0);
        return mongoAchievement;
    }

    public void addUser(String userId) {
        users.add(userId);
    }

    @Override
    public void setId(Achievement id) {
        this.achievement = id;
    }

    @Override
    public Achievement getId() {
        return achievement;
    }
}
