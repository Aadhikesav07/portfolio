package com.yourcompany.elearningplatform.controller;

import com.yourcompany.elearningplatform.entity.User;
import com.yourcompany.elearningplatform.repository.UserRepository;
import com.yourcompany.elearningplatform.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password)
        );

        User user = userRepository.findByEmail(email).orElseThrow();

        String jwt = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return ResponseEntity.ok().body("{ \"jwt\": \"" + jwt + "\" }");
    }
}
