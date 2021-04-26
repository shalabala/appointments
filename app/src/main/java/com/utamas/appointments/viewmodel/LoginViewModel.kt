package com.utamas.appointments.viewmodel

import android.app.Application
import android.util.Log
import androidx.databinding.ObservableField
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.utamas.appointments.architecture.abstractions.BaseViewModel
import com.utamas.appointments.architecture.abstractions.UserService
import javax.inject.Inject

class LoginViewModel(application: Application) :BaseViewModel(application){
    private val TAG= this::class.qualifiedName;
    val userName=ObservableField<String>("")
    val password=ObservableField<String>("")

    fun login(onLogin:(Task<AuthResult>)->Unit){
        userService.signInWithEmailAndPassword(userName.get()!!,password.get()!!, onLogin)
        userName.set("")
        password.set("")
    }


}