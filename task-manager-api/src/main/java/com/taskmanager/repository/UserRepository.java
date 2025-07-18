package com.taskmanager.repository;

import com.taskmanager.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User operations
 * 
 * Provides data access methods for user management
 * including authentication and user queries.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by username or email
     */
    @Query("SELECT u FROM User u WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    Optional<User> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Find users by role
     */
    List<User> findByRole(User.Role role);

    /**
     * Find enabled users
     */
    List<User> findByEnabledTrue();

    /**
     * Find users created within date range
     */
    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate")
    List<User> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);

    /**
     * Find users by first name or last name containing search term
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<User> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Count users by role
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    Long countByRole(@Param("role") User.Role role);

    /**
     * Count enabled users
     */
    Long countByEnabledTrue();

    /**
     * Find users with tasks count
     */
    @Query("SELECT u, COUNT(t) FROM User u LEFT JOIN u.tasks t GROUP BY u")
    List<Object[]> findUsersWithTaskCount();

    /**
     * Find users who have tasks due soon
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.tasks t " +
           "WHERE t.dueDate BETWEEN :now AND :futureDate " +
           "AND t.status != 'DONE'")
    List<User> findUsersWithTasksDueSoon(@Param("now") LocalDateTime now,
                                        @Param("futureDate") LocalDateTime futureDate);

    /**
     * Find most active users (users with most tasks)
     */
    @Query("SELECT u FROM User u LEFT JOIN u.tasks t " +
           "GROUP BY u ORDER BY COUNT(t) DESC")
    List<User> findMostActiveUsers(Pageable pageable);
}

