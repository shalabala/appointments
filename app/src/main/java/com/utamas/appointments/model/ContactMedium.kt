package com.utamas.appointments.model

data class ContactMedium(val name: String, val type: String, val contact: String){
    constructor(): this(
        name="",type="",contact=""
    )
}
