package com.utamas.appointments.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.utamas.appointments.APPOINTMENT_ITEM
import com.utamas.appointments.R
import com.utamas.appointments.architecture.abstractions.BaseActivity
import com.utamas.appointments.architecture.annotations.DeclareViewModel
import com.utamas.appointments.architecture.annotations.DeclareXmlLayout
import com.utamas.appointments.viewmodel.ViewAppointmentViewModel


@DeclareXmlLayout(R.layout.activity_view_appointment)
@DeclareViewModel(ViewAppointmentViewModel::class)
class ViewAppointmentActivity : BaseActivity<ViewAppointmentViewModel>() {
    private lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        id = intent.getStringExtra(APPOINTMENT_ITEM)
        setUpToolbar(findViewById(R.id.toolbar))
        if (id.isNullOrEmpty()) {
            throw RuntimeException("Details page called with no element id to display")
            finish()
        }


    }

    override fun onResume() {
        super.onResume()
        viewModel.loadAppointment(id!!, {}, {
            showLongToast(R.string.data_loading_error)
            it.printStackTrace()
        }, {
            showLongToast(R.string.image_conversion_error)
            it.printStackTrace()
        })
    }

    private fun setUpToolbar(t: Toolbar) {
        t.inflateMenu(R.menu.menu_on_details)
        t.menu.findItem(R.id.edit).setOnMenuItemClickListener { navigateToEditAppointment();true }
        t.menu.findItem(R.id.delete).setOnMenuItemClickListener { delete();true }

        t.setNavigationIcon(R.drawable.outline_arrow_back_24)
        t.setNavigationOnClickListener { finish() }
    }

    private fun navigateToEditAppointment(){
        val intent= Intent(this, EditAppointmentActivity::class.java)
        intent.putExtra(APPOINTMENT_ITEM,id)
        startActivity(intent)
    }

    private fun delete(){
        showOkCancelDialog(R.string.delete_dialog_title,R.string.delete_dialog_message,R.string.ok,R.string.cancel,{
            viewModel.delete({
                finish()
            },{
                Toast.makeText(this,getString(R.string.deletion_failed),Toast.LENGTH_LONG).show()
                it.printStackTrace()
            })
        },{})
    }

}