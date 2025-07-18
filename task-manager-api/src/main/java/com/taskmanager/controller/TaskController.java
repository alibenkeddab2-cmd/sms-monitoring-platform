package com.taskmanager.controller;

import com.taskmanager.dto.TaskDto;
import com.taskmanager.model.Task;
import com.taskmanager.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Task Controller
 * 
 * Handles task management endpoints including CRUD operations,
 * status updates, and task queries.
 */
@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Task management operations")
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired
    private TaskService taskService;

    /**
     * Create a new task
     */
    @PostMapping
    @Operation(summary = "Create task", description = "Create a new task")
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody TaskDto taskDto, 
                                            Authentication authentication) {
        TaskDto createdTask = taskService.createTask(taskDto, authentication.getName());
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    /**
     * Get task by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get task", description = "Retrieve task by ID")
    public ResponseEntity<TaskDto> getTaskById(@Parameter(description = "Task ID") @PathVariable Long id,
                                             Authentication authentication) {
        TaskDto task = taskService.getTaskById(id, authentication.getName());
        return ResponseEntity.ok(task);
    }

    /**
     * Get current user's tasks
     */
    @GetMapping
    @Operation(summary = "Get user tasks", description = "Retrieve current user's tasks with pagination")
    public ResponseEntity<Page<TaskDto>> getUserTasks(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir,
            Authentication authentication) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<TaskDto> tasks = taskService.getUserTasks(authentication.getName(), pageable);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get all tasks (Admin only)
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all tasks", description = "Retrieve all tasks with pagination (Admin only)")
    public ResponseEntity<Page<TaskDto>> getAllTasks(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<TaskDto> tasks = taskService.getAllTasks(pageable);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks by user ID (Admin only)
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get tasks by user", description = "Retrieve tasks for specific user (Admin only)")
    public ResponseEntity<Page<TaskDto>> getTasksByUserId(
            @Parameter(description = "User ID") @PathVariable Long userId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TaskDto> tasks = taskService.getTasksByUserId(userId, pageable);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks by status
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Get tasks by status", description = "Retrieve current user's tasks by status")
    public ResponseEntity<List<TaskDto>> getTasksByStatus(
            @Parameter(description = "Task status") @PathVariable Task.TaskStatus status,
            Authentication authentication) {
        List<TaskDto> tasks = taskService.getTasksByStatus(status, authentication.getName());
        return ResponseEntity.ok(tasks);
    }

    /**
     * Search tasks
     */
    @GetMapping("/search")
    @Operation(summary = "Search tasks", description = "Search current user's tasks by title or description")
    public ResponseEntity<Page<TaskDto>> searchTasks(
            @Parameter(description = "Search term") @RequestParam String q,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TaskDto> tasks = taskService.searchTasks(q, authentication.getName(), pageable);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get overdue tasks
     */
    @GetMapping("/overdue")
    @Operation(summary = "Get overdue tasks", description = "Retrieve current user's overdue tasks")
    public ResponseEntity<List<TaskDto>> getOverdueTasks(Authentication authentication) {
        List<TaskDto> tasks = taskService.getOverdueTasks(authentication.getName());
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get tasks due soon
     */
    @GetMapping("/due-soon")
    @Operation(summary = "Get tasks due soon", description = "Retrieve current user's tasks due within specified hours")
    public ResponseEntity<List<TaskDto>> getTasksDueSoon(
            @Parameter(description = "Hours ahead to check") @RequestParam(defaultValue = "24") int hours,
            Authentication authentication) {
        List<TaskDto> tasks = taskService.getTasksDueSoon(authentication.getName(), hours);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get recently completed tasks
     */
    @GetMapping("/completed")
    @Operation(summary = "Get completed tasks", description = "Retrieve current user's recently completed tasks")
    public ResponseEntity<List<TaskDto>> getRecentlyCompletedTasks(
            @Parameter(description = "Days back to check") @RequestParam(defaultValue = "7") int days,
            @Parameter(description = "Maximum number of tasks") @RequestParam(defaultValue = "10") int limit,
            Authentication authentication) {
        
        Pageable pageable = PageRequest.of(0, limit);
        List<TaskDto> tasks = taskService.getRecentlyCompletedTasks(authentication.getName(), days, pageable);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Update task
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update task", description = "Update an existing task")
    public ResponseEntity<TaskDto> updateTask(@Parameter(description = "Task ID") @PathVariable Long id,
                                            @Valid @RequestBody TaskDto taskDto,
                                            Authentication authentication) {
        TaskDto updatedTask = taskService.updateTask(id, taskDto, authentication.getName());
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Update task status
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update task status", description = "Update the status of a task")
    public ResponseEntity<TaskDto> updateTaskStatus(
            @Parameter(description = "Task ID") @PathVariable Long id,
            @Parameter(description = "New status") @RequestParam Task.TaskStatus status,
            Authentication authentication) {
        TaskDto updatedTask = taskService.updateTaskStatus(id, status, authentication.getName());
        return ResponseEntity.ok(updatedTask);
    }

    /**
     * Delete task
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task", description = "Delete a task")
    public ResponseEntity<Void> deleteTask(@Parameter(description = "Task ID") @PathVariable Long id,
                                         Authentication authentication) {
        taskService.deleteTask(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    /**
     * Get user task statistics
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get task statistics", description = "Get current user's task statistics")
    public ResponseEntity<TaskService.TaskStatistics> getUserTaskStatistics(Authentication authentication) {
        TaskService.TaskStatistics statistics = taskService.getUserTaskStatistics(authentication.getName());
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get overall task statistics (Admin only)
     */
    @GetMapping("/statistics/overall")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get overall statistics", description = "Get overall task statistics (Admin only)")
    public ResponseEntity<TaskService.TaskStatistics> getOverallTaskStatistics() {
        TaskService.TaskStatistics statistics = taskService.getOverallTaskStatistics();
        return ResponseEntity.ok(statistics);
    }
}

