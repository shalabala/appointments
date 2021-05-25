package com.utamas.appointments.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.utamas.appointments.R
import com.utamas.appointments.activity.adapter.AppointmentListItemAdapter
import com.utamas.appointments.architecture.abstractions.BaseActivity
import com.utamas.appointments.architecture.abstractions.ImageUtils
import com.utamas.appointments.architecture.abstractions.UserService
import com.utamas.appointments.architecture.annotations.DeclareViewModel
import com.utamas.appointments.architecture.annotations.DeclareXmlLayout
import com.utamas.appointments.model.Appointment
import com.utamas.appointments.model.AppointmentStatus
import com.utamas.appointments.viewmodel.ListAppointmentsViewModel
import java.time.LocalDateTime
import java.util.HashSet
import javax.inject.Inject

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
        val toolbar=findViewById(R.id.toolbar) as Toolbar;
        setUpToolbar(toolbar)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AppointmentListItemAdapter(imageUtils)
        recyclerView.adapter=adapter
        adapter.addAll(appointments)


    }
    fun setUpToolbar(t: Toolbar) {
        t.inflateMenu(R.menu.menu_on_list)
        val searchItem=t.menu.findItem(R.id.action_search)
        val searchView=searchItem.actionView as SearchView
        t.menu.findItem(R.id.logout).setOnMenuItemClickListener {
            userService.signOut()
            finish()
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
    fun navigateToNewAppointment(v: View){
        val intent= Intent(this,EditAppointmentActivity::class.java)
        startActivity(intent)
    }

    override fun onQueryTextChange(query: String): Boolean {
        val filtered=filter(appointments,query)
        adapter.replaceAll(filtered)
        recyclerView.scrollToPosition(0)
        return true
    }

    private fun filter(appointments: List<Appointment>, query: String): Set<Appointment> {
        val set=HashSet<Appointment>()
        appointments.forEach{if (it.description.toLowerCase().contains(query.toLowerCase())) set.add(it)}
        return set;

    }
}