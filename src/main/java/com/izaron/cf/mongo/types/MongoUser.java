package com.izaron.cf.mongo.types;

import com.izaron.cf.helper.Achievement;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Document(collection = "user")
@RequiredArgsConstructor(staticName = "of")
public class MongoUser implements MongoBaseDocument<String> {

    @Id String handle;
    Set<Achievement> achievements = ConcurrentHashMap.newKeySet();
    Map<Achievement, Map<String, Object>> payload = new ConcurrentHashMap<>();

    public static MongoUser of(String handle) {
        MongoUser user = new MongoUser();
        user.setHandle(handle);
        user.setAchievements(new HashSet<>());
        user.setPayload(new HashMap<>());
        return user;
    }

    public Map<String, Object> getPayload(Achievement achievement) {
        return payload.computeIfAbsent(achievement, key -> new ConcurrentHashMap<>());
    }

    public void addAchievement(Achievement achievement) {
        achievements.add(achievement);
    }

    public boolean hasAchievement(Achievement achievement) {
        return achievements.contains(achievement);
    }

    @Override
    public void setId(String id) {
        this.handle = id;
    }

    @Override
    public String getId() {
        return handle;
    }
}
