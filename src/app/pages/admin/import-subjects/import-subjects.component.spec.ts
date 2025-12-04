import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImportSubjectsComponent } from './import-subjects.component';

describe('ImportSubjectsComponent', () => {
  let component: ImportSubjectsComponent;
  let fixture: ComponentFixture<ImportSubjectsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ImportSubjectsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ImportSubjectsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
