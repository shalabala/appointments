package com.utamas.appointments.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.utamas.appointments.R
import com.utamas.appointments.architecture.abstractions.BaseActivity
import com.utamas.appointments.architecture.annotations.DeclareViewModel
import com.utamas.appointments.architecture.annotations.DeclareXmlLayout
import com.utamas.appointments.viewmodel.ListAppointmentsViewModel

@DeclareViewModel(ListAppointmentsViewModel::class)
@DeclareXmlLayout(R.layout.activity_list_appointments)
class ListAppointmentsActivity : BaseActivity<ListAppointmentsViewModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onBackPressed() {
       // super.onBackPressed()
    }
}