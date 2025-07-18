package com.smsplatform.sms.controller;

import com.smsplatform.sms.dto.SmsMessageDto;
import com.smsplatform.sms.model.SmsMessage;
import com.smsplatform.sms.service.SmsMessageService;
import com.smsplatform.sms.service.NetworkSimulationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * SMS Message REST Controller
 * 
 * Provides REST API endpoints for SMS message management
 * including CRUD operations, status updates, and monitoring.
 */
@RestController
@RequestMapping("/api/v1/sms")
@Tag(name = "SMS Messages", description = "SMS message management operations")
@CrossOrigin(origins = "*")
public class SmsMessageController {

    private final SmsMessageService smsMessageService;
    private final NetworkSimulationService networkSimulationService;

    @Autowired
    public SmsMessageController(SmsMessageService smsMessageService,
                              NetworkSimulationService networkSimulationService) {
        this.smsMessageService = smsMessageService;
        this.networkSimulationService = networkSimulationService;
    }

    /**
     * Create a new SMS message
     */
    @PostMapping("/messages")
    @Operation(summary = "Create SMS message", description = "Create a new SMS message for processing")
    public ResponseEntity<SmsMessageDto> createMessage(@Valid @RequestBody SmsMessageDto messageDto) {
        try {
            SmsMessageDto createdMessage = smsMessageService.createMessage(messageDto);
            return new ResponseEntity<>(createdMessage, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get SMS message by ID
     */
    @GetMapping("/messages/{id}")
    @Operation(summary = "Get SMS message", description = "Retrieve SMS message by ID")
    public ResponseEntity<SmsMessageDto> getMessageById(
            @Parameter(description = "Message ID") @PathVariable Long id) {
        Optional<SmsMessageDto> message = smsMessageService.getMessageById(id);
        return message.map(dto -> ResponseEntity.ok(dto))
                     .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get SMS message by message ID
     */
    @GetMapping("/messages/by-message-id/{messageId}")
    @Operation(summary = "Get SMS message by message ID", description = "Retrieve SMS message by unique message ID")
    public ResponseEntity<SmsMessageDto> getMessageByMessageId(
            @Parameter(description = "Unique message ID") @PathVariable String messageId) {
        Optional<SmsMessageDto> message = smsMessageService.getMessageByMessageId(messageId);
        return message.map(dto -> ResponseEntity.ok(dto))
                     .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all SMS messages with pagination
     */
    @GetMapping("/messages")
    @Operation(summary = "Get all SMS messages", description = "Retrieve all SMS messages with pagination")
    public ResponseEntity<Page<SmsMessageDto>> getAllMessages(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<SmsMessageDto> messages = smsMessageService.getAllMessages(pageable);
        return ResponseEntity.ok(messages);
    }

    /**
     * Get SMS messages by operator
     */
    @GetMapping("/messages/operator/{operatorId}")
    @Operation(summary = "Get messages by operator", description = "Retrieve SMS messages for specific operator")
    public ResponseEntity<Page<SmsMessageDto>> getMessagesByOperator(
            @Parameter(description = "Operator ID") @PathVariable Long operatorId,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<SmsMessageDto> messages = smsMessageService.getMessagesByOperator(operatorId, pageable);
        return ResponseEntity.ok(messages);
    }

    /**
     * Get SMS messages by status
     */
    @GetMapping("/messages/status/{status}")
    @Operation(summary = "Get messages by status", description = "Retrieve SMS messages by status")
    public ResponseEntity<List<SmsMessageDto>> getMessagesByStatus(
            @Parameter(description = "Message status") @PathVariable SmsMessage.SmsStatus status) {
        List<SmsMessageDto> messages = smsMessageService.getMessagesByStatus(status);
        return ResponseEntity.ok(messages);
    }

    /**
     * Update message status
     */
    @PutMapping("/messages/{id}/status")
    @Operation(summary = "Update message status", description = "Update the status of an SMS message")
    public ResponseEntity<SmsMessageDto> updateMessageStatus(
            @Parameter(description = "Message ID") @PathVariable Long id,
            @Parameter(description = "New status") @RequestParam SmsMessage.SmsStatus status) {
        try {
            SmsMessageDto updatedMessage = smsMessageService.updateMessageStatus(id, status);
            return ResponseEntity.ok(updatedMessage);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete SMS message
     */
    @DeleteMapping("/messages/{id}")
    @Operation(summary = "Delete SMS message", description = "Delete an SMS message")
    public ResponseEntity<Void> deleteMessage(
            @Parameter(description = "Message ID") @PathVariable Long id) {
        try {
            smsMessageService.deleteMessage(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get delivery statistics
     */
    @GetMapping("/statistics/delivery")
    @Operation(summary = "Get delivery statistics", description = "Get SMS delivery statistics for date range")
    public ResponseEntity<List<Object[]>> getDeliveryStatistics(
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Object[]> statistics = smsMessageService.getDeliveryStatistics(startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get operator performance statistics
     */
    @GetMapping("/statistics/operators")
    @Operation(summary = "Get operator statistics", description = "Get operator performance statistics")
    public ResponseEntity<List<Object[]>> getOperatorStatistics(
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Object[]> statistics = smsMessageService.getOperatorPerformanceStatistics(startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Run load test simulation
     */
    @PostMapping("/simulation/load-test")
    @Operation(summary = "Run load test", description = "Execute load test simulation")
    public ResponseEntity<NetworkSimulationService.LoadTestResult> runLoadTest(
            @Parameter(description = "Number of messages") @RequestParam(defaultValue = "1000") int messageCount,
            @Parameter(description = "Concurrent users") @RequestParam(defaultValue = "10") int concurrentUsers) {
        NetworkSimulationService.LoadTestResult result = 
            networkSimulationService.simulateLoadTest(messageCount, concurrentUsers);
        return ResponseEntity.ok(result);
    }

    /**
     * Run stress test simulation
     */
    @PostMapping("/simulation/stress-test")
    @Operation(summary = "Run stress test", description = "Execute stress test simulation")
    public ResponseEntity<NetworkSimulationService.StressTestResult> runStressTest(
            @Parameter(description = "Maximum load") @RequestParam(defaultValue = "5000") int maxLoad,
            @Parameter(description = "Duration in seconds") @RequestParam(defaultValue = "60") int duration) {
        NetworkSimulationService.StressTestResult result = 
            networkSimulationService.simulateStressTest(maxLoad, duration);
        return ResponseEntity.ok(result);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check service health status")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("SMS Service is running");
    }
}

