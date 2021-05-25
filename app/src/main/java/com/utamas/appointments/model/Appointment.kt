package com.utamas.appointments.model

import java.time.LocalDateTime
import java.util.*


data class Appointment(
    val id: String, //PK
    val href: String,
    val category: String,
    val creationDate: LocalDateTime,
    val description: String,
    val externalId: String, //userId
    val lastUpdate: LocalDateTime,
    val status: AppointmentStatus,
    val validFor: LocalDateTime,
    val attachments: List<String>, //[0]=image
    val calendarEventRef: String?,
    val notes: List<String>,
    val relatedEntities: List<String>,
    val relatedPlace: String?,
    val relatedParties: List<String>,
    val contactMedia: List<ContactMedium>
)

