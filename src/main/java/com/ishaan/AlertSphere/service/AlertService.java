package com.ishaan.AlertSphere.service;

import com.ishaan.AlertSphere.entity.Alert;
import com.ishaan.AlertSphere.entity.User;
import com.ishaan.AlertSphere.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.bson.types.ObjectId;

import java.util.List;

@Service
public class AlertService {

    @Autowired
    private AlertRepository alertRepository;

    public Alert createAlert(Alert alert){
        return alertRepository.save(alert);
    }

    public List<Alert> getUserAlert(User user){
        return alertRepository.findByUser(user);
    }

    public boolean delete(ObjectId id, User user) {
        return alertRepository.findByIdAndUser(id, user)
                .map(alert -> {
                    alertRepository.delete(alert);
                    return true;
                })
                .orElse(false);
    }
}
