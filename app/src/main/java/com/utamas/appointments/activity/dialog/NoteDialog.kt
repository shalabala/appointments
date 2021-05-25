package com.utamas.appointments.activity.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.databinding.ObservableArrayList
import androidx.fragment.app.DialogFragment
import com.utamas.appointments.R
import com.utamas.appointments.model.ContactMedium

class NoteDialog(private val observableList: ObservableArrayList<String>) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val input = EditText(it)
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)
                .setPositiveButton(
                    R.string.ok,
                    DialogInterface.OnClickListener { dialog, _ ->
                        val note=input.text.toString()
                        observableList.add(note)
                    })
                .setNegativeButton(
                    R.string.cancel,
                    DialogInterface.OnClickListener { dialog, _ ->
                        dialog.cancel()
                    })
                .setTitle(getString(R.string.new_note))
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
