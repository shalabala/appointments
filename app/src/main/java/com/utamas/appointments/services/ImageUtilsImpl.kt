package com.utamas.appointments.services

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.media.ExifInterface
import com.utamas.appointments.architecture.abstractions.ImageUtils
import io.reactivex.rxjava3.core.Single
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import javax.inject.Inject


class ImageUtilsImpl @Inject constructor() : ImageUtils {
    //region async methods
    override fun processIntentImage(imageFile: File): Single<Bitmap> {
        return Single.fromCallable {
            var image = getImageFromExternal(imageFile)
            image = rotateImageIfRequired(image, imageFile.absolutePath)
            image
        }
    }

    //endregion
    //sync methods
    private fun ensureNotTooBig(bitmap: Bitmap, maxwidth: Int, maxheight: Int): Bitmap {
        val ratioToMaxWidth = bitmap.width.toDouble() / maxwidth
        val ratioToMaxHeight = bitmap.height.toDouble() / maxheight
        if (ratioToMaxHeight <= 1 && ratioToMaxWidth <= 1) {
            return bitmap
        }
        val biggerOffender = Math.max(ratioToMaxHeight, ratioToMaxWidth)
        val newHeight = (bitmap.height / biggerOffender).toInt()
        val newWidth = (bitmap.width / biggerOffender).toInt()
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    @Throws(IOException::class)
    private fun rotateImageIfRequired(img: Bitmap, selectedImage: String?): Bitmap {
        val ei = ExifInterface(selectedImage)
        val orientation =
            ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270)
            else -> img
        }
    }

    private fun rotateImage(img: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedImg =
            Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
        img.recycle()
        return rotatedImg
    }

    private fun addBorder(bmp: Bitmap, borderSize: Int, color: Int): Bitmap {
        val bmpWithBorder = Bitmap.createBitmap(
            bmp.width + borderSize * 2,
            bmp.height + borderSize * 2,
            bmp.config
        )
        val canvas = Canvas(bmpWithBorder)
        canvas.drawColor(color)
        canvas.drawBitmap(bmp, borderSize.toFloat(), borderSize.toFloat(), null)
        return bmpWithBorder
    }

    //endregion
    //private helpers
    @Throws(IOException::class)
    private fun getImageFromExternal(path: File): Bitmap {
        val fis = FileInputStream(path)
        return BitmapFactory.decodeStream(fis)
    } //endregion
}