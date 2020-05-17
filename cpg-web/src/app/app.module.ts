import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { PharmaciesTabsComponent } from './pharmacies-tabs/pharmacies-tabs.component';
import { PharmacyComponent } from './pharmacy/pharmacy.component';
import { PharmaciesAllComponent } from './pharmacies-all/pharmacies-all.component';
import { PharmaciesSelectedComponent } from './pharmacies-selected/pharmacies-selected.component';
import { PharmacyItemComponent } from './pharmacy-item/pharmacy-item.component';

import { MatTabsModule } from '@angular/material/tabs';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule} from '@angular/material/icon';
import { MatCardModule} from '@angular/material/card';
import { MatButtonModule} from '@angular/material/button';
import { MatTooltipModule} from '@angular/material/tooltip';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AspectsenseComponent } from './aspectsense.component';
import { MatDividerModule } from '@angular/material/divider';
import { MatRippleModule } from '@angular/material/core';
import { AndroidAppComponent } from './android-app.component';
import { MatExpansionModule } from '@angular/material/expansion';
import { PharmacyHoursComponent } from './pharmacy-hours.component';
import { MatBadgeModule } from '@angular/material/badge';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { SettingsComponent } from './settings.component';
import { InfoComponent } from './info.component';

@NgModule({
  declarations: [
    AppComponent,
    PharmaciesAllComponent,
    PharmaciesSelectedComponent,
    PharmacyItemComponent,
    PharmaciesTabsComponent,
    PharmacyComponent,
    AspectsenseComponent,
    AndroidAppComponent,
    PharmacyHoursComponent,
    SettingsComponent,
    InfoComponent,
  ],
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        HttpClientModule,
        AppRoutingModule,
        FormsModule,
        ReactiveFormsModule,
        MatTabsModule,
        MatToolbarModule,
        MatFormFieldModule,
        MatAutocompleteModule,
        MatInputModule,
        MatIconModule,
        MatCardModule,
        MatButtonModule,
        MatTooltipModule,
        MatProgressSpinnerModule,
        MatDividerModule,
        MatRippleModule,
        MatExpansionModule,
        MatBadgeModule,
        MatSlideToggleModule,
    ],
  providers: [],
  bootstrap: [AppComponent],
  exports: [
    MatAutocompleteModule,
    MatInputModule,
    MatTabsModule,
    MatToolbarModule,
  ]
})
export class AppModule { }
