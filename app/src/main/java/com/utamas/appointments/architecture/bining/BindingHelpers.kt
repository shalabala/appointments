package com.utamas.appointments.architecture

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.view.View
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingConversion
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.utamas.appointments.R
import com.utamas.appointments.model.AppointmentStatus
import com.utamas.appointments.model.ContactMedium
import java.lang.AssertionError
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Function


@BindingAdapter("chipItems")
fun loadChipGroupItems(cg: ChipGroup, list: List<String>) {
    cg.removeAllViews()
    list.forEach{item->
        cg.addView(createChip(item,cg.context,false) )
    }
}

@BindingAdapter("backgroundDrawableOrBitmap")
fun backgroundDrawableOrBitmap(v:TextView, background:Any){
    if (background is Bitmap){
        v.background = BitmapDrawable(v.resources,background)
    }else if(background is Drawable){
        v.background=background
    }else throw AssertionError("given value is neither bitmap nor drawable")
}

fun <T> getDefaultIfNotProvided(provided: T?, default: T):T {
    if(provided==null){
        return default
    }
    return provided
}

@BindingConversion
fun convertContactListToStrings(list: List<ContactMedium>)=list.map { "${it.name} (${it.type}): ${it.contact}" }


fun convertContactString(contact: ContactMedium) = "${contact.name} (${contact.type}): ${contact.contact}"
@BindingConversion
fun appointmentStatusToString(appointmentStatus: AppointmentStatus?): Int{
    return when(appointmentStatus){
        null->R.string.empty_string
        AppointmentStatus.INITIALIZED->
            R.string.initialized_status
        AppointmentStatus.CONFIRMED ->
            R.string.confirmed_status
        AppointmentStatus.CANCELLED ->
            R.string.cancelled_status
        AppointmentStatus.COMPLETED->
            R.string.completed_status
        AppointmentStatus.FAILED->
            R.string.failed_status
    }
}

@BindingConversion
fun convertDrawableToBitmap(drawable: VectorDrawable)=drawable.toBitmap(drawable.intrinsicWidth,drawable.intrinsicHeight)

fun createChip(str: String, context: Context, closeable: Boolean): Chip {

    val chip = Chip(context)
    chip.text = str
    chip.isCloseIconVisible = closeable
    return chip

}