package com.utamas.appointments.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.text.SpannableStringBuilder
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import com.utamas.appointments.architecture.abstractions.BaseViewModel
import com.utamas.appointments.architecture.abstractions.ImageUtils
import com.utamas.appointments.model.Appointment
import com.utamas.appointments.model.ContactMedium
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject

class EditAppointmentViewModel(application: Application) : BaseViewModel(application){
    val dateFormat=DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
    val timeFormat=DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)


    val description=ObservableField<String>("")
    val category=ObservableField<String>("")
    val place=ObservableField<String>("")
    val validForTime=ObservableField<String>("")
    val validForDate=ObservableField<String>("")
    val people=ObservableArrayList<Pair<String,ContactMedium>>()
    val notes=ObservableArrayList<String>()
    val image=ObservableField<BitmapDrawable?>()

    private var validForField:LocalDateTime
    var validFor:LocalDateTime
    get() =validForField
    set(value) {
        validForField=value
        validForDate.set(dateFormat.format(validFor.toLocalDate()))
        validForTime.set(timeFormat.format(validFor.toLocalTime()))
    }

    private lateinit var bitmapField:Bitmap
    var bitmap: Bitmap
        get() =bitmapField
        set(value) {
            bitmapField=value
            image.set(BitmapDrawable(appointmentApplication.resources,bitmap))
        }



    @Inject
    lateinit var imageUtils: ImageUtils

    init {
        appointmentApplication.appComponent.inject(this)
        validForField= LocalDateTime.now()

    }
    fun setAppointment(a: Appointment){
        description.set(a.description)
        category.set(a.category)
        place.set(a.relatedPlace)
        validFor=a.validFor
        if(a.attachments.isNotEmpty()){
            imageUtils.base64ToImage(a.attachments.first()).subscribe()//TODO
        }
    }





}