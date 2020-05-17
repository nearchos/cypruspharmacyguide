export class FlatPharmacy {

  constructor(
    public ID: number,
    public nameEn: string,
    public nameEl: string,
    public address: string,
    public addressDetails: string,
    public addressPostalCode: string,
    public lat: number,
    public lng: number,
    public localityNameEn: string,
    public localityNameEl: string,
    public cityNameEn: string,
    public cityNameEl: string,
    public phoneBusiness: string,
    public phoneHome: string,
    public distance: number = 0
  ) {
  }

  public toString(): string {
    return `FlatPharmacy (ID: ${this.ID}, name: ${this.nameEn}, locality: ${this.localityNameEn}, city: ${this.cityNameEn})`;
  }
}
