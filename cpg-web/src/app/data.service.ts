import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of} from 'rxjs';

import { Data } from './data';
import { City } from './city';
import { Locality } from './locality';
import { Pharmacy } from './pharmacy';
import { OnCall } from './on-call';
import { FlatPharmacy } from './flat-pharmacy';
import Utils from './utils';

@Injectable({
  providedIn: 'root'
})
export class DataService {

  private lastKnownPosition: Position;

  // Create an Observable that will start listening to geolocation updates when a consumer subscribes.
  // from: https://angular.io/guide/observables
  private locations = new Observable<Position>((observer) => {
    let watchId: number;
    // Simple geolocation API check provides values to publish
    if ('geolocation' in navigator) {
      watchId = navigator.geolocation.watchPosition((position: Position) => {
        this.lastKnownPosition = position;
        observer.next(position);
      }, (error: PositionError) => {
        observer.error(error);
      });
    } else {
      observer.error('Geolocation not available');
    }

    // When the consumer unsubscribes, clean up data ready for next subscription.
    return {
      unsubscribe() {
        navigator.geolocation.clearWatch(watchId);
      }
    };
  });

  // https://www.movable-type.co.uk/scripts/latlong.html
  public static getDistance(lat1: number, lon1: number, lat2: number, lon2: number): number {
    const R = 6371e3; // metres
    const f1 = lat1 * Math.PI / 180; // φ, λ in radians
    const f2 = lat2 * Math.PI / 180;
    const dF = (lat2 - lat1) * Math.PI / 180;
    const dL = (lon2 - lon1) * Math.PI / 180;

    const a = Math.sin(dF / 2) * Math.sin(dF /  2) +
      Math.cos(f1) * Math.cos(f2) *
      Math.sin(dL / 2) * Math.sin(dL / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    return Math.ceil(R * c / 10) * 10; // in metres - rounded to nearest 10
  }

  private static isEarlyHours(): boolean {
    return new Date().getHours() < 8; // Get the hour (0-23)
  }

  private static getFormattedDateForNow(): string {
    if (DataService.isEarlyHours()) { // yesterday
      const yesterday: Date = new Date(Date.now() - 864e5);
      return yesterday.toISOString().split('T')[0];
    } else { // today
      const today: Date = new Date();
      return today.toISOString().split('T')[0];
    }
  }

  private static getFormattedDateForNextDay(): string {
    if (DataService.isEarlyHours()) { // yesterday
      const today: Date = new Date();
      return today.toISOString().split('T')[0];
    } else { // today
      const tomorrow: Date = new Date(Date.now() + 864e5);
      return tomorrow.toISOString().split('T')[0];
    }
  }

  private static extractArray(listAsString: string): number[] {
    if (listAsString.length > 0) {
      return listAsString.replace(/, +/g, ',').split(',').map(Number);
    } else {
      return [];
    }
  }

  constructor(
    private readonly http: HttpClient
  ) {
  }

  private static convertToFlatPharmacy(pharmacy: Pharmacy, locality: Locality, city: City): FlatPharmacy {
    return new FlatPharmacy(
      pharmacy.ID,
      Utils.toGreeklish(pharmacy.name),
      pharmacy.name,
      pharmacy.address,
      pharmacy.addressDetails,
      pharmacy.addressPostalCode,
      pharmacy.lat,
      pharmacy.lng,
      locality.nameEn,
      locality.nameEl,
      city.nameEn,
      city.nameEl,
      pharmacy.phoneBusiness,
      pharmacy.phoneHome
    );
  }

  private getData(): Observable<Data> {
    return this.http.get<Data>('/assets/mock-data.json');
    // return this.http.get<Data>('https://cypruspharmacyguide.appspot.com/json/update-all?magic=...');
  }

  public getAllFlatPharmacies(): Observable<FlatPharmacy[]> {
    const flatPharmaciesAll: FlatPharmacy[] = [];
    const localitiesMap = new Map();
    const citiesMap = new Map();
    this.getData().subscribe(data => {
      data.cities.forEach(city => citiesMap[city.UUID] = city);
      data.localities.forEach(locality => localitiesMap[locality.UUID] = locality);
      data.pharmacies.forEach(pharmacy => {
        const locality: Locality = localitiesMap[pharmacy.localityUUID];
        const city: City = citiesMap[locality.cityUUID];
        const flatPharmacy: FlatPharmacy = DataService.convertToFlatPharmacy(pharmacy, locality, city);
        flatPharmaciesAll.push(flatPharmacy);
      });
    });
    return of(flatPharmaciesAll);
  }

  public getPharmaciesOnCallNow(): Observable<FlatPharmacy[]> {
    const flatPharmaciesOnCallNow: FlatPharmacy[] = [];
    const localitiesMap = new Map();
    const citiesMap = new Map();
    this.getData().subscribe(data => {
      data.cities.forEach(city => citiesMap[city.UUID] = city);
      data.localities.forEach(locality => localitiesMap[locality.UUID] = locality);
      const idsNow: string = data['on-calls'].find((onCall: OnCall) => onCall.date === DataService.getFormattedDateForNow()).pharmacies;
      const onCallNow: number[] = DataService.extractArray(idsNow);
      data.pharmacies.forEach(pharmacy => {
        const locality: Locality = localitiesMap[pharmacy.localityUUID];
        const city: City = citiesMap[locality.cityUUID];
        const flatPharmacy: FlatPharmacy = DataService.convertToFlatPharmacy(pharmacy, locality, city);
        if (onCallNow.includes(pharmacy.ID)) { flatPharmaciesOnCallNow.push(flatPharmacy); }
      });
    });
    return of(flatPharmaciesOnCallNow);
  }

  public getPharmaciesNextDay(): Observable<FlatPharmacy[]> {
    const flatPharmaciesNextDay: FlatPharmacy[] = [];
    const localitiesMap = new Map();
    const citiesMap = new Map();
    this.getData().subscribe(data => {
      data.cities.forEach(city => citiesMap[city.UUID] = city);
      data.localities.forEach(locality => localitiesMap[locality.UUID] = locality);
      const idsNextDay: string = data['on-calls'].find(onCall => onCall.date === DataService.getFormattedDateForNextDay()).pharmacies;
      const onCallNextDay: number[] = DataService.extractArray(idsNextDay);
      data.pharmacies.forEach(pharmacy => {
        const locality: Locality = localitiesMap[pharmacy.localityUUID];
        const city: City = citiesMap[locality.cityUUID];
        const flatPharmacy: FlatPharmacy = DataService.convertToFlatPharmacy(pharmacy, locality, city);
        if (onCallNextDay.includes(pharmacy.ID)) { flatPharmaciesNextDay.push(flatPharmacy); }
      });
    });
    return of(flatPharmaciesNextDay);
  }

  public getFlatPharmaciesById(id: number): Observable<FlatPharmacy[]> {
    const flatPharmaciesById: FlatPharmacy[] = [];
    const localitiesMap = new Map();
    const citiesMap = new Map();
    this.getData().subscribe(data => {
      data.cities.forEach(city => citiesMap[city.UUID] = city);
      data.localities.forEach(locality => localitiesMap[locality.UUID] = locality);
      data.pharmacies.forEach(pharmacy => {
        if (pharmacy.ID === id) {
          const locality: Locality = localitiesMap[pharmacy.localityUUID];
          const city: City = citiesMap[locality.cityUUID];
          const flatPharmacy: FlatPharmacy = DataService.convertToFlatPharmacy(pharmacy, locality, city);
          flatPharmaciesById.push(flatPharmacy);
        }
      });
    });
    return of(flatPharmaciesById);
  }

  public getLocation(): Observable<Position> {
    return this.locations;
  }
}
