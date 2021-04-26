package com.utamas.appointments.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import com.utamas.appointments.*;
import com.utamas.appointments.R
import com.utamas.appointments.architecture.abstractions.BaseActivity
import com.utamas.appointments.architecture.annotations.DeclareViewModel
import com.utamas.appointments.architecture.annotations.DeclareXmlLayout
import com.utamas.appointments.viewmodel.RegisterViewModel

@DeclareViewModel(RegisterViewModel::class)
@DeclareXmlLayout(R.layout.activity_register)
class RegisterActivity : BaseActivity<RegisterViewModel>() {
    private val TAG = this::class.qualifiedName
    private lateinit var userName: EditText
    private lateinit var password: EditText
    private lateinit var passwordAgain: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userName = findViewById<EditText>(R.id.editTextUserName)
        password = findViewById<EditText>(R.id.editTextPassword)
        passwordAgain = findViewById<EditText>(R.id.editTextAgain)
    }

    fun register(view: View): Unit {
        if (validateFields()) {
            viewModel.register {
                if (it.isSuccessful) {
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    setResult(Activity.RESULT_FIRST_USER)
                    Log.d(TAG, "error: ", it.exception)
                    finish()
                }
            }
        }
    }

    fun cancel(v: View): Unit {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun validateFields(): Boolean =
        validateEmailField(userName,this)&& validatePasswordField(password,this)&& validatePasswordAgainField(passwordAgain,password.text.toString(),this)

}