package com.utamas.appointments.activity.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.databinding.ObservableArrayList
import androidx.fragment.app.DialogFragment
import com.utamas.appointments.R
import com.utamas.appointments.model.ContactMedium

class PersonDialog(private val observableList: ObservableArrayList<Pair<String, ContactMedium>>) :
    DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater

            builder.setView(inflater.inflate(R.layout.dialog_person, null))
                .setPositiveButton(R.string.ok,
                    DialogInterface.OnClickListener { dialog, id ->
                        val person =
                            (dialog as AlertDialog).findViewById<EditText>(R.id.name)?.text.toString()
                        val category =
                            dialog.findViewById<EditText>(R.id.contactType)?.text.toString()
                        val contact =
                            dialog.findViewById<EditText>(R.id.contact)?.text.toString()

                        observableList.add(
                            person to ContactMedium(
                                "Contact of $person",
                                category,
                                contact
                            )
                        )
                    })
                .setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        getDialog()?.cancel()
                    })
                .setTitle(getString(R.string.new_person))
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }
}