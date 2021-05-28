package com.utamas.appointments.dependencies

import android.app.Application
import com.utamas.appointments.activity.*
import com.utamas.appointments.architecture.abstractions.BaseViewModel
import com.utamas.appointments.viewmodel.EditAppointmentViewModel
import com.utamas.appointments.viewmodel.ListAppointmentsViewModel
import com.utamas.appointments.viewmodel.LoginViewModel
import com.utamas.appointments.viewmodel.ViewAppointmentViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ServicesModule::class, FirebaseModule::class])
interface AppComponent{
    fun inject(activity: LoginActivity)
    fun inject(activity: EditAppointmentActivity)
    fun inject(activity: ListAppointmentsActivity)
    fun inject(activity: ViewAppointmentActivity)
    fun inject(activity: RegisterActivity)

    fun inject(viewModel: LoginViewModel)
    fun inject(viewModel: BaseViewModel)
    fun inject(viewModel: ListAppointmentsViewModel)
    fun inject(viewModel: EditAppointmentViewModel)
    fun inject(viewModel: ViewAppointmentViewModel)

    @Component.Builder
    interface Builder{
        @BindsInstance
        fun application(app: Application): Builder
        fun build(): AppComponent
    }
}