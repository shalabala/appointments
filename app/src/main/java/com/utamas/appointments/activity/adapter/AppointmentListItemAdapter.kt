package com.utamas.appointments.activity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.utamas.appointments.R
import com.utamas.appointments.architecture.abstractions.ImageService
import com.utamas.appointments.model.Appointment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

class AppointmentListItemAdapter (private val imageService: ImageService):
        RecyclerView.Adapter<AppointmentListItemAdapter.ViewHolder>(){
    inner class ViewHolder(view : View): RecyclerView.ViewHolder(view){
        val imageView: ImageView = view.findViewById(R.id.imageTextView)
        val nameTextView: TextView = view.findViewById(R.id.textViewName)
        val dateTextView: TextView=view.findViewById(R.id.textViewDate)

        fun bind(appointment: Appointment){
            val imageResource=appointment.attachments.firstOrNull()
            if(imageResource!=null) {
                imageService.getImage(imageResource).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe{ bitmap, throwable ->if (bitmap!=null) this.imageView.setImageBitmap(bitmap)  }
            }
            this.nameTextView.text = appointment.description
            this.dateTextView.text= appointment.validFor.toNiceString()
        }

    }

    private val sortedData: SortedList<Appointment> =
        SortedList(Appointment::class.java,SortedListCallback(this))

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.appointment_row_layout, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(sortedData[position])
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = sortedData.size()

    fun addAll(appointment: Appointment) {
        sortedData.add(appointment)
    }

    fun removeAll(appointment: Appointment) {
        sortedData.remove(appointment)
    }

    fun addAll(appointments: List<Appointment>) {
        sortedData.addAll(appointments)
    }

    fun removeAll(appointments: List<Appointment>) {
        sortedData.replaceAll(appointments)
    }
    fun replaceAll( appointments: Set<Appointment>) {
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
fun LocalDateTime.toNiceString(): String{
    val date=this.toLocalDate()
    val time=this.toLocalTime()
    val medFormatter=DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    val shortFormatter=DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault())
    return "${medFormatter.format(date)}\n" +
            "${shortFormatter.format(time)}"
   // return "${this.year}.${this.month}.${this.dayOfMonth} ${this.hour} : ${this.minute}"
}
