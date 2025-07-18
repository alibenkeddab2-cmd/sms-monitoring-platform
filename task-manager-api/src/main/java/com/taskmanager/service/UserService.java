package com.taskmanager.service;

import com.taskmanager.dto.RegisterRequest;
import com.taskmanager.dto.UserDto;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.exception.UserAlreadyExistsException;
import com.taskmanager.model.User;
import com.taskmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * User Service
 * 
 * Provides business logic for user management operations
 * including registration, authentication, and profile management.
 */
@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Load user by username for Spring Security
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + usernameOrEmail));
        return user;
    }

    /**
     * Register a new user
     */
    public UserDto registerUser(RegisterRequest registerRequest) {
        // Check if username already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new UserAlreadyExistsException("Username is already taken: " + registerRequest.getUsername());
        }

        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new UserAlreadyExistsException("Email is already in use: " + registerRequest.getEmail());
        }

        // Create new user
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setRole(User.Role.USER);
        user.setEnabled(true);

        User savedUser = userRepository.save(user);
        return UserDto.fromEntity(savedUser);
    }

    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return UserDto.fromEntity(user);
    }

    /**
     * Get user by username
     */
    @Transactional(readOnly = true)
    public UserDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return UserDto.fromEntity(user);
    }

    /**
     * Get user entity by username (for internal use)
     */
    @Transactional(readOnly = true)
    public User getUserEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    /**
     * Get user entity by ID (for internal use)
     */
    @Transactional(readOnly = true)
    public User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    /**
     * Get all users with pagination
     */
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserDto::fromEntity);
    }

    /**
     * Search users by search term
     */
    @Transactional(readOnly = true)
    public Page<UserDto> searchUsers(String searchTerm, Pageable pageable) {
        return userRepository.findBySearchTerm(searchTerm, pageable)
                .map(UserDto::fromEntity);
    }

    /**
     * Update user profile
     */
    public UserDto updateUserProfile(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Check if new username is already taken by another user
        if (!user.getUsername().equals(userDto.getUsername()) && 
            userRepository.existsByUsername(userDto.getUsername())) {
            throw new UserAlreadyExistsException("Username is already taken: " + userDto.getUsername());
        }

        // Check if new email is already taken by another user
        if (!user.getEmail().equals(userDto.getEmail()) && 
            userRepository.existsByEmail(userDto.getEmail())) {
            throw new UserAlreadyExistsException("Email is already in use: " + userDto.getEmail());
        }

        // Update user fields
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());

        User updatedUser = userRepository.save(user);
        return UserDto.fromEntity(updatedUser);
    }

    /**
     * Update user role (Admin only)
     */
    public UserDto updateUserRole(Long id, User.Role role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setRole(role);
        User updatedUser = userRepository.save(user);
        return UserDto.fromEntity(updatedUser);
    }

    /**
     * Enable/disable user (Admin only)
     */
    public UserDto toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setEnabled(!user.getEnabled());
        User updatedUser = userRepository.save(user);
        return UserDto.fromEntity(updatedUser);
    }

    /**
     * Delete user
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Get users by role
     */
    @Transactional(readOnly = true)
    public List<UserDto> getUsersByRole(User.Role role) {
        return userRepository.findByRole(role)
                .stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get enabled users
     */
    @Transactional(readOnly = true)
    public List<UserDto> getEnabledUsers() {
        return userRepository.findByEnabledTrue()
                .stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get users with tasks due soon
     */
    @Transactional(readOnly = true)
    public List<UserDto> getUsersWithTasksDueSoon(int hours) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusHours(hours);
        
        return userRepository.findUsersWithTasksDueSoon(now, futureDate)
                .stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get most active users
     */
    @Transactional(readOnly = true)
    public List<UserDto> getMostActiveUsers(Pageable pageable) {
        return userRepository.findMostActiveUsers(pageable)
                .stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get user statistics
     */
    @Transactional(readOnly = true)
    public UserStatistics getUserStatistics() {
        long totalUsers = userRepository.count();
        long enabledUsers = userRepository.countByEnabledTrue();
        long adminUsers = userRepository.countByRole(User.Role.USER);
        long regularUsers = userRepository.countByRole(User.Role.ADMIN);

        return new UserStatistics(totalUsers, enabledUsers, adminUsers, regularUsers);
    }

    /**
     * Check if user exists by username
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Check if user exists by email
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * User Statistics DTO
     */
    public static class UserStatistics {
        private final long totalUsers;
        private final long enabledUsers;
        private final long adminUsers;
        private final long regularUsers;

        public UserStatistics(long totalUsers, long enabledUsers, long adminUsers, long regularUsers) {
            this.totalUsers = totalUsers;
            this.enabledUsers = enabledUsers;
            this.adminUsers = adminUsers;
            this.regularUsers = regularUsers;
        }

        // Getters
        public long getTotalUsers() { return totalUsers; }
        public long getEnabledUsers() { return enabledUsers; }
        public long getAdminUsers() { return adminUsers; }
        public long getRegularUsers() { return regularUsers; }
    }
}

