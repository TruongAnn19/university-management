import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { MainLayoutComponent } from './components/layout/main-layout/main-layout.component';
import { authGuard } from '../app/core/auth.guard';
import { guestGuard } from './core/guest.guard';

// Student Pages
import { MyScoresComponent } from './pages/student/my-scores/my-scores.component';
import { CourseRegistrationComponent } from './pages/student/course-registration/course-registration.component';

// Admin/Teacher Pages
import { ImportStudentsComponent } from './pages/admin/import-students/import-students.component';
import { ImportTeachersComponent } from './pages/admin/import-teachers/import-teachers.component';
import { ImportScoresComponent } from './pages/teacher/import-scores/import-scores.component';
import { ManageScoresComponent } from './pages/teacher/manage-scores/manage-scores.component';
import { ImportSubjectsComponent } from './pages/admin/import-subjects/import-subjects.component';
import { ImportSemestersComponent } from './pages/admin/import-semesters/import-semesters.component';

// Shared Pages
import { UserProfileComponent } from './pages/users/user-profile/user-profile.component';
//Phúc khảo
import { ManageAppealsComponent } from './pages/teacher/manage-appeals/manage-appeals.component';

export const routes: Routes = [
    { path: '', redirectTo: 'login', pathMatch: 'full' },
    { path: 'login', component: LoginComponent, title: 'Đăng nhập hệ thống', canActivate: [guestGuard] },

    {
        path: '',
        component: MainLayoutComponent,
        canActivate: [authGuard], 
        children: [
            { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
            { path: 'dashboard', component: DashboardComponent, title: 'Trang chủ' },

            // --- Student Routes ---
            { path: 'my-scores', component: MyScoresComponent, title: 'Xem điểm cá nhân' },
            { path: 'register-course', component: CourseRegistrationComponent, title: 'Đăng ký tín chỉ' },

            // --- Admin/Teacher Routes ---
            // Nhập điểm thủ công
            { path: 'manage-scores', component: ManageScoresComponent, title: 'Quản lý điểm (Thủ công)' },
            { path: 'appeals', component: ManageAppealsComponent, title: 'Duyệt đơn phúc khảo' },

            //Các trang Import Excel
            { path: 'import-students', component: ImportStudentsComponent, title: 'Import Sinh viên' },
            { path: 'import-teachers', component: ImportTeachersComponent, title: 'Import Giảng viên' },
            { path: 'import-scores', component: ImportScoresComponent, title: 'Import Điểm (Excel)' },
            { path: 'import-subjects', component: ImportSubjectsComponent, title: 'Import Môn học' },
            { path: 'import-semesters', component: ImportSemestersComponent, title: 'Import Học kỳ' },

            // --- User Profile ---
            { path: 'profile/me', component: UserProfileComponent, title: 'Hồ sơ của tôi' },
            { path: 'profile/:id', component: UserProfileComponent, title: 'Chi tiết người dùng' },
        ]
    },

    { path: '**', redirectTo: 'login' }
];