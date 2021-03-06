import { Component, OnInit } from '@angular/core';
import { FlatPharmacy } from '../flat-pharmacy';
import { Router, ActivatedRoute } from '@angular/router';
import { DataService } from '../data.service';
import { Platform } from '@angular/cdk/platform';
import Utils from '../utils';

@Component({
  selector: 'app-pharmacy',
  templateUrl: './pharmacy.component.html',
  styleUrls: ['./pharmacy.component.css']
})
export class PharmacyComponent implements OnInit {

  static coreNavUrl = 'www.google.com/maps/dir/?api=1&travelmode=driving&layer=traffic&destination';

  public flatPharmacies: FlatPharmacy[];
  public coreMapUrl = 'https://www.google.com/maps/search/?api=1&query';
  public navUrl: string;
  isAndroid = false;
  structuredData = ''; // todo JSON.stringify(new FlatPharmacyStructuredData(flatPharmacy)); // set structured data

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private dataService: DataService,
    private platform: Platform
  ) {
    this.navUrl = platform.IOS ? `maps://${PharmacyComponent.coreNavUrl}` : `https:${PharmacyComponent.coreNavUrl}`;
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = (+params.get('pharmacyID'));
      this.dataService.getFlatPharmaciesById(id).subscribe(flatPharmacies => {
        this.flatPharmacies = flatPharmacies;
      });
    });
    this.isAndroid = this.platform.ANDROID;
    this.dataService.getLocation().subscribe(position => {
      this.flatPharmacies.forEach(flatPharmacy => flatPharmacy.distance = DataService.getDistance(
        flatPharmacy.lat, flatPharmacy.lng, position.coords.latitude, position.coords.longitude
      ));
    });
  }

  public toGreeklish(greek: string): string {
    return Utils.toGreeklish(greek);
  }
}
