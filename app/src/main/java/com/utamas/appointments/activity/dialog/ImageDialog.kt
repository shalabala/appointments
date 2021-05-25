package com.utamas.appointments.activity.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.utamas.appointments.R

class ImageDialog(private val allowGallery: Boolean, private val callback: (Options) -> Unit) :
    DialogFragment() {

    enum class Options { CAMERA, GALLERY, CANCELLED }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val chooseString = resources.getString((R.string.choose_photo))
        val takeString = resources.getString((R.string.take_photo))
        val cancelString = resources.getString((R.string.cancel))
        val options = if (allowGallery) arrayOf(chooseString, takeString, cancelString)
        else arrayOf(takeString, cancelString)

        val builder = AlertDialog.Builder(activity)
        builder.setItems(options) { dialog, item ->
            var option: Options
            when (options[item]) {
                takeString ->
                    option = Options.CAMERA
                chooseString ->
                    option = Options.GALLERY
                else ->
                    option = Options.CANCELLED
            }
            dialog.dismiss()
            callback(option)
        }
        return builder.create()
    }
}
