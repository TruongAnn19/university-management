import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from '../header/header.component';
import { SidebarComponent } from '../sidebar/sidebar.component';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [RouterOutlet, HeaderComponent, SidebarComponent],
  template: `
    <div class="app-wrapper">
      <app-sidebar class="sidebar"></app-sidebar>
      <div class="main-content">
        <app-header></app-header>
        <div class="content-body">
          <router-outlet></router-outlet>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .app-wrapper { display: flex; height: 100vh; }
    .sidebar { width: 250px; background: #2c3e50; color: white; }
    .main-content { flex: 1; display: flex; flex-direction: column; }
    .content-body { padding: 20px; overflow-y: auto; }
  `]
})
export class MainLayoutComponent {}