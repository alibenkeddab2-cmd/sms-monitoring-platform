package com.smsplatform.sms.service;

import com.smsplatform.sms.model.SmsMessage;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Network Simulation Service
 * 
 * Simulates various network conditions and telecom operator behaviors
 * for testing and monitoring purposes.
 */
@Service
public class NetworkSimulationService {

    private final Random random = new Random();

    /**
     * Simulate message delivery with various network conditions
     */
    public boolean simulateMessageDelivery(SmsMessage message) {
        try {
            // Simulate network latency
            simulateNetworkLatency();

            // Simulate different success rates based on priority
            double successRate = getSuccessRateByPriority(message.getPriority());
            
            // Simulate operator-specific behavior
            successRate = adjustSuccessRateByOperator(successRate, message.getOperatorId());

            return random.nextDouble() < successRate;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * Simulate network latency based on realistic conditions
     */
    private void simulateNetworkLatency() throws InterruptedException {
        // Simulate realistic network delays (50ms to 2000ms)
        int latency = ThreadLocalRandom.current().nextInt(50, 2001);
        Thread.sleep(latency);
    }

    /**
     * Get success rate based on message priority
     */
    private double getSuccessRateByPriority(SmsMessage.SmsPriority priority) {
        switch (priority) {
            case URGENT:
                return 0.98; // 98% success rate for urgent messages
            case HIGH:
                return 0.95; // 95% success rate for high priority
            case NORMAL:
                return 0.90; // 90% success rate for normal priority
            case LOW:
                return 0.85; // 85% success rate for low priority
            default:
                return 0.90;
        }
    }

    /**
     * Adjust success rate based on operator performance characteristics
     */
    private double adjustSuccessRateByOperator(double baseSuccessRate, Long operatorId) {
        if (operatorId == null) {
            return baseSuccessRate;
        }

        // Simulate different operator performance characteristics
        switch (operatorId.intValue() % 3) {
            case 0:
                // High-performance operator
                return Math.min(baseSuccessRate + 0.05, 1.0);
            case 1:
                // Average operator
                return baseSuccessRate;
            case 2:
                // Lower-performance operator
                return Math.max(baseSuccessRate - 0.10, 0.5);
            default:
                return baseSuccessRate;
        }
    }

    /**
     * Simulate load testing scenario
     */
    public LoadTestResult simulateLoadTest(int messageCount, int concurrentUsers) {
        LoadTestResult result = new LoadTestResult();
        result.setTotalMessages(messageCount);
        result.setConcurrentUsers(concurrentUsers);
        
        long startTime = System.currentTimeMillis();
        
        int successCount = 0;
        int failureCount = 0;
        long totalLatency = 0;
        
        for (int i = 0; i < messageCount; i++) {
            long messageStartTime = System.currentTimeMillis();
            
            // Simulate message processing
            boolean success = random.nextDouble() < 0.92; // 92% base success rate
            
            if (success) {
                successCount++;
            } else {
                failureCount++;
            }
            
            // Simulate processing time
            int processingTime = ThreadLocalRandom.current().nextInt(100, 1000);
            totalLatency += processingTime;
            
            try {
                Thread.sleep(processingTime / 10); // Scaled down for simulation
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        long endTime = System.currentTimeMillis();
        
        result.setSuccessCount(successCount);
        result.setFailureCount(failureCount);
        result.setAverageLatency(totalLatency / messageCount);
        result.setTotalDuration(endTime - startTime);
        result.setThroughput((double) messageCount / ((endTime - startTime) / 1000.0));
        
        return result;
    }

    /**
     * Simulate stress testing scenario
     */
    public StressTestResult simulateStressTest(int maxLoad, int duration) {
        StressTestResult result = new StressTestResult();
        result.setMaxLoad(maxLoad);
        result.setDuration(duration);
        
        // Simulate gradual load increase
        int currentLoad = 0;
        int successCount = 0;
        int failureCount = 0;
        long totalLatency = 0;
        int totalMessages = 0;
        
        long startTime = System.currentTimeMillis();
        long endTime = startTime + (duration * 1000L);
        
        while (System.currentTimeMillis() < endTime) {
            // Gradually increase load
            if (currentLoad < maxLoad) {
                currentLoad += Math.max(1, maxLoad / 20); // Increase by 5% each iteration
            }
            
            // Process messages at current load
            for (int i = 0; i < Math.min(currentLoad, 100); i++) { // Cap at 100 for simulation
                totalMessages++;
                
                // Success rate decreases with higher load
                double successRate = Math.max(0.5, 1.0 - (currentLoad / (double) maxLoad) * 0.4);
                boolean success = random.nextDouble() < successRate;
                
                if (success) {
                    successCount++;
                } else {
                    failureCount++;
                }
                
                // Latency increases with load
                int latency = (int) (100 + (currentLoad / (double) maxLoad) * 2000);
                totalLatency += latency;
            }
            
            try {
                Thread.sleep(100); // Simulate time passage
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        result.setTotalMessages(totalMessages);
        result.setSuccessCount(successCount);
        result.setFailureCount(failureCount);
        result.setAverageLatency(totalMessages > 0 ? totalLatency / totalMessages : 0);
        result.setPeakThroughput((double) currentLoad);
        
        return result;
    }

    /**
     * Load Test Result DTO
     */
    public static class LoadTestResult {
        private int totalMessages;
        private int concurrentUsers;
        private int successCount;
        private int failureCount;
        private long averageLatency;
        private long totalDuration;
        private double throughput;

        // Getters and Setters
        public int getTotalMessages() { return totalMessages; }
        public void setTotalMessages(int totalMessages) { this.totalMessages = totalMessages; }
        
        public int getConcurrentUsers() { return concurrentUsers; }
        public void setConcurrentUsers(int concurrentUsers) { this.concurrentUsers = concurrentUsers; }
        
        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }
        
        public int getFailureCount() { return failureCount; }
        public void setFailureCount(int failureCount) { this.failureCount = failureCount; }
        
        public long getAverageLatency() { return averageLatency; }
        public void setAverageLatency(long averageLatency) { this.averageLatency = averageLatency; }
        
        public long getTotalDuration() { return totalDuration; }
        public void setTotalDuration(long totalDuration) { this.totalDuration = totalDuration; }
        
        public double getThroughput() { return throughput; }
        public void setThroughput(double throughput) { this.throughput = throughput; }
    }

    /**
     * Stress Test Result DTO
     */
    public static class StressTestResult {
        private int maxLoad;
        private int duration;
        private int totalMessages;
        private int successCount;
        private int failureCount;
        private long averageLatency;
        private double peakThroughput;

        // Getters and Setters
        public int getMaxLoad() { return maxLoad; }
        public void setMaxLoad(int maxLoad) { this.maxLoad = maxLoad; }
        
        public int getDuration() { return duration; }
        public void setDuration(int duration) { this.duration = duration; }
        
        public int getTotalMessages() { return totalMessages; }
        public void setTotalMessages(int totalMessages) { this.totalMessages = totalMessages; }
        
        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }
        
        public int getFailureCount() { return failureCount; }
        public void setFailureCount(int failureCount) { this.failureCount = failureCount; }
        
        public long getAverageLatency() { return averageLatency; }
        public void setAverageLatency(long averageLatency) { this.averageLatency = averageLatency; }
        
        public double getPeakThroughput() { return peakThroughput; }
        public void setPeakThroughput(double peakThroughput) { this.peakThroughput = peakThroughput; }
    }
}

