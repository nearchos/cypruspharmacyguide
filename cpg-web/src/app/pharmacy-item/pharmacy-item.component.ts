import { Component, Input, OnInit } from '@angular/core';
import { FlatPharmacy } from '../flat-pharmacy';
import { DataService } from '../data.service';

@Component({
  selector: 'app-pharmacy-item',
  templateUrl: './pharmacy-item.component.html',
  styleUrls: ['./pharmacy-item.component.css'],
})
export class PharmacyItemComponent implements OnInit {

  @Input() flatPharmacy: FlatPharmacy;

  constructor(
    private dataService: DataService
  ) { }

  ngOnInit(): void {
    this.dataService.getLocation().subscribe(position =>
      this.flatPharmacy.distance = DataService.getDistance(
        position.coords.latitude, position.coords.longitude,
        this.flatPharmacy.lat, this.flatPharmacy.lng));
  }
}
