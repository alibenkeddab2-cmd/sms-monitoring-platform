import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoadTestResult, StressTestResult } from '../models/sms-message.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class NetworkTestService {
  private apiUrl = `${environment.apiUrl}/api/v1/sms/simulation`;

  constructor(private http: HttpClient) { }

  /**
   * Run load test simulation
   */
  runLoadTest(messageCount: number = 1000, concurrentUsers: number = 10): Observable<LoadTestResult> {
    const params = new HttpParams()
      .set('messageCount', messageCount.toString())
      .set('concurrentUsers', concurrentUsers.toString());

    return this.http.post<LoadTestResult>(`${this.apiUrl}/load-test`, null, { params });
  }

  /**
   * Run stress test simulation
   */
  runStressTest(maxLoad: number = 5000, duration: number = 60): Observable<StressTestResult> {
    const params = new HttpParams()
      .set('maxLoad', maxLoad.toString())
      .set('duration', duration.toString());

    return this.http.post<StressTestResult>(`${this.apiUrl}/stress-test`, null, { params });
  }

  /**
   * Format test duration
   */
  formatDuration(milliseconds: number): string {
    const seconds = Math.floor(milliseconds / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);

    if (hours > 0) {
      return `${hours}h ${minutes % 60}m ${seconds % 60}s`;
    } else if (minutes > 0) {
      return `${minutes}m ${seconds % 60}s`;
    } else {
      return `${seconds}s`;
    }
  }

  /**
   * Format throughput
   */
  formatThroughput(throughput: number): string {
    return `${throughput.toFixed(2)} msg/s`;
  }

  /**
   * Format latency
   */
  formatLatency(latency: number): string {
    return `${latency.toFixed(0)}ms`;
  }

  /**
   * Calculate success rate
   */
  calculateSuccessRate(successCount: number, totalCount: number): number {
    return totalCount > 0 ? (successCount / totalCount) * 100 : 0;
  }

  /**
   * Get success rate color class
   */
  getSuccessRateColorClass(successRate: number): string {
    if (successRate >= 95) {
      return 'success-rate-excellent';
    } else if (successRate >= 90) {
      return 'success-rate-good';
    } else if (successRate >= 80) {
      return 'success-rate-fair';
    } else {
      return 'success-rate-poor';
    }
  }
}

