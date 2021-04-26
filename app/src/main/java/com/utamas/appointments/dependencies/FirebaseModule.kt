package com.utamas.appointments.dependencies

import android.app.Application
import android.provider.Settings
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.utamas.appointments.R
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class FirebaseModule{
    private val webClientId="259239252898-ohuemkaghsuo9kidegl90arg9kv58k8g.apps.googleusercontent.com"

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth= Firebase.auth
    @Provides
    @Singleton
    fun provideGoogleAuth(app: Application): GoogleSignInClient {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(app, gso)

    }
}