import { Component, OnInit } from '@angular/core';
import { FlatPharmacy } from '../flat-pharmacy';
import { Router, ActivatedRoute } from '@angular/router';
import { DataService } from '../data.service';
import { Platform } from '@angular/cdk/platform';

@Component({
  selector: 'app-pharmacy',
  templateUrl: './pharmacy.component.html',
  styleUrls: ['./pharmacy.component.css']
})
export class PharmacyComponent implements OnInit {

  static coreNavUrl = 'www.google.com/maps/dir/?api=1&travelmode=driving&layer=traffic&destination';

  id: number;
  public flatPharmacies: FlatPharmacy[];
  public coreMapUrl = 'https://www.google.com/maps/search/?api=1&query';
  public navUrl: string;
  isAndroid = false;

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
      this.id = (+params.get('pharmacyID'));
      this.dataService.getFlatPharmaciesById(this.id).subscribe(flatPharmacies => {
        this.flatPharmacies = flatPharmacies;
      });
    });
    this.isAndroid = this.platform.ANDROID;
  }
}
