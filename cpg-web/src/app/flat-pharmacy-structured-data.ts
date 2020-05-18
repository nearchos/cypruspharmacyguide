import { FlatPharmacy } from './flat-pharmacy';

export class FlatPharmacyStructuredData {
  private '@context' = 'https://schema.org';
  private '@type' = 'Pharmacy';
  private '@id': string;
  private name: string;
  private address: Address;
  private telephone: string;
  private geo: Geo;
  // todo consider adding 'openingHoursSpecification' - see: https://developers.google.com/search/docs/data-types/local-business

  constructor(flatPharmacy: FlatPharmacy) {
    this['@id'] = 'http://cypruspharmacyguide.com/pharmacy/' + flatPharmacy.ID;
    this.name = flatPharmacy.nameEn;
    this.address = new Address(flatPharmacy);
    this.telephone = '+357' + flatPharmacy.phoneBusiness;
    this.geo = new Geo(flatPharmacy);
  }
}

class Address {
  private '@type' = 'postalAddress';
  private addressCountry = 'Cyprus';
  private addressRegion: string;
  private addressLocality: string;
  private postalCode: string;
  private streetAddress: string;

  constructor(flatPharmacy: FlatPharmacy) {
    this.addressRegion = flatPharmacy.cityNameEn;
    this.addressLocality = flatPharmacy.localityNameEn;
    this.postalCode = flatPharmacy.addressPostalCode;
    this.streetAddress = flatPharmacy.address;
  }
}

class Geo {
  private '@type' = 'GeoCoordinates';
  private latitude: number;
  private longitude: number;

  constructor(flatPharmacy: FlatPharmacy) {
    this.latitude = flatPharmacy.lat;
    this.longitude = flatPharmacy.lng;
  }
}
