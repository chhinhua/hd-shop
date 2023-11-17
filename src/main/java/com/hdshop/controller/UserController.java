package com.hdshop.controller;

import com.hdshop.dto.user.ChangePassReq;
import com.hdshop.dto.user.UserDTO;
import com.hdshop.dto.user.UserProfile;
import com.hdshop.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "User")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get a user by id")
    @GetMapping("/id/{id}")
    public ResponseEntity<UserDTO> getSingleUser(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "Get a user by username or by email")
    @GetMapping("/{usernameOrEmail}")
    public ResponseEntity<UserDTO> getSingleUserByUsernameOrEamil(@PathVariable(value = "usernameOrEmail") String usernameOrEmail) {
        return ResponseEntity.ok(userService.getUserByUsernameOrEmail(usernameOrEmail));
    }

    @Operation(summary = "Change password for current user")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/password/change")
    public ResponseEntity<String> changePasswordOfCurrentUser(@RequestBody ChangePassReq request, Principal principal) {
        String result = userService.changePasswordOfCurrentUser(request, principal);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Change password for Forgot password")
    @PutMapping("/password/forgot")
    public ResponseEntity<String> changePasswordByUserEmail(@RequestParam String email,
                                                            @RequestParam String newPassword) {
        String result = userService.forgotPassword(email, newPassword);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Update account profile of signed in user")
    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateProfile(@Valid @RequestBody UserProfile profile, Principal principal) {
        return ResponseEntity.ok(userService.updateProfile(profile, principal));
    }

    @Operation(summary = "Update account profile by id user")
    @PutMapping("/profile/{userId}")
    public ResponseEntity<UserDTO> updateProfileByUserId(@RequestBody @Valid UserProfile profile,
                                                         @PathVariable Long userId) {
        return ResponseEntity.ok(userService.updateProfileByUserId(profile, userId));
    }

    @Operation(summary = "Change lock-unlock user account by id user")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{userId}/status")
    public ResponseEntity<UserDTO> changeLockedStatus(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.changeLockedStatus(userId));
    }
}
