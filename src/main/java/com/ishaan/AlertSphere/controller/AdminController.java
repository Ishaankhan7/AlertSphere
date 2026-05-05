package com.ishaan.AlertSphere.controller;



import com.ishaan.AlertSphere.entity.User;
import com.ishaan.AlertSphere.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;



    @GetMapping("/all-users")
    public ResponseEntity<?> getAllUser() {
        List<User> all = userService.getAllUser();
        if (all != null && !all.isEmpty()) {
            return new ResponseEntity<>(all, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
