package com.utamas.appointments.activity

import android.Manifest
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.utamas.appointments.R
import com.utamas.appointments.activity.dialog.ImageDialog
import com.utamas.appointments.activity.dialog.PersonDialog
import com.utamas.appointments.architecture.abstractions.BaseActivity
import com.utamas.appointments.architecture.annotations.DeclareViewModel
import com.utamas.appointments.architecture.annotations.DeclareXmlLayout
import com.utamas.appointments.viewmodel.EditAppointmentViewModel
import java.lang.AssertionError
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


@DeclareXmlLayout(R.layout.activity_edit_appointment)
@DeclareViewModel(EditAppointmentViewModel::class)
class EditAppointmentActivity : BaseActivity<EditAppointmentViewModel>() {
    private lateinit var personChipGroup: ChipGroup
    private lateinit var dateTextField: TextInputEditText
    private lateinit var timeTextField: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        personChipGroup=findViewById(R.id.chipGroupPerson)
        dateTextField=findViewById(R.id.dateTextInputEditText)
        timeTextField=findViewById(R.id.timeTextInputEditText)

        dateTextField.setOnTouchListener{a,b->
            openDatePicker()
            true
        }
        timeTextField.setOnTouchListener{a,b->
            openTimePicker()
            true
        }
        performManualBindings()

    }

    private fun performManualBindings() {
        viewModel.people.addOnListChangedCallback( CustomListObserver(viewModel.people,personChipGroup,{it.first}))
        viewModel.notes.addOnListChangedCallback(CustomListObserver(viewModel.notes,personChipGroup))
    }

    fun addPerson(v : View){
        PersonDialog(viewModel.people).show(supportFragmentManager,"personTag")
    }
    fun addNote(v: View){

    }


    fun getImage(v: View){

//        ImageDialog(this::imageDialogCallback)
    }

    /*private fun checkStoragePermission(){
        return ContextCompat.checkSelfPermission(this,
        Manifest.permission.)
    }*/
    fun imageDialogCallback(option: ImageDialog.Options){

    }

    fun openDatePicker(){
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText(getResources().getString(R.string.pick_a_date))
                .setSelection(viewModel.validFor.get()!!.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .setCalendarConstraints(CalendarConstraints.Builder().setStart(MaterialDatePicker.todayInUtcMilliseconds()).build())
                .build()

        datePicker.addOnPositiveButtonClickListener {
            val date= Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
            val time=viewModel.validFor.get()!!.toLocalTime()
            viewModel.validFor.set(LocalDateTime.of(date,time))
            dateTextField.text=
                SpannableStringBuilder(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(date))

        }
        datePicker.show(supportFragmentManager,"dateTag")
    }
    fun openTimePicker(){
        val timePicker =
            MaterialTimePicker.Builder()
                .setTitleText(getResources().getString(R.string.pick_a_time))
                .setHour(viewModel.validFor.get()!!.hour)
                .setMinute(viewModel.validFor.get()!!.minute)
                .build()

        timePicker.addOnPositiveButtonClickListener {
            val date= viewModel.validFor.get()!!.toLocalDate()
            val time= LocalTime.of(timePicker.hour,timePicker.minute)
            viewModel.validFor.set(LocalDateTime.of(date,time))
            timeTextField.text=
                SpannableStringBuilder(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).format(time))

        }
        timePicker.show(supportFragmentManager,"timeTag")
    }
    private fun createChip(str: String): Chip {
        val chip = Chip(this)
        chip.text = str
        chip.isCloseIconVisible = true
        return chip
    }

    inner class CustomListObserver<T>(private val observableData:ObservableArrayList<T>, private val cGroup:ChipGroup, private val converter:(T)->String={it.toString()}) : ObservableList.OnListChangedCallback<ObservableArrayList<T>>(){
        val listOfChips= mutableListOf<Chip>()


        init {
            onItemRangeInserted(observableData,0,observableData.size)
        }

        override fun onChanged(sender: ObservableArrayList<T>?) {
        }

        override fun onItemRangeRemoved(
            sender: ObservableArrayList<T>?,
            positionStart: Int,
            itemCount: Int
        ) {
            for (i in positionStart+itemCount-1 downTo positionStart){
                cGroup.removeView(listOfChips[i])
                listOfChips.removeAt(i)

            }
        }

        override fun onItemRangeMoved(
            sender: ObservableArrayList<T>?,
            fromPosition: Int,
            toPosition: Int,
            itemCount: Int
        ) {
            throw AssertionError("this should not happen")
        }

        override fun onItemRangeInserted(
            sender: ObservableArrayList<T>?,
            positionStart: Int,
            itemCount: Int
        ) {
            if (sender==null) return
            for(i in positionStart until positionStart+itemCount){
                val chip=createChip(converter(sender[i]))
                chip.setOnCloseIconClickListener{
                    observableData.remove(sender[i])
                }
                cGroup.addView(chip)
                if(i==listOfChips.size){
                    listOfChips.add(chip)
                }else throw Error("this should never happen, only end of list insertion is allowed")
            }

        }

        override fun onItemRangeChanged(
            sender: ObservableArrayList<T>?,
            positionStart: Int,
            itemCount: Int
        ) {
        }

    }
}