package com.utamas.appointments.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.utamas.appointments.R
import com.utamas.appointments.architecture.abstractions.BaseActivity
import com.utamas.appointments.architecture.annotations.DeclareXmlLayout
import com.utamas.appointments.viewmodel.ViewAppointmentViewModel

@DeclareXmlLayout(R.layout.activity_view_appointment)
class ViewAppointmentActivity : BaseActivity<ViewAppointmentViewModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
}