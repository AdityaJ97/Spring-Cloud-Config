import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export class CredentialSpring {
  domain: string;
  subdomain: string;
  key: string;
  label: string;
  url: string;
  constructor(domain: string, subdomain: string, key: string, label: string, url: string) {
    this.domain = domain;
    this.subdomain = subdomain;
    this.key = key;
    this.label = label;
    this.url = url;
  }
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent {
  public credential: CredentialSpring = new CredentialSpring('', '', '', '', '');
  public result;
  public setText: string;
  public error: any;
  constructor(private httpClient: HttpClient) {

  }

  onSubmit() {
    /* this.httpClient.post('/users/defaultScreen', this.model, this.optionsBuilder.createRequestOptions(this.requestOptions)).subscribe((data) => {
         this.changeState(State.SUBMITTED);
     });*/
    let flag = 0;
    let keyfound = "null";
    this.setText=null;
    this.error=null;
    this.httpClient.get(this.credential.url + '/' + this.credential.domain + '/' + this.credential.subdomain + '/' + this.credential.label).subscribe(
      (data) => {
        this.result = data;
          for (let entry of this.result.propertySources) {
            for (let key in entry.source) {
              if (key == this.credential.key) {
                keyfound = key;
                flag = 1;
                break;

              }
            }
            if (flag) {
              this.setText = entry.source[keyfound];
              break;
            }
          }
          if (flag == 0) {
            this.httpClient.get(this.credential.url + '/' + this.credential.key + '/' + this.credential.domain + this.credential.subdomain + '/' + this.credential.label).subscribe(
              (data) => {
                this.result = data;
                this.setText = this.result.propertySources[0].source[this.credential.key];

              }
            );

          }
        

      },
      (err) => {
        this.error = err.message;
      }
    );
  }
}
