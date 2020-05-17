import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PharmaciesSelectedComponent } from './pharmacies-selected.component';

describe('PharmaciesSelectedComponent', () => {
  let component: PharmaciesSelectedComponent;
  let fixture: ComponentFixture<PharmaciesSelectedComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PharmaciesSelectedComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PharmaciesSelectedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
