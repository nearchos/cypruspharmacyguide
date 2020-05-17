import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-aspectsense',
  template: '<div class="aspectsense">' +
            '<span>Developed by <a href="http://aspectsense.com" target="_blank" class="aspectsense-link">aspectsense.com</a> &copy; 2020</span>' +
            '</div>'
})
export class AspectsenseComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }
}
