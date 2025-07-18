package com.taskmanager.service;

import com.taskmanager.dto.TaskDto;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.exception.UnauthorizedAccessException;
import com.taskmanager.model.Task;
import com.taskmanager.model.User;
import com.taskmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Task Service
 * 
 * Provides business logic for task management operations
 * including CRUD operations, status updates, and task queries.
 */
@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    /**
     * Create a new task
     */
    public TaskDto createTask(TaskDto taskDto, String username) {
        User user = userService.getUserEntityByUsername(username);
        
        Task task = taskDto.toEntity();
        task.setUser(user);
        
        Task savedTask = taskRepository.save(task);
        return TaskDto.fromEntity(savedTask);
    }

    /**
     * Get task by ID
     */
    @Transactional(readOnly = true)
    public TaskDto getTaskById(Long id, String username) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        
        // Check if user has access to this task
        if (!hasAccessToTask(task, username)) {
            throw new UnauthorizedAccessException("You don't have access to this task");
        }
        
        return TaskDto.fromEntity(task);
    }

    /**
     * Get all tasks with pagination (Admin only)
     */
    @Transactional(readOnly = true)
    public Page<TaskDto> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable)
                .map(TaskDto::fromEntity);
    }

    /**
     * Get user's tasks with pagination
     */
    @Transactional(readOnly = true)
    public Page<TaskDto> getUserTasks(String username, Pageable pageable) {
        User user = userService.getUserEntityByUsername(username);
        return taskRepository.findByUser(user, pageable)
                .map(TaskDto::fromEntity);
    }

    /**
     * Get tasks by user ID (Admin only)
     */
    @Transactional(readOnly = true)
    public Page<TaskDto> getTasksByUserId(Long userId, Pageable pageable) {
        return taskRepository.findByUserId(userId, pageable)
                .map(TaskDto::fromEntity);
    }

    /**
     * Get tasks by status
     */
    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByStatus(Task.TaskStatus status, String username) {
        User user = userService.getUserEntityByUsername(username);
        return taskRepository.findByUserAndStatus(user, status)
                .stream()
                .map(TaskDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get tasks by status with pagination
     */
    @Transactional(readOnly = true)
    public Page<TaskDto> getTasksByStatus(Task.TaskStatus status, Pageable pageable) {
        return taskRepository.findByStatus(status, pageable)
                .map(TaskDto::fromEntity);
    }

    /**
     * Get user's tasks by status
     */
    @Transactional(readOnly = true)
    public Page<TaskDto> getUserTasksByStatus(Long userId, Task.TaskStatus status, Pageable pageable) {
        return taskRepository.findByUserIdAndStatus(userId, status, pageable)
                .map(TaskDto::fromEntity);
    }

    /**
     * Search tasks by title or description
     */
    @Transactional(readOnly = true)
    public Page<TaskDto> searchTasks(String searchTerm, String username, Pageable pageable) {
        User user = userService.getUserEntityByUsername(username);
        return taskRepository.findByUserAndSearchTerm(user, searchTerm, pageable)
                .map(TaskDto::fromEntity);
    }

    /**
     * Search all tasks (Admin only)
     */
    @Transactional(readOnly = true)
    public Page<TaskDto> searchAllTasks(String searchTerm, Pageable pageable) {
        return taskRepository.findByTitleOrDescriptionContainingIgnoreCase(searchTerm, pageable)
                .map(TaskDto::fromEntity);
    }

    /**
     * Update task
     */
    public TaskDto updateTask(Long id, TaskDto taskDto, String username) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        
        // Check if user has access to this task
        if (!hasAccessToTask(existingTask, username)) {
            throw new UnauthorizedAccessException("You don't have access to this task");
        }
        
        // Update task fields
        existingTask.setTitle(taskDto.getTitle());
        existingTask.setDescription(taskDto.getDescription());
        existingTask.setStatus(taskDto.getStatus());
        existingTask.setPriority(taskDto.getPriority());
        existingTask.setDueDate(taskDto.getDueDate());
        
        Task updatedTask = taskRepository.save(existingTask);
        return TaskDto.fromEntity(updatedTask);
    }

    /**
     * Update task status
     */
    public TaskDto updateTaskStatus(Long id, Task.TaskStatus status, String username) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        
        // Check if user has access to this task
        if (!hasAccessToTask(task, username)) {
            throw new UnauthorizedAccessException("You don't have access to this task");
        }
        
        task.setStatus(status);
        Task updatedTask = taskRepository.save(task);
        return TaskDto.fromEntity(updatedTask);
    }

    /**
     * Delete task
     */
    public void deleteTask(Long id, String username) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        
        // Check if user has access to this task
        if (!hasAccessToTask(task, username)) {
            throw new UnauthorizedAccessException("You don't have access to this task");
        }
        
        taskRepository.delete(task);
    }

    /**
     * Get overdue tasks for user
     */
    @Transactional(readOnly = true)
    public List<TaskDto> getOverdueTasks(String username) {
        User user = userService.getUserEntityByUsername(username);
        LocalDateTime now = LocalDateTime.now();
        
        return taskRepository.findOverdueTasks(now)
                .stream()
                .filter(task -> task.getUser().equals(user))
                .map(TaskDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get all overdue tasks (Admin only)
     */
    @Transactional(readOnly = true)
    public List<TaskDto> getAllOverdueTasks() {
        LocalDateTime now = LocalDateTime.now();
        return taskRepository.findOverdueTasks(now)
                .stream()
                .map(TaskDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get tasks due soon for user
     */
    @Transactional(readOnly = true)
    public List<TaskDto> getTasksDueSoon(String username, int hours) {
        User user = userService.getUserEntityByUsername(username);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusHours(hours);
        
        return taskRepository.findTasksDueSoon(now, futureDate)
                .stream()
                .filter(task -> task.getUser().equals(user))
                .map(TaskDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get recently completed tasks for user
     */
    @Transactional(readOnly = true)
    public List<TaskDto> getRecentlyCompletedTasks(String username, int days, Pageable pageable) {
        User user = userService.getUserEntityByUsername(username);
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        
        return taskRepository.findUserRecentlyCompletedTasks(user, since, pageable)
                .stream()
                .map(TaskDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get task statistics for user
     */
    @Transactional(readOnly = true)
    public TaskStatistics getUserTaskStatistics(String username) {
        User user = userService.getUserEntityByUsername(username);
        LocalDateTime now = LocalDateTime.now();
        
        long totalTasks = taskRepository.countByUserAndStatus(user, null);
        long todoTasks = taskRepository.countByUserAndStatus(user, Task.TaskStatus.TODO);
        long inProgressTasks = taskRepository.countByUserAndStatus(user, Task.TaskStatus.IN_PROGRESS);
        long completedTasks = taskRepository.countByUserAndStatus(user, Task.TaskStatus.DONE);
        long overdueTasks = taskRepository.countUserOverdueTasks(user, now);
        
        return new TaskStatistics(totalTasks, todoTasks, inProgressTasks, completedTasks, overdueTasks);
    }

    /**
     * Get overall task statistics (Admin only)
     */
    @Transactional(readOnly = true)
    public TaskStatistics getOverallTaskStatistics() {
        LocalDateTime now = LocalDateTime.now();
        
        long totalTasks = taskRepository.count();
        long todoTasks = taskRepository.countByStatus(Task.TaskStatus.TODO);
        long inProgressTasks = taskRepository.countByStatus(Task.TaskStatus.IN_PROGRESS);
        long completedTasks = taskRepository.countByStatus(Task.TaskStatus.DONE);
        long overdueTasks = taskRepository.countOverdueTasks(now);
        
        return new TaskStatistics(totalTasks, todoTasks, inProgressTasks, completedTasks, overdueTasks);
    }

    /**
     * Check if user has access to task
     */
    private boolean hasAccessToTask(Task task, String username) {
        User user = userService.getUserEntityByUsername(username);
        return task.getUser().equals(user) || user.getRole() == User.Role.ADMIN;
    }

    /**
     * Task Statistics DTO
     */
    public static class TaskStatistics {
        private final long totalTasks;
        private final long todoTasks;
        private final long inProgressTasks;
        private final long completedTasks;
        private final long overdueTasks;

        public TaskStatistics(long totalTasks, long todoTasks, long inProgressTasks, 
                            long completedTasks, long overdueTasks) {
            this.totalTasks = totalTasks;
            this.todoTasks = todoTasks;
            this.inProgressTasks = inProgressTasks;
            this.completedTasks = completedTasks;
            this.overdueTasks = overdueTasks;
        }

        // Getters
        public long getTotalTasks() { return totalTasks; }
        public long getTodoTasks() { return todoTasks; }
        public long getInProgressTasks() { return inProgressTasks; }
        public long getCompletedTasks() { return completedTasks; }
        public long getOverdueTasks() { return overdueTasks; }
        
        public double getCompletionRate() {
            return totalTasks > 0 ? (double) completedTasks / totalTasks * 100 : 0;
        }
    }
}

