package com.utamas.appointments.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import com.utamas.appointments.architecture.abstractions.AppointmentService
import com.utamas.appointments.architecture.abstractions.BaseViewModel
import com.utamas.appointments.architecture.abstractions.ImageUtils
import com.utamas.appointments.model.Appointment
import com.utamas.appointments.model.AppointmentStatus
import com.utamas.appointments.model.ContactMedium
import com.utamas.appointments.model.validFor
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import javax.inject.Inject

class ViewAppointmentViewModel(application: Application) : BaseViewModel(application){
    @Inject
    lateinit var imageUtils: ImageUtils

    @Inject
    lateinit var appointmentService: AppointmentService

    private var appointment: Appointment?=null

    val description=ObservableField("")
    val category=ObservableField("")
    val place=ObservableField("")
    val date=ObservableField("")
    val status=ObservableField<AppointmentStatus>()

    val notes= ObservableArrayList<String>()
    val contacts= ObservableArrayList<ContactMedium>()
    val image=ObservableField<Bitmap>()

    init {
        appointmentApplication.appComponent.inject(this)
    }

    fun loadAppointment(id: String,onSuccess: ()->Unit,onError:(Throwable)->Unit, onImageError:(Throwable)->Unit={}){
        appointmentService.getForId(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                this@ViewAppointmentViewModel.appointment=it
                description.set(it.description)
                category.set(it.category)
                place.set(it.relatedPlace)
                notes.clear()
                notes.addAll(it.notes)
                contacts.clear()
                contacts.addAll(it.contactMedia)
                date.set(it.validFor.toNiceString())
                status.set(it.status)
                if(!it.attachments.isEmpty()){
                    imageUtils.base64ToImage(it.attachments[0]).subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe({
                            image.set(it)
                        },{
                            onImageError(it)
                        })
                }
            },{
                onError(it)
            })
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
        return "${medFormatter.format(date)} ${shortFormatter.format(time)}"
    }

    fun delete(onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
        if(appointment!=null){
            appointmentService.delete(appointment!!).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess,onError)
        }
    }

}