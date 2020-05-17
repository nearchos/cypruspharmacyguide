import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PharmaciesTabsComponent } from './pharmacies-tabs/pharmacies-tabs.component';
import { PharmacyComponent } from './pharmacy/pharmacy.component';
import { InfoComponent } from './info.component';
import { PrivacyComponent } from './privacy.component';

const routes: Routes = [
  { path: '', redirectTo: '/pharmacies', pathMatch: 'full' },
  { path: 'pharmacies', component: PharmaciesTabsComponent },
  { path: 'pharmacy/:pharmacyID', component: PharmacyComponent },
  { path: 'info', component: InfoComponent },
  { path: 'privacy', component: PrivacyComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
