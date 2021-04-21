package com.utamas.appointments

import android.app.Application
import com.utamas.appointments.dependencies.AppComponent
import com.utamas.appointments.dependencies.DaggerAppComponent

class AppointmentApplication: Application() {
    val appComponent: AppComponent= DaggerAppComponent.builder().application(this).build();
}