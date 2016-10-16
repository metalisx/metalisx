import {Component} from 'angular2/core';

@Component({
    selector: 'my-app',
    template: '<h1>My First Angular 2 App a {{username}}</h1><button (click)="showMessage()">Show message</button>'
})
export class AppComponent {

    username = 'Wayne';

    showMessage = function() {
        console.log('fbk');
    }
}
