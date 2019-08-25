package com.izaron.cf.mongo.repository;

import com.izaron.cf.mongo.types.ApiQuery;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Map;

public interface ApiQueryRepository extends MongoRepository<ApiQuery, String> {

    List<ApiQuery> findByMethodName(String methodName);
    List<ApiQuery> findByMethodNameAndParams(String methodName, Map<String, List<String>> params);
    void deleteByMethodNameAndParams(String methodName, Map<String, List<String>> params);
}
