package com.ishaan.AlertSphere.controller;

import com.ishaan.AlertSphere.entity.Alert;
import com.ishaan.AlertSphere.entity.User;
import com.ishaan.AlertSphere.repository.UserRepository;
import com.ishaan.AlertSphere.service.AlertService;
import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/alert")
public class AlertController {
    @Autowired
    private AlertService alertService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create-alert")
    public ResponseEntity<?> createAlert(@Valid @RequestBody Alert alert){
        User user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        alert.setUser(user);
        if (alert.getTriggerTime() == null) {
            alert.setTriggerTime(LocalDateTime.now());
        }

        return ResponseEntity.ok(alertService.createAlert(alert));
    }

    @GetMapping
    public ResponseEntity<?> getAlert(){
        User user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        return ResponseEntity.ok(alertService.getUserAlert(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAlert(@PathVariable String id) {
        User user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        if (!ObjectId.isValid(id)) {
            return ResponseEntity.badRequest().body("Invalid alert id");
        }

        boolean deleted = alertService.delete(new ObjectId(id), user);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Alert not found");
        }

        return ResponseEntity.noContent().build();
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUserName(username);
    }
}
