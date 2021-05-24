package com.utamas.appointments.activity.adapter

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.utamas.appointments.model.Appointment

class SortedListCallback(private val mAdapter: RecyclerView.Adapter<*>) : SortedList.Callback<Appointment>(){
    override fun areItemsTheSame(item1: Appointment?, item2: Appointment?): Boolean =item1===item2

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        mAdapter.notifyItemMoved(fromPosition, toPosition);    }

    override fun onChanged(position: Int, count: Int) {
        mAdapter.notifyItemRangeChanged(position, count);    }

    override fun onInserted(position: Int, count: Int) {
        mAdapter.notifyItemRangeInserted(position, count);    }

    override fun onRemoved(position: Int, count: Int) {
        mAdapter.notifyItemRangeRemoved(position, count);    }

    override fun compare(o1: Appointment?, o2: Appointment?): Int {
        if (o1==null||o2==null){
            val o1Comp=if (o1!=null) -1 else 0
            val o2Comp=if (o2!=null) -1 else 0
            return o1Comp+o2Comp
        }
        return o1.validFor.compareTo(o2.validFor)
    }

    override fun areContentsTheSame(oldItem: Appointment?, newItem: Appointment?): Boolean = oldItem==newItem


}