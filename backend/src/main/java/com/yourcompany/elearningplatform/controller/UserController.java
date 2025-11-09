package com.yourcompany.elearningplatform.controller;

import com.yourcompany.elearningplatform.entity.User;
import com.yourcompany.elearningplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestParam String email, 
                                          @RequestParam String password,
                                          @RequestParam String fullName,
                                          @RequestParam(defaultValue = "ROLE_STUDENT") String role) {
        try {
            User newUser = userService.registerUser(email, password, fullName, role);
            return ResponseEntity.ok("User registered successfully with id: " + newUser.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // API endpoints for login, details etc. can be added later
}
