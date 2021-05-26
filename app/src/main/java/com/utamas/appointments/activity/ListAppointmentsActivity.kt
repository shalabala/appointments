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
import com.utamas.appointments.R
import com.utamas.appointments.activity.adapter.AppointmentListItemAdapter
import com.utamas.appointments.architecture.abstractions.BaseActivity
import com.utamas.appointments.architecture.abstractions.ImageUtils
import com.utamas.appointments.architecture.abstractions.UserService
import com.utamas.appointments.architecture.annotations.DeclareViewModel
import com.utamas.appointments.architecture.annotations.DeclareXmlLayout
import com.utamas.appointments.viewmodel.ListAppointmentsViewModel
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

        setUpToolbar(findViewById<Toolbar>(R.id.toolbar))
        setUpRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        reloadAdapterItems()
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
        recyclerView.adapter = adapter

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