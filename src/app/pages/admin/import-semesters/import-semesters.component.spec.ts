import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImportSemestersComponent } from './import-semesters.component';

describe('ImportSemestersComponent', () => {
  let component: ImportSemestersComponent;
  let fixture: ComponentFixture<ImportSemestersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ImportSemestersComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ImportSemestersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
