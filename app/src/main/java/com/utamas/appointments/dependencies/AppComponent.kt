package com.utamas.appointments.dependencies

import android.app.Application
import com.utamas.appointments.activity.ListAppointmentsActivity
import com.utamas.appointments.activity.LoginActivity
import com.utamas.appointments.architecture.abstractions.BaseViewModel
import com.utamas.appointments.viewmodel.LoginViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ServicesModule::class, FirebaseModule::class])
interface AppComponent{
    fun inject(activity: LoginActivity)
    fun inject(viewModel: LoginViewModel)
    fun inject(viewModel: BaseViewModel)
    fun inject(activity: ListAppointmentsActivity)

    @Component.Builder
    interface Builder{
        @BindsInstance
        fun application(app: Application): Builder
        fun build(): AppComponent
    }
}