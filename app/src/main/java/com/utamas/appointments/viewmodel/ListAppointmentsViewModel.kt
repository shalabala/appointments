package com.utamas.appointments.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.databinding.ObservableField
import com.google.firebase.auth.ktx.oAuthProvider
import com.utamas.appointments.architecture.abstractions.AppointmentService
import com.utamas.appointments.architecture.abstractions.BaseViewModel
import com.utamas.appointments.architecture.abstractions.ImageUtils
import com.utamas.appointments.model.Appointment
import io.reactivex.rxjava3.schedulers.Schedulers
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import javax.inject.Inject

class ListAppointmentsViewModel(application: Application) : BaseViewModel(application){
    var listOfAppointments=""

    @Inject
    lateinit var appointmentService: AppointmentService

    @Inject
    lateinit var imageUtils: ImageUtils

    init{
        appointmentApplication.appComponent.inject(this)

    }

    fun loadAppointments(){
        appointmentService.getAllForUser(userService.currentUser?.uid?:"")
            .addOnSuccessListener { documents->
                for(a in documents){
            }
            }
    }
    inner class AppointmentDisplayItem{
        val id= ObservableField<String>("")
        val name= ObservableField<String>("")
        val category= ObservableField<String>("")
        val date= ObservableField<String>("")
        val image= ObservableField<Bitmap>()

        constructor(a: Appointment){
            id.set(a.id)
            name.set(a.description)
            category.set(a.category)
            date.set(a.validFor.toNiceString())

            if(a.attachments.isNotEmpty()){
                imageUtils.base64ToImage(a.attachments.first()).subscribeOn(Schedulers.computation())
                    .subscribe{bitmap->runOnMainThread { image.set(bitmap) }}

            }


        }
        private fun LocalDateTime.toNiceString(): String{
            val date=this.toLocalDate()
            val time=this.toLocalTime()
            val medFormatter= DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
            val shortFormatter=
                DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(Locale.getDefault()).withZone(
                    ZoneId.systemDefault())
            return "${medFormatter.format(date)}\n" +
                    "${shortFormatter.format(time)}"
            // return "${this.year}.${this.month}.${this.dayOfMonth} ${this.hour} : ${this.minute}"
        }

    }


}