import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'SMS Monitoring Platform';
  
  menuItems = [
    { path: '/dashboard', icon: 'dashboard', label: 'Dashboard' },
    { path: '/sms-list', icon: 'message', label: 'SMS Messages' },
    { path: '/sms-form', icon: 'add', label: 'Send SMS' },
    { path: '/statistics', icon: 'analytics', label: 'Statistics' },
    { path: '/network-test', icon: 'network_check', label: 'Network Tests' }
  ];
}

