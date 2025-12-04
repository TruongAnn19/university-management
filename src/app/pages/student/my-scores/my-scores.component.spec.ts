import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyScoresComponent } from './my-scores.component';

describe('MyScoresComponent', () => {
  let component: MyScoresComponent;
  let fixture: ComponentFixture<MyScoresComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MyScoresComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(MyScoresComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
