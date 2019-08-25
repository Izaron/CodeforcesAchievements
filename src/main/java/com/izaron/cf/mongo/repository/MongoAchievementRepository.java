package com.izaron.cf.mongo.repository;

import com.izaron.cf.helper.Achievement;
import com.izaron.cf.mongo.types.MongoAchievement;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoAchievementRepository extends MongoRepository<MongoAchievement, Achievement> {
}
