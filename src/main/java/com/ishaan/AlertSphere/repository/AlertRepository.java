package com.ishaan.AlertSphere.repository;

import com.ishaan.AlertSphere.entity.Alert;
import com.ishaan.AlertSphere.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AlertRepository extends MongoRepository<Alert,ObjectId> {
    List<Alert> findByUser(User user);

    Optional<Alert> findByIdAndUser(ObjectId id, User user);
}
