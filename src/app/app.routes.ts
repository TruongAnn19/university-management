import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { MainLayoutComponent } from './components/layout/main-layout/main-layout.component';
import { MyScoresComponent } from './pages/student/my-scores/my-scores.component';

export const routes: Routes = [
    { path: '', redirectTo: 'login', pathMatch: 'full' },
    { path: 'login', component: LoginComponent, title: 'Đăng nhập hệ thống' },
    {
        path: '',
        component: MainLayoutComponent,
        children: [
            { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
            { path: 'dashboard', component: DashboardComponent },
            { path: 'my-scores', component: MyScoresComponent },
        ]
    },
    { path: '**', redirectTo: 'login' }
];
