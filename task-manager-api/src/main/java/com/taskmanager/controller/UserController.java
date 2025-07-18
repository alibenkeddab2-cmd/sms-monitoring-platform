package com.taskmanager.controller;

import com.taskmanager.dto.UserDto;
import com.taskmanager.model.User;
import com.taskmanager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User Controller
 * 
 * Handles user management endpoints including profile management
 * and user administration operations.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "User management operations")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Get current user profile
     */
    @GetMapping("/profile")
    @Operation(summary = "Get user profile", description = "Get current user's profile information")
    public ResponseEntity<UserDto> getCurrentUserProfile(Authentication authentication) {
        UserDto userDto = userService.getUserByUsername(authentication.getName());
        return ResponseEntity.ok(userDto);
    }

    /**
     * Update current user profile
     */
    @PutMapping("/profile")
    @Operation(summary = "Update user profile", description = "Update current user's profile information")
    public ResponseEntity<UserDto> updateCurrentUserProfile(@Valid @RequestBody UserDto userDto,
                                                           Authentication authentication) {
        UserDto currentUser = userService.getUserByUsername(authentication.getName());
        UserDto updatedUser = userService.updateUserProfile(currentUser.getId(), userDto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Get all users (Admin only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Retrieve all users with pagination (Admin only)")
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<UserDto> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Get user by ID (Admin only)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user by ID", description = "Retrieve user by ID (Admin only)")
    public ResponseEntity<UserDto> getUserById(@Parameter(description = "User ID") @PathVariable Long id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Search users (Admin only)
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Search users", description = "Search users by name or username (Admin only)")
    public ResponseEntity<Page<UserDto>> searchUsers(
            @Parameter(description = "Search term") @RequestParam String q,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<UserDto> users = userService.searchUsers(q, pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Get users by role (Admin only)
     */
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get users by role", description = "Retrieve users by role (Admin only)")
    public ResponseEntity<List<UserDto>> getUsersByRole(
            @Parameter(description = "User role") @PathVariable User.Role role) {
        List<UserDto> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    /**
     * Get enabled users (Admin only)
     */
    @GetMapping("/enabled")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get enabled users", description = "Retrieve all enabled users (Admin only)")
    public ResponseEntity<List<UserDto>> getEnabledUsers() {
        List<UserDto> users = userService.getEnabledUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Get most active users (Admin only)
     */
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get active users", description = "Retrieve most active users (Admin only)")
    public ResponseEntity<List<UserDto>> getMostActiveUsers(
            @Parameter(description = "Maximum number of users") @RequestParam(defaultValue = "10") int limit) {
        
        Pageable pageable = PageRequest.of(0, limit);
        List<UserDto> users = userService.getMostActiveUsers(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Get users with tasks due soon (Admin only)
     */
    @GetMapping("/tasks-due-soon")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get users with tasks due soon", description = "Retrieve users with tasks due within specified hours (Admin only)")
    public ResponseEntity<List<UserDto>> getUsersWithTasksDueSoon(
            @Parameter(description = "Hours ahead to check") @RequestParam(defaultValue = "24") int hours) {
        List<UserDto> users = userService.getUsersWithTasksDueSoon(hours);
        return ResponseEntity.ok(users);
    }

    /**
     * Update user role (Admin only)
     */
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user role", description = "Update user's role (Admin only)")
    public ResponseEntity<UserDto> updateUserRole(
            @Parameter(description = "User ID") @PathVariable Long id,
            @Parameter(description = "New role") @RequestParam User.Role role) {
        UserDto updatedUser = userService.updateUserRole(id, role);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Toggle user status (Admin only)
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Toggle user status", description = "Enable/disable user account (Admin only)")
    public ResponseEntity<UserDto> toggleUserStatus(@Parameter(description = "User ID") @PathVariable Long id) {
        UserDto updatedUser = userService.toggleUserStatus(id);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Delete user (Admin only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Delete user account (Admin only)")
    public ResponseEntity<Void> deleteUser(@Parameter(description = "User ID") @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get user statistics (Admin only)
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user statistics", description = "Get user statistics (Admin only)")
    public ResponseEntity<UserService.UserStatistics> getUserStatistics() {
        UserService.UserStatistics statistics = userService.getUserStatistics();
        return ResponseEntity.ok(statistics);
    }
}

