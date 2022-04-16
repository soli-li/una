import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ReactiveFormsModule } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';

import { IconsProviderModule } from './icons-provider.module';
import { NzModule } from './nz.module';

@NgModule({
  providers: [DatePipe],
  exports: [FormsModule, BrowserModule, BrowserAnimationsModule, ReactiveFormsModule, IconsProviderModule, NzModule],
})
export class SharedModule {}
