package com.izaron.cf.mongo.repository;

import com.izaron.cf.mongo.types.MongoContest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Collection;
import java.util.Set;

public interface MongoContestRepository extends MongoRepository<MongoContest, Long> {

    @Query(value = "{ '_id' : { '$in': ?0 } }", fields = "{ '_id' : 1 }")
    Set<MongoContest> getExistingIds(Collection<Long> ids);
}