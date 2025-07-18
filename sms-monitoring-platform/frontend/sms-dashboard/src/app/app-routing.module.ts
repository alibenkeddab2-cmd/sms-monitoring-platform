import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { SmsListComponent } from './components/sms-list/sms-list.component';
import { SmsFormComponent } from './components/sms-form/sms-form.component';
import { StatisticsComponent } from './components/statistics/statistics.component';
import { NetworkTestComponent } from './components/network-test/network-test.component';

const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'sms-list', component: SmsListComponent },
  { path: 'sms-form', component: SmsFormComponent },
  { path: 'statistics', component: StatisticsComponent },
  { path: 'network-test', component: NetworkTestComponent },
  { path: '**', redirectTo: '/dashboard' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

