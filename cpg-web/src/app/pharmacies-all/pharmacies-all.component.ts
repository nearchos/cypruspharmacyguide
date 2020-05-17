import { Component, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { Observable } from 'rxjs';
import { map, startWith } from 'rxjs/operators';
import { FlatPharmacy } from '../flat-pharmacy';
import { DataService } from '../data.service';
import { Platform } from '@angular/cdk/platform';

@Component({
  selector: 'app-pharmacies-all',
  templateUrl: './pharmacies-all.component.html',
  styleUrls: ['./pharmacies-all.component.css']
})
export class PharmaciesAllComponent implements OnInit {

  flatPharmacies: FlatPharmacy[];

  pharmaciesCtrl = new FormControl();
  filteredFlatPharmacies: Observable<FlatPharmacy[]>;
  isAndroid = false;

  constructor(
    private dataService: DataService,
    private platform: Platform
  ) {
    this.filteredFlatPharmacies = this.pharmaciesCtrl.valueChanges
      .pipe(
        startWith(''),
        map(state => state ? this._filterStates(state) : this.flatPharmacies.slice())
      );
    this.isAndroid = this.platform.ANDROID;
  }

  ngOnInit() {
    this.getFlatPharmacies();
  }

  private getFlatPharmacies(): void {
    this.dataService.getAllFlatPharmacies().subscribe(flatPharmacies => {
      this.flatPharmacies = flatPharmacies;
      this.dataService.getLocation().subscribe(position => {
        // const distances: Map<string, number> = new Map();
        this.flatPharmacies.forEach(flatPharmacy => {
          flatPharmacy.distance = DataService.getDistance(
            position.coords.latitude, position.coords.longitude, flatPharmacy.lat, flatPharmacy.lng);
        });
        this.flatPharmacies.sort((a, b) => a.distance - b.distance);
      });
    });
  }

  private _filterStates(value: string): FlatPharmacy[] {
    const filterValue = value.toLowerCase();
    return this.flatPharmacies.filter(state => (state.nameEl.toLowerCase().indexOf(filterValue) === 0)
      || (state.nameEn.toLowerCase().indexOf(filterValue) === 0));
  }
}
