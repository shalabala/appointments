package com.utamas.appointments.services

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.utamas.appointments.architecture.abstractions.AppointmentService
import com.utamas.appointments.model.Appointment
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class FirebaseAppointmentService @Inject constructor(private val db: FirebaseFirestore) :
    AppointmentService {

    private val COLL_STRING = "appointments"
    private val USER_ID_PROPERTY_NAME = "externalId"
    private val collection = db.collection(COLL_STRING)

    override fun getForId(id: String): Single<Appointment> {
        return Single.fromCallable {
            val task = collection.document(id).get()
            Tasks.await(task)
            if (task.isSuccessful) {
                task.result?.toObject(Appointment::class.java)!!
            } else {
                throw task.exception!!
            }
        }
    }

    override fun setOrUpdate(appointment: Appointment): Completable {
        return Completable.fromCallable {
            val task = collection.document(appointment.id).set(appointment)
            Tasks.await(task)
            if (task.isSuccessful) {
                Completable.complete()
            } else {
                Completable.error(task.exception)
            }
        }
    }


    override fun getAllForUser(userId: String): Single<List<Appointment>> {
        return Single.fromCallable {
            val list = mutableListOf<Appointment>()
            val task = collection.whereEqualTo(USER_ID_PROPERTY_NAME, userId).get()
            Tasks.await(task)
            if (task.isSuccessful) {
                task.result!!.forEach { list.add(it.toObject(Appointment::class.java)) }
                list
            } else {
                throw task.exception!!
            }
        }
    }

    override fun delete(appointment: Appointment): Completable {
        return Completable.fromCallable {
            val task = collection.document(appointment.id).delete()
            Tasks.await(task)
            if (task.isSuccessful) {
                Completable.complete()
            } else {
                Completable.error(task.exception)
            }
        }
    }

}