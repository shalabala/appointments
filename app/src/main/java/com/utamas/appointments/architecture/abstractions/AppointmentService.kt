package com.utamas.appointments.architecture.abstractions

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.utamas.appointments.model.Appointment

interface AppointmentService{
    fun setOrUpdate(appointment: Appointment): Task<Void>

    fun getAllForUser(userId: String): Task<QuerySnapshot>

    fun delete(appointment: Appointment): Task<Void>

}