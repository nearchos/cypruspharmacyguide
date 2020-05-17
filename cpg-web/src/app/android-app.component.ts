import { Component, OnInit } from '@angular/core';
import {Platform} from '@angular/cdk/platform';

@Component({
  selector: 'app-android-app',
  templateUrl: './android-app.component.html',
  styleUrls: ['./android-app.component.css']
})
export class AndroidAppComponent implements OnInit {

  isAndroid = false;

  constructor(
    private platform: Platform
  ) { }

  ngOnInit(): void {
    this.isAndroid = this.platform.ANDROID;
  }

}
