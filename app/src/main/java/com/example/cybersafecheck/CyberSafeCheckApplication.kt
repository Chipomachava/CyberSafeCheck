package com.example.cybersafecheck

import android.app.Application

class CyberSafeCheckApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PreferencesRepository.initialize(this)
    }
}