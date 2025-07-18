package com.taskmanager.repository;

import com.taskmanager.model.Task;
import com.taskmanager.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Task operations
 * 
 * Provides data access methods for task management
 * including CRUD operations and custom queries.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Find tasks by user
     */
    Page<Task> findByUser(User user, Pageable pageable);

    /**
     * Find tasks by user ID
     */
    Page<Task> findByUserId(Long userId, Pageable pageable);

    /**
     * Find tasks by status
     */
    List<Task> findByStatus(Task.TaskStatus status);

    /**
     * Find tasks by status with pagination
     */
    Page<Task> findByStatus(Task.TaskStatus status, Pageable pageable);

    /**
     * Find tasks by priority
     */
    List<Task> findByPriority(Task.TaskPriority priority);

    /**
     * Find tasks by user and status
     */
    List<Task> findByUserAndStatus(User user, Task.TaskStatus status);

    /**
     * Find tasks by user ID and status
     */
    Page<Task> findByUserIdAndStatus(Long userId, Task.TaskStatus status, Pageable pageable);

    /**
     * Find tasks by user and priority
     */
    List<Task> findByUserAndPriority(User user, Task.TaskPriority priority);

    /**
     * Find tasks created within date range
     */
    @Query("SELECT t FROM Task t WHERE t.createdAt BETWEEN :startDate AND :endDate")
    List<Task> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);

    /**
     * Find tasks due within date range
     */
    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :startDate AND :endDate")
    List<Task> findByDueDateBetween(@Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate);

    /**
     * Find overdue tasks
     */
    @Query("SELECT t FROM Task t WHERE t.dueDate < :currentDate AND t.status != 'DONE'")
    List<Task> findOverdueTasks(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Find tasks due soon
     */
    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :now AND :futureDate AND t.status != 'DONE'")
    List<Task> findTasksDueSoon(@Param("now") LocalDateTime now, 
                               @Param("futureDate") LocalDateTime futureDate);

    /**
     * Find tasks by title containing search term
     */
    @Query("SELECT t FROM Task t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Task> findByTitleContainingIgnoreCase(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find tasks by title or description containing search term
     */
    @Query("SELECT t FROM Task t WHERE " +
           "LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Task> findByTitleOrDescriptionContainingIgnoreCase(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find user's tasks by search term
     */
    @Query("SELECT t FROM Task t WHERE t.user = :user AND " +
           "(LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Task> findByUserAndSearchTerm(@Param("user") User user, 
                                      @Param("searchTerm") String searchTerm, 
                                      Pageable pageable);

    /**
     * Count tasks by status
     */
    @Query("SELECT COUNT(t) FROM Task t WHERE t.status = :status")
    Long countByStatus(@Param("status") Task.TaskStatus status);

    /**
     * Count tasks by user and status
     */
    @Query("SELECT COUNT(t) FROM Task t WHERE t.user = :user AND t.status = :status")
    Long countByUserAndStatus(@Param("user") User user, @Param("status") Task.TaskStatus status);

    /**
     * Count overdue tasks
     */
    @Query("SELECT COUNT(t) FROM Task t WHERE t.dueDate < :currentDate AND t.status != 'DONE'")
    Long countOverdueTasks(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Count user's overdue tasks
     */
    @Query("SELECT COUNT(t) FROM Task t WHERE t.user = :user AND t.dueDate < :currentDate AND t.status != 'DONE'")
    Long countUserOverdueTasks(@Param("user") User user, @Param("currentDate") LocalDateTime currentDate);

    /**
     * Get task statistics by status
     */
    @Query("SELECT t.status, COUNT(t) FROM Task t GROUP BY t.status")
    List<Object[]> getTaskStatisticsByStatus();

    /**
     * Get task statistics by priority
     */
    @Query("SELECT t.priority, COUNT(t) FROM Task t GROUP BY t.priority")
    List<Object[]> getTaskStatisticsByPriority();

    /**
     * Get user's task statistics by status
     */
    @Query("SELECT t.status, COUNT(t) FROM Task t WHERE t.user = :user GROUP BY t.status")
    List<Object[]> getUserTaskStatisticsByStatus(@Param("user") User user);

    /**
     * Find recently completed tasks
     */
    @Query("SELECT t FROM Task t WHERE t.status = 'DONE' AND t.completedAt >= :since ORDER BY t.completedAt DESC")
    List<Task> findRecentlyCompletedTasks(@Param("since") LocalDateTime since, Pageable pageable);

    /**
     * Find user's recently completed tasks
     */
    @Query("SELECT t FROM Task t WHERE t.user = :user AND t.status = 'DONE' AND t.completedAt >= :since ORDER BY t.completedAt DESC")
    List<Task> findUserRecentlyCompletedTasks(@Param("user") User user, 
                                             @Param("since") LocalDateTime since, 
                                             Pageable pageable);
}

