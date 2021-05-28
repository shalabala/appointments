package com.utamas.appointments.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import com.utamas.appointments.architecture.abstractions.AppointmentService
import com.utamas.appointments.architecture.abstractions.BaseViewModel
import com.utamas.appointments.architecture.abstractions.ImageUtils
import com.utamas.appointments.model.Appointment
import com.utamas.appointments.model.validFor
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import javax.inject.Inject

class ListAppointmentsViewModel(application: Application) : BaseViewModel(application) {
    var listOfAppointments = ""

    @Inject
    lateinit var appointmentService: AppointmentService

    @Inject
    lateinit var imageUtils: ImageUtils

    init {
        appointmentApplication.appComponent.inject(this)

    }


    fun loadAppointments(
        onSuccesCallback: (List<AppointmentDisplayItem>) -> Unit,
        onErrorCallback: (Throwable) -> Unit
    ) {
        if (userService.currentUser?.uid != null) {
            appointmentService.getAllForUser(userService.currentUser?.uid!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { l -> onSuccesCallback(l.map { AppointmentDisplayItem(imageUtils,it) }) },
                    { t -> onErrorCallback(t) })
        }
    }

    fun checkForOverdue(onSuccesCallback: (List<Appointment>) -> Unit,
                        onErrorCallback: (Throwable) -> Unit) {

    }


}

