import { Component } from '@angular/core';
import { SelectionType } from '../selection-type';

@Component({
  selector: 'app-pharmacies-tabs',
  templateUrl: './pharmacies-tabs.component.html',
  styleUrls: ['./pharmacies-tabs.component.css']
})
export class PharmaciesTabsComponent {

  SelectionType = SelectionType;

  constructor() { }
}
