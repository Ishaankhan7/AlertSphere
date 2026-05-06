package com.ishaan.AlertSphere.controller;

import com.ishaan.AlertSphere.dto.WeatherAlertRequest;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/alert")
public class AlertController {

    @Autowired
    private AlertService alertService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping({"/create-alert"})
    public ResponseEntity<?> createAlert(@Valid @RequestBody WeatherAlertRequest request) {
        User user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(alertService.createAlert(request, user));
    }

    @GetMapping
    public ResponseEntity<?> getAlerts() {
        User user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        return ResponseEntity.ok(alertService.getUserAlerts(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAlert(@PathVariable String id) {
        User user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
        if (!ObjectId.isValid(id)) {
            return ResponseEntity.badRequest().body("Invalid alert id");
        }

        return alertService.getUserAlert(new ObjectId(id), user)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Alert not found"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAlert(
            @PathVariable String id,
            @Valid @RequestBody WeatherAlertRequest request
    ) {
        User user = getCurrentUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
        if (!ObjectId.isValid(id)) {
            return ResponseEntity.badRequest().body("Invalid alert id");
        }

        return alertService.updateAlert(new ObjectId(id), request, user)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Alert not found"));
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
        if (authentication == null || authentication.getName() == null) {
            return null;
        }

        return userRepository.findByUserName(authentication.getName());
    }
}
