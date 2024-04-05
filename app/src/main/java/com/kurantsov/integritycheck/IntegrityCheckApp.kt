package com.kurantsov.integritycheck

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class IntegrityCheckApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )
        Log.e("TEST", "Static field: $API_KEY")
        Log.e("TEST", "BuildConfig field: ${BuildConfig.API_KEY}")
        Log.e("TEST", "Res field: ${getString(R.string.api_key)}")
        Log.e("TEST", "Field from native: ${NativeSecrets.getApiKeyFromNative()}")
    }

    companion object {
        const val API_KEY = "SECRET_API_KEY"
    }
}