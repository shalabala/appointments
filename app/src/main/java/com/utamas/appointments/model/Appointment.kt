package com.utamas.appointments.model

import com.utamas.appointments.toLocalDateTime
import com.utamas.appointments.toTimeStamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*


data class Appointment(
    val id: String, //PK
    val href: String,
    val category: String,
    val creationDateDate: Date,
    val description: String,
    val externalId: String, //userId
    val lastUpdateDate: Date,
    val status: AppointmentStatus,
    val validForTimeStamp: Date,
    val attachments: List<String>, //[0]=image
    val calendarEventRef: String?,
    val notes: List<String>,
    val relatedEntities: List<String>,
    val relatedPlace: String?,
    val relatedParties: List<String>,
    val contactMedia: List<ContactMedium>
){
    constructor():this(
                id ="",
        href = "",
        category = "",
        creationDateDate = LocalDateTime.MIN.toTimeStamp(),
        description ="",
        externalId ="",
        lastUpdateDate = LocalDateTime.now().toTimeStamp(),
        status =AppointmentStatus.INITIALIZED,
        validForTimeStamp = LocalDateTime.MAX.toTimeStamp(),
        attachments = emptyList(),
        calendarEventRef ="",
        notes = emptyList(),
        relatedEntities = emptyList(),
        relatedPlace ="",
        relatedParties = emptyList(),
        contactMedia = emptyList()
    )

}
val Appointment.validFor: LocalDateTime
get() = this.validForTimeStamp.toLocalDateTime()

val Appointment.lastUpdate: LocalDateTime
get() = this.lastUpdateDate.toLocalDateTime()

val Appointment.creationDate: LocalDateTime
get()= this.creationDateDate.toLocalDateTime()
