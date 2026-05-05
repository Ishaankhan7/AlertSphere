package com.ishaan.AlertSphere.controller;

import com.ishaan.AlertSphere.entity.User;
import com.ishaan.AlertSphere.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/update-user")
    public ResponseEntity<?> updateUser(@RequestBody User user){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User userInDb =userService.findByUserName(username);
        if (userInDb == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if (user.getUserName() != null) {
            userInDb.setUserName(user.getUserName());
        }
        if (user.getPassword() != null) {
            userInDb.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userService.saveUser(userInDb);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
