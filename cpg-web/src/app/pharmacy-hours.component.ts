import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-pharmacy-hours',
  templateUrl: './pharmacy-hours.component.html',
  styleUrls: ['./pharmacy-hours.component.css']
})
export class PharmacyHoursComponent implements OnInit{

  isSummer = false;

  constructor() { }

  ngOnInit() {
    const month = new Date().getMonth(); // 0..11
    this.isSummer = month >= 4 && month <= 8;
  }
}
