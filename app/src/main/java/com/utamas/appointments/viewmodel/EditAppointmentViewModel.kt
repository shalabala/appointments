package com.utamas.appointments.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
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
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import javax.inject.Inject

class EditAppointmentViewModel(application: Application) : BaseViewModel(application) {
    val dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
    val timeFormat = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

    private var originalAppointment = Optional.empty<Appointment>()

    val description = ObservableField<String>("")
    val category = ObservableField<String>("")
    val place = ObservableField<String>("")
    val validForTime = ObservableField<String>("")
    val validForDate = ObservableField<String>("")
    val people = ObservableArrayList<Pair<String, ContactMedium>>()
    val notes = ObservableArrayList<String>()
    val image = ObservableField<BitmapDrawable?>()

    @Inject
    lateinit var imageUtils: ImageUtils

    @Inject
    lateinit var appointmentService: AppointmentService

    private var validForField: LocalDateTime


    var validFor: LocalDateTime
        get() = validForField
        set(value) {
            validForField = value
            validForDate.set(dateFormat.format(validFor.toLocalDate()))
            validForTime.set(timeFormat.format(validFor.toLocalTime()))
        }

    private lateinit var bitmapField: Bitmap
    var bitmap: Bitmap
        get() = bitmapField
        set(value) {
            bitmapField = value
            image.set(BitmapDrawable(appointmentApplication.resources, bitmap))
        }


    init {
        appointmentApplication.appComponent.inject(this)
        validForField = LocalDateTime.now()

    }

    fun setAppointment(a: Appointment) {
        originalAppointment = Optional.of(a)
        description.set(a.description)
        category.set(a.category)
        place.set(a.relatedPlace)
        validFor = a.validFor
        if (a.attachments.isNotEmpty()) {
            imageUtils.base64ToImage(a.attachments.first()).subscribe()//TODO
        }
    }

    fun save(onSucces: () -> Unit, onError: (Throwable) -> Unit) {
        imageUtils.imageToBase64(bitmap).subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.io())
            .flatMapCompletable { base64->appointmentService.setOrUpdate(getAppointmentToSave(base64))}
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onSucces,onError)

    }

    private fun getAppointmentToSave(base64: String): Appointment{
        if(originalAppointment.isPresent){
            return modifyAppointment(base64)
        }else{
            return createAppointment(base64)
        }
    }

    private fun modifyAppointment(base64: String): Appointment {
        return originalAppointment.get().copy(
            description = description.get()!!,
            category = category.get()!!,
            relatedPlace = category.get()!!,
            validForStr = validFor.toString(),
            contactMedia = people.map { it.second },
            notes = notes,
            attachments = addImageToAttachments(base64),
            lastUpdateStr = LocalDateTime.now().toString()
        )
    }

    private fun addImageToAttachments(base64: String): List<String> {
        val list = ArrayList(originalAppointment.get().attachments)
        list[0] = base64
        return list
    }

    private fun createAppointment(base64: String): Appointment {
        return Appointment(
            id = UUID.randomUUID().toString(),
            href = "",
            category = category.get()!!,
            creationDateStr = LocalDateTime.now().toString(),
            description = description.get()!!,
            externalId = userService.currentUser?.uid!!,
            lastUpdateStr = LocalDateTime.now().toString(),
            status = AppointmentStatus.INITIALIZED,
            validForStr = validFor.toString(),
            attachments = listOf(base64),
            calendarEventRef = "",
            notes = notes,
            relatedEntities = emptyList<String>(),
            relatedPlace = place.get()!!,
            relatedParties = emptyList<String>(),
            contactMedia = people.map { it.second }
        )
    }


}