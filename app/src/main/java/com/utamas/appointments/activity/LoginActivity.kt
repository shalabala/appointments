package com.utamas.appointments.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.utamas.appointments.R
import com.utamas.appointments.architecture.abstractions.BaseActivity
import com.utamas.appointments.architecture.abstractions.UserService
import com.utamas.appointments.architecture.annotations.DeclareViewModel
import com.utamas.appointments.architecture.annotations.DeclareXmlLayout
import com.utamas.appointments.viewmodel.LoginViewModel
import javax.inject.Inject


@DeclareXmlLayout(R.layout.activity_login)
@DeclareViewModel(LoginViewModel::class)
class LoginActivity : BaseActivity<LoginViewModel>() {
    private val REGISTER_REQUEST=1;
    private val GOOGLE_REQUEST=10;
    private val TAG=this::class.qualifiedName
    private lateinit var userName: EditText
    private lateinit var password: EditText

    @Inject
    lateinit var userService: UserService

    override fun onStart() {
        super.onStart()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appointmentApplication.appComponent.inject(this)

        userName = findViewById<EditText>(R.id.editTextUserName)
        password = findViewById<EditText>(R.id.editTextPassword)
    }

    fun checkUser() :Unit{
        if(userService.currentUser!=null){
            navigateToListAppointmentActivity()
        }
    }


    fun login(v: View):Unit{
        viewModel.login(onLogin(getString(R.string.unsuccessful_login)))
    }
    fun navigateToRegister(v: View): Unit{
        val intent= Intent(this, RegisterActivity::class.java)
        startActivityForResultIfIntentReceivable(intent, REGISTER_REQUEST)
    }
    fun loginWithGoogle(v: View): Unit{
        startActivityForResultIfIntentReceivable(userService.googleSignInIntent, GOOGLE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==REGISTER_REQUEST){
            when(resultCode){
                Activity.RESULT_OK -> Toast.makeText(
                    this,
                    getString(R.string.successful_registration),
                    Toast.LENGTH_LONG
                ).show()
                Activity.RESULT_FIRST_USER -> Toast.makeText(
                    this,
                    getString(R.string.unsuccessful_registration),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        if(requestCode==GOOGLE_REQUEST){
            userService.handleGoogleSignInResult(data,
                onLogin(getString(R.string.unsuccessful_google_login)),
                {
                    Log.d(TAG, "google login error: ", it)
                    Toast.makeText(this, getString(R.string.checkConnection), Toast.LENGTH_LONG)
                        .show()
                })
        }
    }
    private fun onLogin(errorMessage: String): (Task<AuthResult>) -> Unit {
        return{
        if(it.isSuccessful){
            navigateToListAppointmentActivity()
        }else{
            Log.d(TAG, "login error: ", it.exception)
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        }
    }}
    private fun navigateToListAppointmentActivity(){
        val intent=Intent(this, ListAppointmentsActivity::class.java)
        startActivity(intent)
    }
}