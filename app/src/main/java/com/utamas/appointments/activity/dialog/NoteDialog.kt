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

class NoteDialog(private val observableList: ObservableArrayList<String>) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            val input = EditText(it)
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)
                .setPositiveButton(
                    R.string.ok,
                    null)
                .setNegativeButton(
                    R.string.cancel,
                    DialogInterface.OnClickListener { dialog, _ ->
                        dialog.cancel()
                    })
                .setTitle(getString(R.string.new_note))
            val dialog=builder.create()
            dialog.setOnShowListener {
                val button = (it as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener{
                        val note = input.text.toString()
                        if (note.isNullOrBlank()) {
                            input.error = it.resources.getString(R.string.this_cant_be_empty)
                        } else {
                            observableList.add(note)
                            dialog.dismiss()
                        }

                }
            }
            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
