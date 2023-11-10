package com.hdshop.controller;

import com.hdshop.dto.user.UserDTO;
import com.hdshop.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "User")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get one user")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getSingleUser(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "Get user by username")
    @GetMapping()
    public ResponseEntity<UserDTO> getSingleUser(@RequestParam String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @PutMapping("/password/change")
    public ResponseEntity<String> changePasswordOfCurrentUser(@RequestParam String newPassword, Principal principal) {
        String result = userService.changePasswordOfCurrentUser(newPassword, principal);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/password/forgot")
    public ResponseEntity<String> changePasswordByUserEmail(@RequestParam String email,
                                                            @RequestParam String newPassword) {
        String result = userService.changePasswordByUserEmail(email, newPassword);
        return ResponseEntity.ok(result);
    }
}
