package com.akotako.loanservice.controller;

import com.akotako.loanservice.model.entity.User;
import com.akotako.loanservice.model.exception.UserNotFoundException;
import com.akotako.loanservice.model.request.LoginRequest;
import com.akotako.loanservice.model.request.RegisterRequest;
import com.akotako.loanservice.model.response.ResponseObject;
import com.akotako.loanservice.repository.UserRepository;
import com.akotako.loanservice.service.security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/register")
    public ResponseEntity<ResponseObject<String>> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.ok(ResponseObject.failure("Username is already taken!"));
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole(request.getRole());

        userRepository.save(user);
        return ResponseEntity.ok(ResponseObject.success("User registered successfully!"));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseObject<String>> login(@RequestBody LoginRequest request) {
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User not found!");
        }

        User user = userOptional.get();
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UserNotFoundException("User not found!");
        }

        String role = user.getRole().name();
        String token = jwtTokenUtil.generateToken(user.getUsername(), role);
        return ResponseEntity.ok(ResponseObject.success(token));
    }
}
