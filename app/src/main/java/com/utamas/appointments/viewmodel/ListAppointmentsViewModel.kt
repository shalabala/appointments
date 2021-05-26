package com.utamas.appointments.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableField
import com.google.firebase.auth.ktx.oAuthProvider
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

class ListAppointmentsViewModel(application: Application) : BaseViewModel(application){
    var listOfAppointments=""

    @Inject
    lateinit var appointmentService: AppointmentService

    @Inject
    lateinit var imageUtils: ImageUtils

    init{
        appointmentApplication.appComponent.inject(this)

    }
    @BindingAdapter("android:imageBitmap")
    fun loadImage(iv: ImageView, bitmap: Bitmap?) {
        //TODO megcsinálni ezt ObservableField-é, hogy működjön a betöltött kép
        iv.setImageBitmap(bitmap)
    }

    fun loadAppointments(onSuccesCallback:(List<AppointmentDisplayItem>)->Unit,onErrorCallback:(Throwable)->Unit){
        appointmentService.getAllForUser(userService.currentUser?.uid?:"")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ l->onSuccesCallback(l.map { AppointmentDisplayItem(it) }) }, {t-> onErrorCallback(t)} )

    }
    inner class AppointmentDisplayItem{
        val date: String
        lateinit var image: Bitmap
        val appointment: Appointment

        constructor(a: Appointment){
            appointment=a
            date=a.validFor.toNiceString()

            if(a.attachments.isNotEmpty()){
                imageUtils.base64ToImage(a.attachments.first()).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe{bitmap-> image=bitmap }
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

