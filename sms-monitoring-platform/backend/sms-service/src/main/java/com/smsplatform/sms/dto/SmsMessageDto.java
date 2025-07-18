package com.smsplatform.sms.dto;

import com.smsplatform.sms.model.SmsMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for SMS Message
 * 
 * Used for API requests and responses to transfer SMS message data
 * between client and server.
 */
public class SmsMessageDto {

    private Long id;

    @NotBlank(message = "Message ID is required")
    private String messageId;

    private Long operatorId;

    @NotBlank(message = "Sender number is required")
    @Size(max = 20, message = "Sender number must not exceed 20 characters")
    private String senderNumber;

    @NotBlank(message = "Recipient number is required")
    @Size(max = 20, message = "Recipient number must not exceed 20 characters")
    private String recipientNumber;

    @NotBlank(message = "Message content is required")
    private String messageContent;

    @NotNull(message = "Status is required")
    private SmsMessage.SmsStatus status;

    @NotNull(message = "Priority is required")
    private SmsMessage.SmsPriority priority;

    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public SmsMessageDto() {}

    public SmsMessageDto(String messageId, String senderNumber, String recipientNumber, String messageContent) {
        this.messageId = messageId;
        this.senderNumber = senderNumber;
        this.recipientNumber = recipientNumber;
        this.messageContent = messageContent;
        this.status = SmsMessage.SmsStatus.PENDING;
        this.priority = SmsMessage.SmsPriority.NORMAL;
    }

    // Static factory method to create DTO from entity
    public static SmsMessageDto fromEntity(SmsMessage entity) {
        SmsMessageDto dto = new SmsMessageDto();
        dto.setId(entity.getId());
        dto.setMessageId(entity.getMessageId());
        dto.setOperatorId(entity.getOperatorId());
        dto.setSenderNumber(entity.getSenderNumber());
        dto.setRecipientNumber(entity.getRecipientNumber());
        dto.setMessageContent(entity.getMessageContent());
        dto.setStatus(entity.getStatus());
        dto.setPriority(entity.getPriority());
        dto.setScheduledAt(entity.getScheduledAt());
        dto.setSentAt(entity.getSentAt());
        dto.setDeliveredAt(entity.getDeliveredAt());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    // Method to convert DTO to entity
    public SmsMessage toEntity() {
        SmsMessage entity = new SmsMessage();
        entity.setId(this.id);
        entity.setMessageId(this.messageId);
        entity.setOperatorId(this.operatorId);
        entity.setSenderNumber(this.senderNumber);
        entity.setRecipientNumber(this.recipientNumber);
        entity.setMessageContent(this.messageContent);
        entity.setStatus(this.status != null ? this.status : SmsMessage.SmsStatus.PENDING);
        entity.setPriority(this.priority != null ? this.priority : SmsMessage.SmsPriority.NORMAL);
        entity.setScheduledAt(this.scheduledAt);
        entity.setSentAt(this.sentAt);
        entity.setDeliveredAt(this.deliveredAt);
        return entity;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getSenderNumber() {
        return senderNumber;
    }

    public void setSenderNumber(String senderNumber) {
        this.senderNumber = senderNumber;
    }

    public String getRecipientNumber() {
        return recipientNumber;
    }

    public void setRecipientNumber(String recipientNumber) {
        this.recipientNumber = recipientNumber;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public SmsMessage.SmsStatus getStatus() {
        return status;
    }

    public void setStatus(SmsMessage.SmsStatus status) {
        this.status = status;
    }

    public SmsMessage.SmsPriority getPriority() {
        return priority;
    }

    public void setPriority(SmsMessage.SmsPriority priority) {
        this.priority = priority;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

