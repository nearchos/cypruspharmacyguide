import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PharmaciesTabsComponent } from './pharmacies-tabs/pharmacies-tabs.component';
import { PharmacyComponent } from './pharmacy/pharmacy.component';
import { InfoComponent } from './info.component';
import { SettingsComponent } from './settings.component';

const routes: Routes = [
  { path: '', redirectTo: '/pharmacies', pathMatch: 'full' },
  { path: 'pharmacies', component: PharmaciesTabsComponent },
  { path: 'pharmacy/:pharmacyID', component: PharmacyComponent },
  { path: 'info', component: InfoComponent },
  { path: 'settings', component: SettingsComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
