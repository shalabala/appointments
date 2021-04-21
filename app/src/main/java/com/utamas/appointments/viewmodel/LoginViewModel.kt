package com.utamas.appointments.viewmodel

import android.app.Application
import com.utamas.appointments.architecture.abstractions.BaseViewModel
import com.utamas.appointments.architecture.abstractions.UserService
import javax.inject.Inject

class LoginViewModel(application: Application) :BaseViewModel(application){
    val name="Hello world"
    @Inject
    lateinit var userService: UserService
    init {
        appointmentApplication.appComponent.inject(this)
    }

}