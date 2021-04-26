package com.utamas.appointments.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.utamas.appointments.R
import com.utamas.appointments.architecture.abstractions.BaseActivity
import com.utamas.appointments.architecture.annotations.DeclareViewModel
import com.utamas.appointments.architecture.annotations.DeclareXmlLayout
import com.utamas.appointments.viewmodel.EditAppointmentViewModel

@DeclareXmlLayout(R.layout.activity_edit_appointment)
@DeclareViewModel(EditAppointmentViewModel::class)
class EditAppointmentActivity : BaseActivity<EditAppointmentViewModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
}