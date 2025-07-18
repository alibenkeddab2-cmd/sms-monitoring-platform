package com.smsplatform.sms.service;

import com.smsplatform.sms.dto.SmsMessageDto;
import com.smsplatform.sms.model.SmsMessage;
import com.smsplatform.sms.repository.SmsMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * SMS Message Service
 * 
 * Provides business logic for SMS message operations including
 * creation, processing, status updates, and monitoring.
 */
@Service
@Transactional
public class SmsMessageService {

    private final SmsMessageRepository smsMessageRepository;
    private final NetworkSimulationService networkSimulationService;

    @Autowired
    public SmsMessageService(SmsMessageRepository smsMessageRepository,
                           NetworkSimulationService networkSimulationService) {
        this.smsMessageRepository = smsMessageRepository;
        this.networkSimulationService = networkSimulationService;
    }

    /**
     * Create a new SMS message
     */
    public SmsMessageDto createMessage(SmsMessageDto messageDto) {
        // Generate unique message ID if not provided
        if (messageDto.getMessageId() == null || messageDto.getMessageId().isEmpty()) {
            messageDto.setMessageId(generateMessageId());
        }

        // Set default values
        if (messageDto.getStatus() == null) {
            messageDto.setStatus(SmsMessage.SmsStatus.PENDING);
        }
        if (messageDto.getPriority() == null) {
            messageDto.setPriority(SmsMessage.SmsPriority.NORMAL);
        }

        SmsMessage entity = messageDto.toEntity();
        SmsMessage savedEntity = smsMessageRepository.save(entity);

        // Trigger async processing
        processMessageAsync(savedEntity.getId());

        return SmsMessageDto.fromEntity(savedEntity);
    }

    /**
     * Get SMS message by ID
     */
    @Transactional(readOnly = true)
    public Optional<SmsMessageDto> getMessageById(Long id) {
        return smsMessageRepository.findById(id)
                .map(SmsMessageDto::fromEntity);
    }

    /**
     * Get SMS message by message ID
     */
    @Transactional(readOnly = true)
    public Optional<SmsMessageDto> getMessageByMessageId(String messageId) {
        return smsMessageRepository.findByMessageId(messageId)
                .map(SmsMessageDto::fromEntity);
    }

    /**
     * Get all SMS messages with pagination
     */
    @Transactional(readOnly = true)
    public Page<SmsMessageDto> getAllMessages(Pageable pageable) {
        return smsMessageRepository.findAll(pageable)
                .map(SmsMessageDto::fromEntity);
    }

    /**
     * Get SMS messages by operator
     */
    @Transactional(readOnly = true)
    public Page<SmsMessageDto> getMessagesByOperator(Long operatorId, Pageable pageable) {
        return smsMessageRepository.findByOperatorId(operatorId, pageable)
                .map(SmsMessageDto::fromEntity);
    }

    /**
     * Get SMS messages by status
     */
    @Transactional(readOnly = true)
    public List<SmsMessageDto> getMessagesByStatus(SmsMessage.SmsStatus status) {
        return smsMessageRepository.findByStatus(status)
                .stream()
                .map(SmsMessageDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Update message status
     */
    public SmsMessageDto updateMessageStatus(Long id, SmsMessage.SmsStatus status) {
        Optional<SmsMessage> messageOpt = smsMessageRepository.findById(id);
        if (messageOpt.isPresent()) {
            SmsMessage message = messageOpt.get();
            message.setStatus(status);

            // Set timestamps based on status
            LocalDateTime now = LocalDateTime.now();
            switch (status) {
                case SENT:
                    message.setSentAt(now);
                    break;
                case DELIVERED:
                    message.setDeliveredAt(now);
                    break;
                case FAILED:
                case EXPIRED:
                    // Keep existing timestamps
                    break;
            }

            SmsMessage updatedMessage = smsMessageRepository.save(message);
            return SmsMessageDto.fromEntity(updatedMessage);
        }
        throw new RuntimeException("SMS message not found with id: " + id);
    }

    /**
     * Delete SMS message
     */
    public void deleteMessage(Long id) {
        if (!smsMessageRepository.existsById(id)) {
            throw new RuntimeException("SMS message not found with id: " + id);
        }
        smsMessageRepository.deleteById(id);
    }

    /**
     * Get delivery statistics for date range
     */
    @Transactional(readOnly = true)
    public List<Object[]> getDeliveryStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        return smsMessageRepository.getDeliveryStatistics(startDate, endDate);
    }

    /**
     * Get operator performance statistics
     */
    @Transactional(readOnly = true)
    public List<Object[]> getOperatorPerformanceStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        return smsMessageRepository.getOperatorPerformanceStatistics(startDate, endDate);
    }

    /**
     * Process pending messages asynchronously
     */
    @Async
    public CompletableFuture<Void> processMessageAsync(Long messageId) {
        try {
            Optional<SmsMessage> messageOpt = smsMessageRepository.findById(messageId);
            if (messageOpt.isPresent()) {
                SmsMessage message = messageOpt.get();
                
                // Simulate network processing
                boolean success = networkSimulationService.simulateMessageDelivery(message);
                
                if (success) {
                    updateMessageStatus(messageId, SmsMessage.SmsStatus.SENT);
                    
                    // Simulate delivery confirmation after delay
                    Thread.sleep(2000);
                    updateMessageStatus(messageId, SmsMessage.SmsStatus.DELIVERED);
                } else {
                    updateMessageStatus(messageId, SmsMessage.SmsStatus.FAILED);
                }
            }
        } catch (Exception e) {
            // Log error and update status
            updateMessageStatus(messageId, SmsMessage.SmsStatus.FAILED);
        }
        
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Scheduled task to process pending messages
     */
    @Scheduled(fixedDelay = 30000) // Run every 30 seconds
    public void processPendingMessages() {
        LocalDateTime now = LocalDateTime.now();
        List<SmsMessage> pendingMessages = smsMessageRepository.findPendingMessagesForSending(now);
        
        for (SmsMessage message : pendingMessages) {
            processMessageAsync(message.getId());
        }
    }

    /**
     * Generate unique message ID
     */
    private String generateMessageId() {
        return "SMS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

