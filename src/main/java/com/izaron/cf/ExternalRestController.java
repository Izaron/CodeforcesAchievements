package com.izaron.cf;

import com.izaron.cf.helper.Achievement;
import com.izaron.cf.mongo.types.MongoAchievement;
import com.izaron.cf.mongo.repository.MongoAchievementRepository;
import com.izaron.cf.mongo.types.MongoUser;
import com.izaron.cf.mongo.repository.MongoUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class ExternalRestController {

    @Autowired
    private MongoUserRepository mongoUserRepository;
    @Autowired
    private MongoAchievementRepository mongoAchievementRepository;

    @GetMapping(value = "/user/{id}", produces = "application/json")
    public ResponseEntity<MongoUser> getUser(@PathVariable String id) {
        Optional<MongoUser> mongoUser = mongoUserRepository.findById(id);
        return convert(mongoUser);
    }

    @GetMapping(value = "/achievement/{id}", produces = "application/json")
    public ResponseEntity<MongoAchievement> getAchievement(@PathVariable String id) {
        Optional<MongoAchievement> mongoAchievement = mongoAchievementRepository.findById(Achievement.valueOf(id));
        return convert(mongoAchievement);
    }

    private <T> ResponseEntity<T> convert(Optional<T> optional) {
        if (optional.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(optional.get(), HttpStatus.OK);
        }
    }
}
