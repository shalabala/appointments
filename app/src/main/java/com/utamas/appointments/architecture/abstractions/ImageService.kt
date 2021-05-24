package com.utamas.appointments.architecture.abstractions

import android.graphics.Bitmap
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.CompletableFuture

interface ImageService {
    fun getImage(reference: String): Single<Bitmap?>
    fun putImage(image: Bitmap, reference: String): Single<Result<Nothing>>
}