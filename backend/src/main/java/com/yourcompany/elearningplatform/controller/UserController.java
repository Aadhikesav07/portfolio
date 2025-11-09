package com.yourcompany.elearningplatform.controller;

import com.yourcompany.elearningplatform.entity.User;
import com.yourcompany.elearningplatform.service.UserService;
import com.yourcompany.elearningplatform.util.RoleUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Public registration - Only students can self-register
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestParam String email, 
                                          @RequestParam String password,
                                          @RequestParam String fullName,
                                          @RequestParam(defaultValue = "ROLE_STUDENT") String role) {
        // Only allow student self-registration
        if (!role.equals("ROLE_STUDENT") && !role.equals("ROLE_STUDENT")) {
            return ResponseEntity.status(403).body("Only students can self-register. Other roles must be created by ADMIN.");
        }

        try {
            User newUser = userService.registerUser(email, password, fullName, "ROLE_STUDENT");
            return ResponseEntity.ok("User registered successfully with id: " + newUser.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Create instructor - Only ADMIN
    @PostMapping("/create-instructor")
    public ResponseEntity<?> createInstructor(@RequestParam String email,
                                             @RequestParam String password,
                                             @RequestParam String fullName,
                                             Authentication authentication) {
        if (!RoleUtil.isAdmin(authentication)) {
            return ResponseEntity.status(403).body("Only ADMIN can create instructors");
        }

        try {
            User newUser = userService.registerUser(email, password, fullName, "ROLE_INSTRUCTOR");
            return ResponseEntity.ok("Instructor created successfully with id: " + newUser.getId());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Get current user info
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        String email = RoleUtil.getUserId(authentication);
        User user = userService.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        // Don't return password
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    // Get all users - Only ADMIN
    @GetMapping
    public ResponseEntity<?> getAllUsers(Authentication authentication) {
        if (!RoleUtil.isAdmin(authentication)) {
            return ResponseEntity.status(403).body("Only ADMIN can view all users");
        }
        List<User> users = userService.getAllUsers();
        users.forEach(u -> u.setPassword(null)); // Remove passwords
        return ResponseEntity.ok(users);
    }
}
