import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { 
  SmsMessage, 
  SmsStatus, 
  SmsStatistics, 
  OperatorStatistics, 
  PageResponse 
} from '../models/sms-message.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SmsService {
  private apiUrl = `${environment.apiUrl}/api/v1/sms`;

  constructor(private http: HttpClient) { }

  /**
   * Create a new SMS message
   */
  createMessage(message: SmsMessage): Observable<SmsMessage> {
    return this.http.post<SmsMessage>(`${this.apiUrl}/messages`, message);
  }

  /**
   * Get SMS message by ID
   */
  getMessageById(id: number): Observable<SmsMessage> {
    return this.http.get<SmsMessage>(`${this.apiUrl}/messages/${id}`);
  }

  /**
   * Get SMS message by message ID
   */
  getMessageByMessageId(messageId: string): Observable<SmsMessage> {
    return this.http.get<SmsMessage>(`${this.apiUrl}/messages/by-message-id/${messageId}`);
  }

  /**
   * Get all SMS messages with pagination
   */
  getAllMessages(page: number = 0, size: number = 20, sortBy: string = 'createdAt', sortDir: string = 'desc'): Observable<PageResponse<SmsMessage>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDir', sortDir);

    return this.http.get<PageResponse<SmsMessage>>(`${this.apiUrl}/messages`, { params });
  }

  /**
   * Get SMS messages by operator
   */
  getMessagesByOperator(operatorId: number, page: number = 0, size: number = 20): Observable<PageResponse<SmsMessage>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<SmsMessage>>(`${this.apiUrl}/messages/operator/${operatorId}`, { params });
  }

  /**
   * Get SMS messages by status
   */
  getMessagesByStatus(status: SmsStatus): Observable<SmsMessage[]> {
    return this.http.get<SmsMessage[]>(`${this.apiUrl}/messages/status/${status}`);
  }

  /**
   * Update message status
   */
  updateMessageStatus(id: number, status: SmsStatus): Observable<SmsMessage> {
    const params = new HttpParams().set('status', status);
    return this.http.put<SmsMessage>(`${this.apiUrl}/messages/${id}/status`, null, { params });
  }

  /**
   * Delete SMS message
   */
  deleteMessage(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/messages/${id}`);
  }

  /**
   * Get delivery statistics
   */
  getDeliveryStatistics(startDate: Date, endDate: Date): Observable<SmsStatistics[]> {
    const params = new HttpParams()
      .set('startDate', startDate.toISOString())
      .set('endDate', endDate.toISOString());

    return this.http.get<SmsStatistics[]>(`${this.apiUrl}/statistics/delivery`, { params });
  }

  /**
   * Get operator performance statistics
   */
  getOperatorStatistics(startDate: Date, endDate: Date): Observable<OperatorStatistics[]> {
    const params = new HttpParams()
      .set('startDate', startDate.toISOString())
      .set('endDate', endDate.toISOString());

    return this.http.get<OperatorStatistics[]>(`${this.apiUrl}/statistics/operators`, { params });
  }

  /**
   * Health check
   */
  healthCheck(): Observable<string> {
    return this.http.get(`${this.apiUrl}/health`, { responseType: 'text' });
  }

  /**
   * Get status display text
   */
  getStatusDisplayText(status: SmsStatus): string {
    switch (status) {
      case SmsStatus.PENDING:
        return 'Pending';
      case SmsStatus.SENT:
        return 'Sent';
      case SmsStatus.DELIVERED:
        return 'Delivered';
      case SmsStatus.FAILED:
        return 'Failed';
      case SmsStatus.EXPIRED:
        return 'Expired';
      default:
        return status;
    }
  }

  /**
   * Get status color class
   */
  getStatusColorClass(status: SmsStatus): string {
    switch (status) {
      case SmsStatus.PENDING:
        return 'status-pending';
      case SmsStatus.SENT:
        return 'status-sent';
      case SmsStatus.DELIVERED:
        return 'status-delivered';
      case SmsStatus.FAILED:
        return 'status-failed';
      case SmsStatus.EXPIRED:
        return 'status-expired';
      default:
        return '';
    }
  }

  /**
   * Get priority display text
   */
  getPriorityDisplayText(priority: string): string {
    switch (priority) {
      case 'LOW':
        return 'Low';
      case 'NORMAL':
        return 'Normal';
      case 'HIGH':
        return 'High';
      case 'URGENT':
        return 'Urgent';
      default:
        return priority;
    }
  }
}

