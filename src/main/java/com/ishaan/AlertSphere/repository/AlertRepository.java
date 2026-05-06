package com.ishaan.AlertSphere.repository;

import com.ishaan.AlertSphere.entity.Alert;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AlertRepository extends MongoRepository<Alert,ObjectId> {
    List<Alert> findByUserId(String userId);

    List<Alert> findByActiveTrue();

    List<Alert> findByCityIgnoreCaseAndActiveTrue(String city);

    Optional<Alert> findByIdAndUserId(ObjectId id, String userId);
}
