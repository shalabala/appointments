package com.utamas.appointments.architecture.abstractions

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

interface UserService {
    val currentUser: FirebaseUser?
    val isUserLoggedIn: Boolean
    val googleSignInIntent: Intent

    fun handleGoogleSignInResult(data: Intent?, onComplete: (Task<AuthResult>) -> Unit, onExcept: (Throwable) -> Unit)
    fun registerWithUsernameAndPassword(email: String, pass: String, callback: (Task<AuthResult>) -> Unit): Unit
    fun signInWithEmailAndPassword(email: String, pass: String, callback: (Task<AuthResult>) -> Unit)
    fun signOut(): Unit
}