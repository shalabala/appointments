package com.utamas.appointments.activity.adapter

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.utamas.appointments.model.Appointment

class SortedListCallback<T>(private val mAdapter: RecyclerView.Adapter<*>,private val comparator:(T,T)->Int) : SortedList.Callback<T>(){
    override fun areItemsTheSame(item1: T?, item2: T?): Boolean =item1===item2

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        mAdapter.notifyItemMoved(fromPosition, toPosition);    }

    override fun onChanged(position: Int, count: Int) {
        mAdapter.notifyItemRangeChanged(position, count);    }

    override fun onInserted(position: Int, count: Int) {
        mAdapter.notifyItemRangeInserted(position, count);    }

    override fun onRemoved(position: Int, count: Int) {
        mAdapter.notifyItemRangeRemoved(position, count);    }

    override fun compare(o1: T?, o2: T?): Int {
        if (o1==null||o2==null){
            val o1Comp=if (o1!=null) -1 else 0
            val o2Comp=if (o2!=null) -1 else 0
            return o1Comp+o2Comp
        }
        return comparator(o1,o2)
    }

    override fun areContentsTheSame(oldItem: T?, newItem: T?): Boolean = oldItem==newItem


}