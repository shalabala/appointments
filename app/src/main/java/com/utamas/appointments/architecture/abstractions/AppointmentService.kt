package com.utamas.appointments.architecture.abstractions

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.utamas.appointments.model.Appointment
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface AppointmentService{
    fun setOrUpdate(appointment: Appointment): Completable

    fun getAllForUser(userId: String): Single<List<Appointment>>

    fun delete(appointment: Appointment): Completable

}