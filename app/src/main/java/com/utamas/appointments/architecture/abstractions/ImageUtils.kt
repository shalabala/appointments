package com.utamas.appointments.architecture.abstractions

import android.graphics.Bitmap
import io.reactivex.rxjava3.core.Single
import java.io.File

interface ImageUtils{
    fun processIntentImage(imageFile: File): Single<Bitmap>
    fun imageToBase64(bitmap: Bitmap): Single<String>
    fun base64ToImage(string: String): Single<Bitmap>
    fun addBorder(bmp: Bitmap, borderSize: Int, color: Int): Single<Bitmap>
     fun ensureNotTooBig(bitmap: Bitmap, maxWidtthOfImage: Int, maxHeightOfImage: Int): Single<Bitmap>
}