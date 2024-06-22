package com.duck.controller;

import com.duck.dto.user.ChangePassReq;
import com.duck.dto.user.UserDTO;
import com.duck.dto.user.UserProfile;
import com.duck.dto.user.UserResponse;
import com.duck.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Tag(name = "User")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @SecurityRequirement(name = "Bear Authentication")
    @Operation(summary = "Get a user by id")
    @GetMapping("/id/{id}")
    public ResponseEntity<UserDTO> getSingleUser(@PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @Operation(summary = "Get a user by username or by email")
    @GetMapping("/{usernameOrEmail}")
    public ResponseEntity<UserDTO> getSingleUserByUsernameOrEamil(@PathVariable(value = "usernameOrEmail") String usernameOrEmail) {
        return ResponseEntity.ok(userService.getByUsernameOrEmail(usernameOrEmail));
    }

    @SecurityRequirement(name = "Bear Authentication")
    @Operation(summary = "Change password for current user")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/password/change")
    public ResponseEntity<String> changePasswordOfCurrentUser(@RequestBody ChangePassReq request, Principal principal) {
        String result = userService.changePassword(request, principal);
        return ResponseEntity.ok(result);
    }


    @Operation(summary = "Change password for Forgot password")
    @PutMapping("/password/forgot")
    public ResponseEntity<String> changePasswordByUserEmail(@RequestParam String email,
                                                            @RequestParam String new_pass) {
        String result = userService.forgotPassword(email, new_pass);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Update account profile of signed in user")
    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateProfile(@Valid @RequestBody UserProfile profile, Principal principal) {
        return ResponseEntity.ok(userService.updateProfile(profile, principal));
    }

    @Operation(summary = "Update account profile by id user")
    @SecurityRequirement(name = "Bear Authentication")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/profile/{userId}")
    public ResponseEntity<UserDTO> updateProfileByUserId(@RequestBody @Valid UserProfile profile,
                                                         @PathVariable Long userId) {
        return ResponseEntity.ok(userService.updateProfileById(profile, userId));
    }

    @SecurityRequirement(name = "Bear Authentication")
    @Operation(summary = "Change lock-unlock user account by id user")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{userId}/status")
    public ResponseEntity<UserDTO> changeLockedStatus(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.changeLockedStatus(userId));
    }

    @SecurityRequirement(name = "Bear Authentication")
    @Operation(summary = "Get All Users with pagination")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<UserResponse> getAllUsers(
            @RequestParam(value = "pageNo", required = false,
                    defaultValue = "${paging.default.page-number}") int pageNo,
            @RequestParam(value = "pageSize", required = false,
                    defaultValue = "${paging.default.page-size}") int pageSize
    ) {
        return ResponseEntity.ok(userService.getAll(pageNo, pageSize));
    }

    @SecurityRequirement(name = "Bear Authentication")
    @Operation(summary = "Filter users")
//    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/search")
    public ResponseEntity<UserResponse> filter(
            @RequestParam(name = "key", required = false) String key,
            @RequestParam(name = "sort", required = false) List<String> sortCriteria,
            @RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize,
            @RequestParam(value = "role", required = false, defaultValue = "ROLE_USER") String roleName
    ) {
        return ResponseEntity.ok(userService.filter(key, sortCriteria, pageNo, pageSize, roleName));
    }
}
