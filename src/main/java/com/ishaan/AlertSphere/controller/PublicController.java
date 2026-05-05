package com.ishaan.AlertSphere.controller;



import com.ishaan.AlertSphere.dto.UserDto;
import com.ishaan.AlertSphere.entity.User;
import com.ishaan.AlertSphere.service.UserDetailsServiceImpl;
import com.ishaan.AlertSphere.service.UserService;
import com.ishaan.AlertSphere.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class PublicController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/health-check")
    public String healtcheck(){
        return "ok";
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDto user){
        User newUser = new User();
        newUser.setUserName(user.getUserName());
        newUser.setPassword(user.getPassword());
        newUser.setEmail(user.getEmail());
        userService.saveUser(newUser);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/create-admin-user")
    public void createAdmin(@RequestBody User user){
        userService.saveAdmin(user);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody User user){
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUserName(),user.getPassword())
            );
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        }catch (Exception e){
            log.error("Exception occurred while createAuthenticationToken ", e);
            return new ResponseEntity<>("Incorrect username or password", HttpStatus.BAD_REQUEST);
        }
    }

}
