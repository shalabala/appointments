package com.utamas.appointments.viewmodel

import android.app.Application
import android.text.SpannableStringBuilder
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import com.utamas.appointments.architecture.abstractions.BaseViewModel
import com.utamas.appointments.model.ContactMedium
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class EditAppointmentViewModel(application: Application) : BaseViewModel(application){
    val description=ObservableField<String>("")
    val category=ObservableField<String>("")
    val place=ObservableField<String>("")
    val validFor=ObservableField<LocalDateTime>(LocalDateTime.now())
    val people=ObservableArrayList<Pair<String,ContactMedium>>()
    val notes=ObservableArrayList<String>()



}