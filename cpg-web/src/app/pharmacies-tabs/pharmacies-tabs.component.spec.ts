import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PharmaciesTabsComponent } from './pharmacies-tabs.component';

describe('PharmaciesTabsComponent', () => {
  let component: PharmaciesTabsComponent;
  let fixture: ComponentFixture<PharmaciesTabsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PharmaciesTabsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PharmaciesTabsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
