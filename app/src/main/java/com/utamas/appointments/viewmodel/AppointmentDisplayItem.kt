package com.utamas.appointments.viewmodel

import android.graphics.Bitmap
import android.graphics.Color
import androidx.databinding.ObservableField
import com.utamas.appointments.architecture.abstractions.ImageUtils
import com.utamas.appointments.model.Appointment
import com.utamas.appointments.model.validFor
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class AppointmentDisplayItem(private val imageUtils: ImageUtils,  val appointment: Appointment) {
    val date: String
    val image = ObservableField<Bitmap>()

    init{
        date = appointment.validFor.toNiceString()

        if (appointment.attachments.isNotEmpty()) {
            imageUtils.base64ToImage(appointment.attachments.first())
                .subscribeOn(Schedulers.computation())
                .flatMap{
                    if(appointment.validFor<LocalDateTime.now())
                    imageUtils.addBorder(it,25,Color.RED)else Single.fromSupplier { it }}
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                        bitmap -> image.set(bitmap)
                }
        }


    }

    private fun LocalDateTime.toNiceString(): String {
        val date = this.toLocalDate()
        val time = this.toLocalTime()
        val medFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        val shortFormatter =
            DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(Locale.getDefault())
                .withZone(
                    ZoneId.systemDefault()
                )
        return "${medFormatter.format(date)}\n" +
                "${shortFormatter.format(time)}"
        // return "${this.year}.${this.month}.${this.dayOfMonth} ${this.hour} : ${this.minute}"
    }

}