package com.utamas.appointments.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.utamas.appointments.architecture.abstractions.BaseViewModel

class RegisterViewModel(application: Application) : BaseViewModel(application) {
    val userName = ObservableField("")
    val password = ObservableField("")
    val passwordAgain = ObservableField("")


    fun register(onRegistration: (Task<AuthResult>) -> Unit) {
        userService.registerWithUsernameAndPassword(
            userName.get()!!,
            password.get()!!,
            onRegistration
        )
        userName.set("")
        password.set("")
        passwordAgain.set("")
    }

}
