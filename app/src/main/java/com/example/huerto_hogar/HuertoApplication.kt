package com.example.huerto_hogar

import android.app.Application
import android.util.Log

class HuertoApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        try {
            Log.d("HuertoApplication", "Application onCreate started")
            Log.d("HuertoApplication", "Application onCreate completed successfully")
        } catch (e: Exception) {
            Log.e("HuertoApplication", "Error in Application onCreate", e)
        }
    }
}