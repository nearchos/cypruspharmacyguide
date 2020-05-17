export class UserLocation {

  constructor(
    public lat: number,
    public lng: number
  ) {
  }

  public toString(): string {
    return `(${this.lat}, ${this.lng}`;
  }
}
