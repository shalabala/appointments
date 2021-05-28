package com.utamas.appointments.activity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.utamas.appointments.architecture.abstractions.ImageUtils
import com.utamas.appointments.databinding.AppointmentRowLayoutBinding
import com.utamas.appointments.model.validFor
import com.utamas.appointments.viewmodel.AppointmentDisplayItem
import java.time.LocalDateTime


private typealias Item= AppointmentDisplayItem
private typealias OnItemClickListener=(View, AppointmentListItemAdapter.ViewHolder)->Unit

class AppointmentListItemAdapter (private val imageService: ImageUtils):
        RecyclerView.Adapter<AppointmentListItemAdapter.ViewHolder>(){

    var onItemClickListener:OnItemClickListener?=null

    private var lastQuery: Query= Query("",true)

    private val sortedData: SortedList<Item> =
        SortedList(Item::class.java,SortedListCallback<Item>(this) { a, b->a.appointment.validFor.compareTo(b.appointment.validFor)})
    private val allData= HashSet<Item>()




    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {

        val binding=AppointmentRowLayoutBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(sortedData[position])
        if (onItemClickListener!=null){
            viewHolder.setItemClickListener (onItemClickListener!!)
        }
    }

    override fun getItemCount() = sortedData.size()



    fun query(query: String){
        lastQuery.queryString=query
        filter()
    }

    fun setShowOverdue(checked: Boolean) {
        lastQuery.showOverdue=checked
        filter()
    }

    private fun filter() {
        val set= getFiltered()
        replaceAllInSortedList(set)
    }

    private fun getFiltered(): Set<Item> {
        val set=HashSet<Item>()
        allData.filter{
            lastQuery.test(it)
        }.forEach{set.add(it)}
        return set
    }

    fun addAll(items: List<Item>) {
        allData.addAll(items)
        filter()
    }

    fun clear() {
        sortedData.replaceAll(emptyList())
        allData.clear()
    }

    private fun replaceAllInSortedList(appointments: Set<Item>) {
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




    private class Query (var queryString: String, var showOverdue: Boolean){
        fun test(item: Item): Boolean{
            return item.appointment.description!=null&& item.appointment.category!=null &&
                    (item.appointment.description.toLowerCase().contains(queryString.toLowerCase()) ||
                            item.appointment.category.toLowerCase().contains(queryString.toLowerCase())) &&
                    (item.appointment.validFor>LocalDateTime.now()||showOverdue)

        }
    }

    inner class ViewHolder(private val binding: AppointmentRowLayoutBinding): RecyclerView.ViewHolder(binding.root){
        var item: Item? =null
        private set

        fun bind(item: Item){
            binding.item=item
            this.item=item
        }

        fun setItemClickListener(listener: OnItemClickListener){
            binding.root.setOnClickListener {  view->listener(view,this@ViewHolder)}
        }
    }
}

