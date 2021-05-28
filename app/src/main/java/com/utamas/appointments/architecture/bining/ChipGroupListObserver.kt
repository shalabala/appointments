package com.utamas.appointments.architecture.bining

import android.content.Context
import androidx.databinding.ObservableList
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.utamas.appointments.architecture.createChip
import java.lang.ref.WeakReference

class ChipGroupListObserver<T>(
    context: Context,
    private val observableData: ObservableList<T>,
    private val cGroup: ChipGroup,
    private val converter: (T) -> String = { it.toString() }
) : ObservableList.OnListChangedCallback<ObservableList<T>>() {

    private val contextRef: WeakReference<Context>
    val listOfChips = mutableListOf<Chip>()


    init {
        contextRef = WeakReference(context)
        observableData.forEach{createAndSetUpChip(it)}
    }

    override fun onChanged(sender: ObservableList<T>?) {
    }

    override fun onItemRangeRemoved(
        sender: ObservableList<T>?,
        positionStart: Int,
        itemCount: Int
    ) {
        for (i in positionStart + itemCount - 1 downTo positionStart) {
            cGroup.removeView(listOfChips[i])
            listOfChips.removeAt(i)

        }
    }

    override fun onItemRangeMoved(
        sender: ObservableList<T>?,
        fromPosition: Int,
        toPosition: Int,
        itemCount: Int
    ) {
        throw AssertionError("this should not happen")
    }

    override fun onItemRangeInserted(
        sender: ObservableList<T>?,
        positionStart: Int,
        itemCount: Int
    ) {
        if (sender == null) return
        for (i in positionStart until positionStart + itemCount) {
            if (i == listOfChips.size) {
                createAndSetUpChip(sender[i])
            } else throw Error("this should never happen, only end of list insertion is allowed")
        }
    }


    private fun createAndSetUpChip(data: T) {
        val chip = createCloseableChip(converter(data))
        if (chip != null) {
            setUpChip(chip, data)
            addChip(chip)
        }

    }

    private fun setUpChip(chip: Chip, data: T) {
        chip.setOnCloseIconClickListener {
            observableData.remove(data)
        }

    }

    private fun addChip(chip: Chip) {
        cGroup.addView(chip)
        listOfChips.add(chip)
    }

    override fun onItemRangeChanged(
        sender: ObservableList<T>?,
        positionStart: Int,
        itemCount: Int
    ) {
    }

    private fun createCloseableChip(str: String): Chip? {
        val context = contextRef.get()
        if (context != null) {
            return createChip(str, context, true)
        }
        return null
    }

}

