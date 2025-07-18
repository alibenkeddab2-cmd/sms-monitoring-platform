package com.smsplatform.sms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * SMS Message Entity
 * 
 * Represents an SMS message in the system with all relevant metadata
 * including delivery status, timestamps, and operator information.
 */
@Entity
@Table(name = "sms_messages")
@EntityListeners(AuditingEntityListener.class)
public class SmsMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", unique = true, nullable = false)
    @NotBlank(message = "Message ID is required")
    private String messageId;

    @Column(name = "operator_id")
    private Long operatorId;

    @Column(name = "sender_number", nullable = false)
    @NotBlank(message = "Sender number is required")
    @Size(max = 20, message = "Sender number must not exceed 20 characters")
    private String senderNumber;

    @Column(name = "recipient_number", nullable = false)
    @NotBlank(message = "Recipient number is required")
    @Size(max = 20, message = "Recipient number must not exceed 20 characters")
    private String recipientNumber;

    @Column(name = "message_content", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Message content is required")
    private String messageContent;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @NotNull(message = "Status is required")
    private SmsStatus status = SmsStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    @NotNull(message = "Priority is required")
    private SmsPriority priority = SmsPriority.NORMAL;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public SmsMessage() {}

    public SmsMessage(String messageId, String senderNumber, String recipientNumber, String messageContent) {
        this.messageId = messageId;
        this.senderNumber = senderNumber;
        this.recipientNumber = recipientNumber;
        this.messageContent = messageContent;
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

    public SmsStatus getStatus() {
        return status;
    }

    public void setStatus(SmsStatus status) {
        this.status = status;
    }

    public SmsPriority getPriority() {
        return priority;
    }

    public void setPriority(SmsPriority priority) {
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

    /**
     * SMS Status Enumeration
     */
    public enum SmsStatus {
        PENDING,
        SENT,
        DELIVERED,
        FAILED,
        EXPIRED
    }

    /**
     * SMS Priority Enumeration
     */
    public enum SmsPriority {
        LOW,
        NORMAL,
        HIGH,
        URGENT
    }
}

