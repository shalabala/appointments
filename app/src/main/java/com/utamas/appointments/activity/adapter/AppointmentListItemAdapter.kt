package com.utamas.appointments.activity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.utamas.appointments.architecture.abstractions.ImageUtils
import com.utamas.appointments.databinding.AppointmentRowLayoutBinding
import com.utamas.appointments.model.Appointment
import com.utamas.appointments.viewmodel.ListAppointmentsViewModel

class AppointmentListItemAdapter (private val imageService: ImageUtils):
        RecyclerView.Adapter<AppointmentListItemAdapter.ViewHolder>(){

    inner class ViewHolder(private val binding: AppointmentRowLayoutBinding): RecyclerView.ViewHolder(binding.root){
        /*val imageView: ImageView = view.findViewById(R.id.imageTextView)
        val nameTextView: TextView = view.findViewById(R.id.textViewName)
        val dateTextView: TextView=view.findViewById(R.id.textViewDate)*/

        fun bind(item: ListAppointmentsViewModel.AppointmentDisplayItem){
            binding.item=item
            /*val imageResource=appointment.attachments.firstOrNull()
            if(imageResource!=null) {
                imageService.base64ToImage(imageResource).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe{ bitmap, throwable ->if (bitmap!=null) this.imageView.setImageBitmap(bitmap)  }
            }
            this.nameTextView.text = appointment.description
            this.dateTextView.text= appointment.validFor.toNiceString()*/
        }

    }

    private val sortedData: SortedList<ListAppointmentsViewModel.AppointmentDisplayItem> =
        SortedList(ListAppointmentsViewModel.AppointmentDisplayItem::class.java,SortedListCallback<ListAppointmentsViewModel.AppointmentDisplayItem>(this))

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {/*
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.appointment_row_layout, viewGroup, false)*/

        val binding=AppointmentRowLayoutBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(sortedData[position])
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = sortedData.size()

    fun addAll(item: ListAppointmentsViewModel.AppointmentDisplayItem) {
        sortedData.add(item)
    }

    fun removeAll(item: ListAppointmentsViewModel.AppointmentDisplayItem) {
        sortedData.remove(item)
    }

    fun addAll(items: List<ListAppointmentsViewModel.AppointmentDisplayItem>) {
        sortedData.addAll(items)
    }

    fun removeAll(appointments: List<ListAppointmentsViewModel.AppointmentDisplayItem>) {
        sortedData.replaceAll(appointments)
    }
    fun replaceAll( appointments: Set<ListAppointmentsViewModel.AppointmentDisplayItem>) {
        sortedData.beginBatchedUpdates();
        for (i in sortedData.size()-1 downTo 0) {
            val appointment = sortedData[i];
            if (!appointments.contains(appointment)) {
                sortedData.remove(appointment);
            }
        }
        sortedData.addAll(appointments);
        sortedData.endBatchedUpdates();
    }

}

