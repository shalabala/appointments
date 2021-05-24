package com.utamas.appointments.dependencies

import com.utamas.appointments.architecture.abstractions.AppointmentService
import com.utamas.appointments.architecture.abstractions.ImageService
import com.utamas.appointments.architecture.abstractions.UserService
import com.utamas.appointments.services.FirebaseAppointmentService
import com.utamas.appointments.services.FirebaseUserService
import com.utamas.appointments.services.LocalStorageImageService
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class ServicesModule{
    @Singleton
    @Binds
    abstract fun firebaseAppointmentService(appointmentService: FirebaseAppointmentService):AppointmentService
    @Singleton
    @Binds
    abstract fun firebaseUserService(userService:FirebaseUserService):UserService
    @Singleton
    @Binds
    abstract fun localStorageImageService(imageService: LocalStorageImageService):ImageService
}