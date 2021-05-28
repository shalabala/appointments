package com.utamas.appointments.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.utamas.appointments.APPOINTMENT_ITEM
import com.utamas.appointments.R
import com.utamas.appointments.activity.adapter.AppointmentListItemAdapter
import com.utamas.appointments.architecture.abstractions.BaseActivity
import com.utamas.appointments.architecture.abstractions.ImageUtils
import com.utamas.appointments.architecture.abstractions.UserService
import com.utamas.appointments.architecture.annotations.DeclareViewModel
import com.utamas.appointments.architecture.annotations.DeclareXmlLayout
import com.utamas.appointments.viewmodel.AppointmentDisplayItem
import com.utamas.appointments.viewmodel.EditAppointmentViewModel
import com.utamas.appointments.viewmodel.ListAppointmentsViewModel
import javax.inject.Inject
import kotlin.random.Random

@DeclareViewModel(ListAppointmentsViewModel::class)
@DeclareXmlLayout(R.layout.activity_list_appointments)
class ListAppointmentsActivity : BaseActivity<ListAppointmentsViewModel>(),
    SearchView.OnQueryTextListener {

    @Inject
    lateinit var imageUtils: ImageUtils

    @Inject
    lateinit var userService: UserService


    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppointmentListItemAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appointmentApplication.appComponent.inject(this)
        setUpToolbar(findViewById<Toolbar>(R.id.toolbar))
        setUpRecyclerView()

    }


    override fun onResume() {
        super.onResume()
        reloadAdapterItems()
        viewModel.checkForOverdue({
            if(it.size>0){
                notifications.showAppointmentNotification(
                    String.format(getString(R.string.number_of_overdue_appointments),
                        it.size)
                    ,System.currentTimeMillis().toInt())
            }},{it.printStackTrace()})

    }

    private fun reloadAdapterItems(){
        viewModel.loadAppointments({ adapter.clear();adapter.addAll(it) },
            {
                Toast.makeText(
                    this,
                    resources.getString(R.string.list_appointment_error),
                    Toast.LENGTH_LONG
                ).show()
                it.printStackTrace()
            })
    }

    private fun setUpRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AppointmentListItemAdapter(imageUtils)
        adapter.onItemClickListener={view,viewholder->
            if(viewholder.item!=null) {
                navigateToViewAppointment(viewholder.item!!)
            }}
        recyclerView.adapter = adapter

    }

    private fun navigateToViewAppointment(item: AppointmentDisplayItem) {
        val intent=Intent(this,ViewAppointmentActivity::class.java)
        intent.putExtra(APPOINTMENT_ITEM,item.appointment.id)
        startActivity(intent)
    }

    private fun setUpToolbar(t: Toolbar) {
        t.inflateMenu(R.menu.menu_on_list)
        val searchItem = t.menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        t.menu.findItem(R.id.logout).setOnMenuItemClickListener {
            userService.signOut()
            finish()
            true
        }
        t.menu.findItem(R.id.overdue).setOnMenuItemClickListener {
            val switchToShow=it.title==getString(R.string.show_overdue)
            this.adapter.setShowOverdue(switchToShow)
            it.title=if(switchToShow) getString(R.string.hide_overdue) else getString(R.string.show_overdue)
            true
        }
        searchView.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
        println(item.title)
    }

    override fun onBackPressed() {
        // super.onBackPressed()
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return true
    }

    fun navigateToNewAppointment(v: View) {
        val intent = Intent(this, EditAppointmentActivity::class.java)
        startActivity(intent)
    }

    override fun onQueryTextChange(query: String): Boolean {
        adapter.query(query)
        recyclerView.scrollToPosition(0)
        return true
    }
}