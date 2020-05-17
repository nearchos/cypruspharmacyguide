import { Component } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { MatIconRegistry } from '@angular/material/icon';

// tslint:disable-next-line:ban-types
declare let gtag: Function;

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  title = 'Cyprus Pharmacy Guide';

  constructor(
    private router: Router,
    private matIconRegistry: MatIconRegistry
  ) {
    this.matIconRegistry.addSvgIcon(
      `en_lang`,
      `/assets/en.svg`
    );
    this.matIconRegistry.addSvgIcon(
      `el_lang`,
      `/assets/el.svg`
    );
    this.router.events.subscribe(event => { // Analytics tracking (also using code in the Header of root index
      if (event instanceof NavigationEnd) {
        gtag('config', 'G-WGVV5KK2RB', {
            page_path: event.urlAfterRedirects
          }
        );
      }
    });
  }
}
