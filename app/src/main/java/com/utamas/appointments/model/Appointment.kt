package com.utamas.appointments.model

import java.time.LocalDateTime

data class Appointment(
    val id: String,
    val href: String,
    val category: String,
    val creationDate: LocalDateTime,
    val description: String,
    val externalId: String,
    val lastUpdate: LocalDateTime,
    val status: AppointmentStatus,
    val validFor: LocalDateTime,
    val attachments: List<String>,
    val calendarEventRef: String?,
    val notes: List<String>,
    val relatedEntities: List<String>,
    val relatedPlace: String?,
    val relatedParties: List<String>,
    val contactMedia: List<ContactMedium>
)

