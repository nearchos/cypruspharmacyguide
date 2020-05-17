import { Component, Input, OnInit } from '@angular/core';
import { SelectionType } from '../selection-type';
import { FlatPharmacy } from '../flat-pharmacy';
import { DataService } from '../data.service';
import { Platform } from '@angular/cdk/platform';

@Component({
  selector: 'app-pharmacies-selected',
  templateUrl: './pharmacies-selected.component.html',
  styleUrls: ['./pharmacies-selected.component.css']
})
export class PharmaciesSelectedComponent implements OnInit {

  @Input() selectionType: SelectionType;
  flatPharmacies: FlatPharmacy[] = [];

  isAndroid = false;
  infoMessage: string;

  constructor(
    private dataService: DataService,
    private platform: Platform
  ) { }

  ngOnInit() {
    this.getFlatPharmacies();
    this.isAndroid = this.platform.ANDROID;
    this.infoMessage = (this.selectionType === SelectionType.Now) ? 'Pharmacies on call now, until next morning 7 AM.' : 'Pharmacies on call next day.';
  }

  private getFlatPharmacies(): void {
    if (this.selectionType === SelectionType.Now) {
      this.dataService.getPharmaciesOnCallNow().subscribe(flatPharmacies => this.flatPharmacies = flatPharmacies);
      this.dataService.getLocation().subscribe(position => this.sortFlatPharmaciesByDistance(position));
    } else {
      this.dataService.getPharmaciesNextDay().subscribe(flatPharmacies => this.flatPharmacies = flatPharmacies);
      this.dataService.getLocation().subscribe(position => this.sortFlatPharmaciesByDistance(position));
    }
  }

  private sortFlatPharmaciesByDistance(position: Position): void {
    this.flatPharmacies.forEach(flatPharmacy =>
      flatPharmacy.distance = DataService.getDistance(
        position.coords.latitude, position.coords.longitude, flatPharmacy.lat, flatPharmacy.lng));
    this.flatPharmacies.sort((a, b) => a.distance - b.distance);
  }
}
