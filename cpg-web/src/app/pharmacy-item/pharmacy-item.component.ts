import { Component, Input, OnInit } from '@angular/core';
import { FlatPharmacy } from '../flat-pharmacy';

@Component({
  selector: 'app-pharmacy-item',
  templateUrl: './pharmacy-item.component.html',
  styleUrls: ['./pharmacy-item.component.css'],
})
export class PharmacyItemComponent implements OnInit {

  @Input() flatPharmacy: FlatPharmacy;

  constructor(
  ) { }

  ngOnInit(): void {
  }
}
