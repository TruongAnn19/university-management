import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageScoresComponent } from './manage-scores.component';

describe('ManageScoresComponent', () => {
  let component: ManageScoresComponent;
  let fixture: ComponentFixture<ManageScoresComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManageScoresComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ManageScoresComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
