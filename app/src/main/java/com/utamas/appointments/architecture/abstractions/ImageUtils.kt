package com.utamas.appointments.architecture.abstractions

import android.graphics.Bitmap
import io.reactivex.rxjava3.core.Single
import java.io.File

interface ImageUtils{
    fun processIntentImage(imageFile: File): Single<Bitmap>
}