package com.utamas.appointments

import android.content.Context
import android.widget.EditText
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

fun isEmail(text: String): Boolean {
    return text.matches(Regex("\\w+@\\w+\\.\\w{2,4}"))
}
fun validatePasswordAgainField(passwordAgain: EditText, password: String, context: Context): Boolean {
    var ret=true;
    if (passwordAgain.text.toString() != password) {
        passwordAgain.error = context.getString(R.string.passwords_dont_equal)
        ret = false
    }
    return ret
}

fun validateEmailField(email: EditText, context: Context):Boolean {
    var ret = true
    if (email.text.isNullOrEmpty()) {
        email.error = context.getString(R.string.reqired)
        ret = false
    } else if (!isEmail(email.text.toString())) {
        email.setError(context.getString(R.string.must_be_valid_email))
    }
    return ret;
}
fun validatePasswordField(password: EditText, context: Context): Boolean{
    var ret = true
    if (password.text.isNullOrEmpty()) {
        password.error = context.getString(R.string.reqired)
        ret = false

    } else if (password.text.length < 6) {
        password.error = context.getString(R.string.password_min_length)
        ret = false

    }
    return ret;
}
