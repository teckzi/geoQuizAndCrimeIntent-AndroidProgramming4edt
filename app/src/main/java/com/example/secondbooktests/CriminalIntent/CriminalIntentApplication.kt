package com.example.secondbooktests.CriminalIntent

import android.app.Application

class CriminalIntentApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CrimeRepository.initialize(this)
    }
}