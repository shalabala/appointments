package com.utamas.appointments.services

import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.utamas.appointments.architecture.abstractions.UserService
import javax.inject.Inject

class FirebaseUserService @Inject constructor(private val auth: FirebaseAuth, private val googleSignInClient: GoogleSignInClient) : UserService{
   private val TAG=this::class.qualifiedName
   /*  private val EMAIL="createWithEmail:"
    private val LOGIN="login:"
    private val REGISTER="register:"
    private val SUCCESS="success"
    private val FAIL="fail"*/

    override val currentUser: FirebaseUser?
        get() = auth.currentUser
    override val isUserLoggedIn: Boolean
        get()=currentUser!=null
    override val googleSignInIntent: Intent
        get() = googleSignInClient.signInIntent



    override fun handleGoogleSignInResult(data: Intent?, onComplete:(Task<AuthResult>)->Unit, onExcept:(Throwable)->Unit){
        val task= GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            // Google Sign In was successful, authenticate with Firebase
            val account = task.getResult(ApiException::class.java)!!
            firebaseAuthWithGoogle(account.idToken!!,onComplete)
        } catch (e: ApiException) {
            onExcept(e)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String, onComplete: (Task<AuthResult>) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(onComplete)
    }
    override fun registerWithUsernameAndPassword(email: String, pass: String, callback:(Task<AuthResult>)->Unit):Unit{
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(callback)
    }

    override fun signInWithEmailAndPassword(email: String, pass: String, callback: (Task<AuthResult>) -> Unit){
        auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(callback)
    }
    override fun signOut():Unit{
        auth.signOut()
    }


}