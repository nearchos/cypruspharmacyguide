import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PharmaciesAllComponent } from './pharmacies-all.component';

describe('PharmaciesAllComponent', () => {
  let component: PharmaciesAllComponent;
  let fixture: ComponentFixture<PharmaciesAllComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PharmaciesAllComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PharmaciesAllComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
