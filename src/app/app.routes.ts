import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { MainLayoutComponent } from './components/layout/main-layout/main-layout.component';
import { MyScoresComponent } from './pages/student/my-scores/my-scores.component';
import { ImportStudentsComponent } from './pages/admin/import-students/import-students.component';
import { ImportTeachersComponent } from './pages/admin/import-teachers/import-teachers.component';
import { ImportScoresComponent } from './pages/teacher/import-scores/import-scores.component';
import { ImportSubjectsComponent } from './pages/admin/import-subjects/import-subjects.component';
import { ImportSemestersComponent } from './pages/admin/import-semesters/import-semesters.component';
import { CourseRegistrationComponent } from './pages/student/course-registration/course-registration.component';

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
            { path: 'import-students', component: ImportStudentsComponent },
            { path: 'import-teachers', component: ImportTeachersComponent },
            { path: 'import-scores', component: ImportScoresComponent },
            { path: 'import-subjects', component: ImportSubjectsComponent },
            { path: 'import-semesters', component: ImportSemestersComponent },
            { path: 'register-course', component: CourseRegistrationComponent },
        ]
    },
    { path: '**', redirectTo: 'login' }
];
