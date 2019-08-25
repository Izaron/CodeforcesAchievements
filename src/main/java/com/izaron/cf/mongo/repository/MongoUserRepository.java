package com.izaron.cf.mongo.repository;

import com.izaron.cf.helper.Achievement;
import com.izaron.cf.mongo.types.MongoUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Collection;
import java.util.List;

public interface MongoUserRepository extends MongoRepository<MongoUser, String> {

    @Query(value = "{ '_id' : ?0 }", fields = "{ 'achievements' : 1 }")
    MongoUser getUserAchievements(String handle);

    @Query(value = "{ '_id' : ?0 }", fields = "{ 'payload.?1' : 1 }")
    MongoUser getUserPayload(String handle, Achievement achievement);

    @Query(value = "{ '_id' : { '$in' : ?0 } }",
            fields = "{ 'achievements' : 1, 'payload.?1' : 1 }")
    List<MongoUser> getUsersForAchievements(Collection<String> handles, Achievement achievement);

    @Query("{ '_id' : { '$in' : ?0 } }")
    List<MongoUser> getUsersByHandles(Collection<String> handles);
}