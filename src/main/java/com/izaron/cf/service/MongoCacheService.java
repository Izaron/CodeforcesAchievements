package com.izaron.cf.service;

import com.izaron.cf.helper.Achievement;
import com.izaron.cf.mongo.repository.MongoAchievementRepository;
import com.izaron.cf.mongo.repository.MongoContestRepository;
import com.izaron.cf.mongo.repository.MongoUserRepository;
import com.izaron.cf.mongo.types.MongoAchievement;
import com.izaron.cf.mongo.types.MongoBaseDocument;
import com.izaron.cf.mongo.types.MongoContest;
import com.izaron.cf.mongo.types.MongoUser;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Log4j2
public class MongoCacheService {

    @Autowired
    private MongoUserRepository mongoUserRepository;
    @Autowired
    private MongoContestRepository mongoContestRepository;
    @Autowired
    private MongoAchievementRepository mongoAchievementRepository;

    private CachePart<MongoUser, String> userCachePart;
    private CachePart<MongoContest, Long> contestCachePart;
    private CachePart<MongoAchievement, Achievement> achievementCachePart;
    private List<CachePart> cacheParts;

    @PostConstruct
    private void postConstruct() {
        userCachePart = new CachePart<>(mongoUserRepository, MongoUser::of);
        contestCachePart = new CachePart<>(mongoContestRepository, MongoContest::of);
        achievementCachePart = new CachePart<>(mongoAchievementRepository, MongoAchievement::of);

        cacheParts = Arrays.asList(userCachePart, contestCachePart, achievementCachePart);
    }

    @Synchronized
    public MongoUser getUser(String id) {
        return userCachePart.get(id);
    }

    @Synchronized
    public MongoContest getContest(Long id) {
        return contestCachePart.get(id);
    }

    @Synchronized
    public MongoAchievement getAchievement(Achievement id) {
        return achievementCachePart.get(id);
    }

    @Synchronized
    public void giveAchievement(String userId, Achievement achievement) {
        // Updating achievement
        MongoAchievement mongoAchievement = getAchievement(achievement);
        mongoAchievement.setAwardCount(mongoAchievement.getAwardCount() + 1);
        mongoAchievement.addUser(userId);

        // Updating user
        MongoUser user = getUser(userId);
        user.addAchievement(achievement);

        // Notifying changes
        notifyChangedAchievement(achievement);
        notifyChangedUser(userId);
    }

    @Synchronized
    public void notifyChangedUser(String id) {
        userCachePart.changed(id);
    }

    @Synchronized
    public void notifyChangedContest(Long id) {
        contestCachePart.changed(id);
    }

    @Synchronized
    public void notifyChangedAchievement(Achievement id) {
        achievementCachePart.changed(id);
    }

    @Synchronized
    public void putCache() {
        cacheParts.forEach(CachePart::flush);
    }

    private  <T, K> void putCachePart(Set<T> changedSet, Map<T, K> cacheMap, MongoRepository<K, T> repository) {
        if (!changedSet.isEmpty()) {
            List<K> list = changedSet.stream().map(cacheMap::get).collect(Collectors.toList());
            repository.saveAll(list);
            changedSet.clear();
        }
    }

    private static class CachePart<T extends MongoBaseDocument<ID>, ID> {

        private MongoRepository<T, ID> mongoRepository;
        private Map<ID, T> idMap;
        private Set<ID> changedSet;
        private Function<ID, T> builder;

        public CachePart(MongoRepository<T, ID> mongoRepository, Function<ID, T> builder) {
            this.mongoRepository = mongoRepository;
            this.builder = builder;
            this.changedSet = new HashSet<>();

            List<T> list = mongoRepository.findAll();
            this.idMap = list.stream().collect(Collectors.toMap(MongoBaseDocument::getId, Function.identity()));
        }

        public T get(ID id) {
            return idMap.computeIfAbsent(id, key -> {
                T result = builder.apply(key);
                changedSet.add(key);
                return result;
            });
        }

        public void changed(ID id) {
            changedSet.add(id);
        }

        public void flush() {
            if (!changedSet.isEmpty()) {
                List<T> list = changedSet.stream().map(idMap::get).collect(Collectors.toList());
                mongoRepository.saveAll(list);
                changedSet.clear();
            }
        }
    }
}
