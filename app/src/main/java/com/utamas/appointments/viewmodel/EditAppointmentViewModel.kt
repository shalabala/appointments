package com.utamas.appointments.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import com.utamas.appointments.MAX_HEIGHT_OF_IMAGE
import com.utamas.appointments.MAX_WIDTTH_OF_IMAGE
import com.utamas.appointments.architecture.abstractions.AppointmentService
import com.utamas.appointments.architecture.abstractions.BaseViewModel
import com.utamas.appointments.architecture.abstractions.ImageUtils
import com.utamas.appointments.model.Appointment
import com.utamas.appointments.model.AppointmentStatus
import com.utamas.appointments.model.ContactMedium
import com.utamas.appointments.model.validFor
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import javax.inject.Inject

class EditAppointmentViewModel(application: Application) : BaseViewModel(application) {
    val dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
    val timeFormat = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

    private var originalAppointment: Appointment? = null

    val description = ObservableField<String>("")
    val category = ObservableField<String>("")
    val place = ObservableField<String>("")
    val validForTime = ObservableField<String>("")
    val validForDate = ObservableField<String>("")
    val contacts = ObservableArrayList<ContactMedium>()
    val notes = ObservableArrayList<String>()
    val image = ObservableField<Bitmap>()

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

    init {
        appointmentApplication.appComponent.inject(this)
        validForField = LocalDateTime.now()

    }

    fun setAppointment(
        id: String,
        onSucces: () -> Unit,
        onError: (Throwable) -> Unit,
        onImageError: (Throwable) -> Unit
    ) {
        appointmentService.getForId(id).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                originalAppointment = it
                description.set(it.description)
                category.set(it.category)
                place.set(it.relatedPlace)
                notes.clear()
                notes.addAll(it.notes)
                contacts.clear()
                contacts.addAll(it.contactMedia)
                validFor = it.validFor
                if (it.attachments.isNotEmpty()) {
                    imageUtils.base64ToImage(it.attachments.first())
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ image.set(it) }, { onImageError })
                }

            }, onError)
    }

    fun save(onSucces: () -> Unit, onError: (Throwable) -> Unit) {
        saveImageWithBitmap()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(onSucces, onError)
    }

    private fun saveImageWithBitmap(): Completable {
        val bitmap = image.get()
        if (bitmap==null) {
            return appointmentService.setOrUpdate(getAppointmentToSave(null)).subscribeOn(Schedulers.io())
        } else {
            return imageUtils.ensureNotTooBig(bitmap, MAX_WIDTTH_OF_IMAGE, MAX_HEIGHT_OF_IMAGE)
                .flatMap { imageUtils.imageToBase64(it)}
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                .flatMapCompletable { base64 ->
                    appointmentService.setOrUpdate(
                        getAppointmentToSave(
                            base64
                        )
                    )
                }

        }

    }

    private fun getAppointmentToSave(base64: String?): Appointment {
        if (originalAppointment != null) {
            return modifyAppointment(base64)!!
        } else {
            return createAppointment(base64)
        }
    }

    private fun modifyAppointment(base64: String?): Appointment? {
        return originalAppointment?.copy(
            description = description.get()!!,
            category = category.get()!!,
            relatedPlace = place.get()!!,
            validForStr = validFor.toString(),
            contactMedia = contacts,
            notes = notes,
            attachments = addImageToAttachments(base64),
            lastUpdateStr = LocalDateTime.now().toString()
        )
    }

    private fun addImageToAttachments(base64: String?): List<String> {
        if (base64.isNullOrEmpty()) {
            return emptyList()
        } else {
            val list = ArrayList(originalAppointment?.attachments?: emptyList() )
            list[0] = base64
            return list
        }
    }

    private fun createAppointment(base64: String?): Appointment {
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
            attachments = if(base64.isNullOrEmpty()) emptyList() else listOf(base64),
            calendarEventRef = "",
            notes = notes,
            relatedEntities = emptyList<String>(),
            relatedPlace = place.get()!!,
            relatedParties = emptyList<String>(),
            contactMedia = contacts
        )
    }


}