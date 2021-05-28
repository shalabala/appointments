package com.utamas.appointments.activity.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.content.res.Resources
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.databinding.ObservableArrayList
import androidx.fragment.app.DialogFragment
import com.utamas.appointments.R
import com.utamas.appointments.model.ContactMedium

class PersonDialog(private val observableList: ObservableArrayList<ContactMedium>) :
    DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater


            builder.setView(inflater.inflate(R.layout.dialog_person, null))
                .setPositiveButton(R.string.ok,null )
                .setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        getDialog()?.cancel()
                    })
                .setTitle(getString(R.string.new_person))
            val dialog=builder.create()
            dialog.setOnShowListener {
                val button = (it as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener{

                    val person =
                        dialog.findViewById<EditText>(R.id.name)
                    val category =
                        dialog.findViewById<EditText>(R.id.contactType)
                    val contact =
                        dialog.findViewById<EditText>(R.id.contact)
                    if (validate(it.resources, person!!, category!!, contact!!)) {
                        observableList.add(
                            ContactMedium(
                                person.text.toString(),
                                category.text.toString(),
                                contact.text.toString()
                            )
                        )
                        dialog.cancel()
                    }
                }
            }
            dialog
        } ?: throw IllegalStateException("Activity cannot be null")

    }

    private fun validate(
        resources: Resources,
        person: EditText,
        category: EditText,
        contact: EditText
    ): Boolean {
        var valid = true
        if (person.text.toString().isNullOrBlank()) {
            person.error = resources.getString(R.string.this_cant_be_empty)
            valid = false
        }
        if (category.text.toString().isNullOrBlank()) {
            category.error = resources.getString(R.string.this_cant_be_empty)
            valid = false
        }
        if (contact.text.toString().isNullOrBlank()) {
            contact.error = resources.getString(R.string.this_cant_be_empty)
            valid = false
        }
        return valid
    }
}