package com.ishaan.AlertSphere.repository;

import com.ishaan.AlertSphere.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository <User,ObjectId>{
    User findByUserName(String username);

}
