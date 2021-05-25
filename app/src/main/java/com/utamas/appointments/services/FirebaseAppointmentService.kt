package com.utamas.appointments.services

import android.database.Observable
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.utamas.appointments.architecture.abstractions.AppointmentService
import com.utamas.appointments.model.Appointment
import javax.inject.Inject
import javax.inject.Singleton

class FirebaseAppointmentService @Inject constructor(private val db: FirebaseFirestore): AppointmentService{

    private val COLL_STRING="appointments"
    private val USER_ID_PROPERTY_NAME="externalId"
    private val collection=db.collection(COLL_STRING)

    fun setOrUpdate(appointment: Appointment): Task<Void> {
        return collection.document(appointment.id).set(appointment)
    }

    fun getAllForUser(userId: String): Task<QuerySnapshot> {
        return collection.whereEqualTo(USER_ID_PROPERTY_NAME,userId).get()
    }

    fun delete(appointment: Appointment): Task<Void> {
        return collection.document(appointment.id).delete()
    }

}