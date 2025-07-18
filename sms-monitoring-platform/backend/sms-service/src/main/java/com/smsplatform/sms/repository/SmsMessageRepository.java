package com.smsplatform.sms.repository;

import com.smsplatform.sms.model.SmsMessage;
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
 * Repository interface for SMS Message operations
 * 
 * Provides data access methods for SMS message management
 * including CRUD operations and custom queries.
 */
@Repository
public interface SmsMessageRepository extends JpaRepository<SmsMessage, Long> {

    /**
     * Find SMS message by message ID
     */
    Optional<SmsMessage> findByMessageId(String messageId);

    /**
     * Find SMS messages by status
     */
    List<SmsMessage> findByStatus(SmsMessage.SmsStatus status);

    /**
     * Find SMS messages by operator ID
     */
    Page<SmsMessage> findByOperatorId(Long operatorId, Pageable pageable);

    /**
     * Find SMS messages by sender number
     */
    Page<SmsMessage> findBySenderNumber(String senderNumber, Pageable pageable);

    /**
     * Find SMS messages by recipient number
     */
    Page<SmsMessage> findByRecipientNumber(String recipientNumber, Pageable pageable);

    /**
     * Find SMS messages created within date range
     */
    @Query("SELECT s FROM SmsMessage s WHERE s.createdAt BETWEEN :startDate AND :endDate")
    List<SmsMessage> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);

    /**
     * Find SMS messages by status and operator
     */
    @Query("SELECT s FROM SmsMessage s WHERE s.status = :status AND s.operatorId = :operatorId")
    List<SmsMessage> findByStatusAndOperatorId(@Param("status") SmsMessage.SmsStatus status, 
                                              @Param("operatorId") Long operatorId);

    /**
     * Count messages by status
     */
    @Query("SELECT COUNT(s) FROM SmsMessage s WHERE s.status = :status")
    Long countByStatus(@Param("status") SmsMessage.SmsStatus status);

    /**
     * Count messages by operator and date range
     */
    @Query("SELECT COUNT(s) FROM SmsMessage s WHERE s.operatorId = :operatorId " +
           "AND s.createdAt BETWEEN :startDate AND :endDate")
    Long countByOperatorIdAndDateRange(@Param("operatorId") Long operatorId,
                                      @Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);

    /**
     * Find pending messages scheduled for sending
     */
    @Query("SELECT s FROM SmsMessage s WHERE s.status = 'PENDING' " +
           "AND (s.scheduledAt IS NULL OR s.scheduledAt <= :currentTime)")
    List<SmsMessage> findPendingMessagesForSending(@Param("currentTime") LocalDateTime currentTime);

    /**
     * Find messages by priority and status
     */
    @Query("SELECT s FROM SmsMessage s WHERE s.priority = :priority AND s.status = :status " +
           "ORDER BY s.createdAt ASC")
    List<SmsMessage> findByPriorityAndStatusOrderByCreatedAt(@Param("priority") SmsMessage.SmsPriority priority,
                                                            @Param("status") SmsMessage.SmsStatus status);

    /**
     * Get delivery statistics for a date range
     */
    @Query("SELECT s.status, COUNT(s) FROM SmsMessage s " +
           "WHERE s.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY s.status")
    List<Object[]> getDeliveryStatistics(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    /**
     * Get operator performance statistics
     */
    @Query("SELECT s.operatorId, s.status, COUNT(s) FROM SmsMessage s " +
           "WHERE s.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY s.operatorId, s.status")
    List<Object[]> getOperatorPerformanceStatistics(@Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate);
}

