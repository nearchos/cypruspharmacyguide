import { City } from './city';
import { Locality } from './locality';
import { Pharmacy } from './pharmacy';
import { OnCall } from './on-call';

export interface Data {
  cities: City[];
  localities: Locality[];
  pharmacies: Pharmacy[];
  'on-calls': OnCall[];
  lastUpdated: number;
}
