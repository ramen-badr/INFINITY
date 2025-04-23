package com.shop.server.controller;

import com.shop.server.dto.UserRequest;
import com.shop.server.model.User;
import com.shop.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserRequest userRequest) {
        if (userRepository.existsByPhoneNumber(userRequest.getPhoneNumber())) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "User with this phone number already exists"));
        }

        User user = new User();
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());

        userRepository.save(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of("message", "User created successfully"));
    }
}