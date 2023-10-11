package com.hdshop.controllers;

import com.hdshop.dtos.user.UserDTO;
import com.hdshop.services.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getSingleUser(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
}
