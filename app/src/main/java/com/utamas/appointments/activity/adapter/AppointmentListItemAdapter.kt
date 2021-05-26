package com.utamas.appointments.activity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.utamas.appointments.architecture.abstractions.ImageUtils
import com.utamas.appointments.databinding.AppointmentRowLayoutBinding
import com.utamas.appointments.model.Appointment
import com.utamas.appointments.model.validFor
import com.utamas.appointments.viewmodel.ListAppointmentsViewModel



private typealias Item=ListAppointmentsViewModel.AppointmentDisplayItem


class AppointmentListItemAdapter (private val imageService: ImageUtils):
        RecyclerView.Adapter<AppointmentListItemAdapter.ViewHolder>(){


    private val sortedData: SortedList<Item> =
        SortedList(Item::class.java,SortedListCallback<Item>(this) { a, b->a.appointment.validFor.compareTo(b.appointment.validFor)})
    private val allData= HashSet<Item>()




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



    fun query(query: String){
        val filtered=filter(allData,query)
        replaceAllInSortedList(filtered)
    }

    private fun filter(appointments: Set<Item>, query: String): Set<Item> {
        val set= HashSet<Item>()
        appointments.forEach{if (it.appointment.description!=null&& it.appointment.description.toLowerCase().toLowerCase().contains(query.toLowerCase())) set.add(it)}
        return set;
    }

    fun addAll(items: List<Item>) {
        sortedData.addAll(items)
        allData.addAll(items)
    }


    fun replaceAllInSortedList(appointments: Set<Item>) {
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

    fun clear() {
        sortedData.replaceAll(emptyList())
    }

    inner class ViewHolder(private val binding: AppointmentRowLayoutBinding): RecyclerView.ViewHolder(binding.root){
        /*val imageView: ImageView = view.findViewById(R.id.imageTextView)
        val nameTextView: TextView = view.findViewById(R.id.textViewName)
        val dateTextView: TextView=view.findViewById(R.id.textViewDate)*/

        fun bind(item: Item){
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

}

