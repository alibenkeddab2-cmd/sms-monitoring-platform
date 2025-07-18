export interface SmsMessage {
  id?: number;
  messageId: string;
  operatorId?: number;
  senderNumber: string;
  recipientNumber: string;
  messageContent: string;
  status: SmsStatus;
  priority: SmsPriority;
  scheduledAt?: Date;
  sentAt?: Date;
  deliveredAt?: Date;
  createdAt?: Date;
  updatedAt?: Date;
}

export enum SmsStatus {
  PENDING = 'PENDING',
  SENT = 'SENT',
  DELIVERED = 'DELIVERED',
  FAILED = 'FAILED',
  EXPIRED = 'EXPIRED'
}

export enum SmsPriority {
  LOW = 'LOW',
  NORMAL = 'NORMAL',
  HIGH = 'HIGH',
  URGENT = 'URGENT'
}

export interface SmsStatistics {
  status: SmsStatus;
  count: number;
}

export interface OperatorStatistics {
  operatorId: number;
  status: SmsStatus;
  count: number;
}

export interface LoadTestResult {
  totalMessages: number;
  concurrentUsers: number;
  successCount: number;
  failureCount: number;
  averageLatency: number;
  totalDuration: number;
  throughput: number;
}

export interface StressTestResult {
  maxLoad: number;
  duration: number;
  totalMessages: number;
  successCount: number;
  failureCount: number;
  averageLatency: number;
  peakThroughput: number;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

