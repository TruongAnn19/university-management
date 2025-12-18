import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManageAppealsComponent } from './manage-appeals.component';

describe('ManageAppealsComponent', () => {
  let component: ManageAppealsComponent;
  let fixture: ComponentFixture<ManageAppealsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ManageAppealsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ManageAppealsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
