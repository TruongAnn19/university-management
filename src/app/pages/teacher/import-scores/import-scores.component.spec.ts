import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImportScoresComponent } from './import-scores.component';

describe('ImportScoresComponent', () => {
  let component: ImportScoresComponent;
  let fixture: ComponentFixture<ImportScoresComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ImportScoresComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ImportScoresComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
