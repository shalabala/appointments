package com.utamas.appointments.model

import java.time.LocalDateTime


data class Appointment(
    val id: String, //PK
    val href: String,
    val category: String,
    val creationDateStr: String,
    val description: String,
    val externalId: String, //userId
    val lastUpdateStr: String,
    val status: AppointmentStatus,
    val validForStr: String,
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
        creationDateStr = LocalDateTime.MIN.toString(),
        description ="",
        externalId ="",
        lastUpdateStr = LocalDateTime.now().toString(),
        status =AppointmentStatus.INITIALIZED,
        validForStr = LocalDateTime.MAX.toString(),
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
get() = LocalDateTime.parse(this.validForStr)

val Appointment.lastUpdate: LocalDateTime
get() = LocalDateTime.parse(this.lastUpdateStr)

val Appointment.creationDate: LocalDateTime
get()= LocalDateTime.parse(this.creationDateStr)
