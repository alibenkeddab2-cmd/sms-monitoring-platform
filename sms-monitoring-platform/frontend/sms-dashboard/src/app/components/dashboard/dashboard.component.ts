import { Component, OnInit } from '@angular/core';
import { SmsService } from '../../services/sms.service';
import { SmsMessage, SmsStatus } from '../../models/sms-message.model';
import { ChartConfiguration, ChartData, ChartType } from 'chart.js';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  
  // Statistics
  totalMessages = 0;
  pendingMessages = 0;
  sentMessages = 0;
  deliveredMessages = 0;
  failedMessages = 0;
  
  // Recent messages
  recentMessages: SmsMessage[] = [];
  
  // Chart data
  public statusChartData: ChartData<'doughnut'> = {
    labels: ['Delivered', 'Sent', 'Pending', 'Failed'],
    datasets: [{
      data: [0, 0, 0, 0],
      backgroundColor: ['#4CAF50', '#2196F3', '#FF9800', '#F44336']
    }]
  };
  
  public statusChartType: ChartType = 'doughnut';
  
  public statusChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    plugins: {
      legend: {
        position: 'bottom'
      }
    }
  };

  // Loading states
  loading = true;
  
  constructor(private smsService: SmsService) { }

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.loading = true;
    
    // Load recent messages
    this.smsService.getAllMessages(0, 10).subscribe({
      next: (response) => {
        this.recentMessages = response.content;
        this.totalMessages = response.totalElements;
        this.updateStatistics();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading dashboard data:', error);
        this.loading = false;
      }
    });
  }

  private updateStatistics(): void {
    // Count messages by status
    this.pendingMessages = this.recentMessages.filter(m => m.status === SmsStatus.PENDING).length;
    this.sentMessages = this.recentMessages.filter(m => m.status === SmsStatus.SENT).length;
    this.deliveredMessages = this.recentMessages.filter(m => m.status === SmsStatus.DELIVERED).length;
    this.failedMessages = this.recentMessages.filter(m => m.status === SmsStatus.FAILED).length;
    
    // Update chart data
    this.statusChartData = {
      labels: ['Delivered', 'Sent', 'Pending', 'Failed'],
      datasets: [{
        data: [this.deliveredMessages, this.sentMessages, this.pendingMessages, this.failedMessages],
        backgroundColor: ['#4CAF50', '#2196F3', '#FF9800', '#F44336']
      }]
    };
  }

  getStatusDisplayText(status: SmsStatus): string {
    return this.smsService.getStatusDisplayText(status);
  }

  getStatusColorClass(status: SmsStatus): string {
    return this.smsService.getStatusColorClass(status);
  }

  refreshData(): void {
    this.loadDashboardData();
  }
}

