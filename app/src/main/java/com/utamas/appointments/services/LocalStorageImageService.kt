package com.utamas.appointments.services

import android.graphics.Bitmap
import com.utamas.appointments.architecture.abstractions.ImageService
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class LocalStorageImageService @Inject constructor() : ImageService{
    override fun getImage(reference: String): Single<Bitmap?> {
        return Single.fromSupplier{null}
    }

    override fun putImage(image: Bitmap, reference: String): Single<Result<Nothing>> {
        TODO("Not yet implemented")
    }

}