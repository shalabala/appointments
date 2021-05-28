package com.utamas.appointments.architecture.abstractions

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.os.Handler
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleObserver
import com.utamas.appointments.AppointmentApplication
import javax.inject.Inject


abstract class BaseViewModel(application: Application) : AndroidViewModel(application),
    LifecycleObserver {

    @Inject
    lateinit var userService: UserService

    protected var appointmentApplication: AppointmentApplication
    private var mainThreadHandler: Handler

    protected val appContext: Context
        protected get() = getApplication<Application>().applicationContext

    protected val appResources: Resources
        protected get() = getApplication<Application>().resources

    init {
        this.appointmentApplication = application as AppointmentApplication
        this.appointmentApplication.appComponent.inject(this)
        mainThreadHandler= Handler(appointmentApplication.mainLooper)
    }

    @Deprecated("Use rx when possible instead")
    fun runOnMainThread(runnable:()->Unit){
        mainThreadHandler.post(runnable)
    }

}