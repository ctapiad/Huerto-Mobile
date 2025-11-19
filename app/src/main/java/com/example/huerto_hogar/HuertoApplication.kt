package com.example.huerto_hogar

import android.app.Application
import android.util.Log

/**
 * HuertoApplication - Simplificado sin base de datos local
 * 
 * Ya no necesita inicializar Room ni poblar datos locales.
 * Toda la data proviene de las APIs de MongoDB.
 */
class HuertoApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        try {
            Log.d("HuertoApplication", "Application onCreate started")
            // Ya no necesitamos inicializar bases de datos locales
            // Los datos vienen de la API de MongoDB
            Log.d("HuertoApplication", "Application onCreate completed successfully")
        } catch (e: Exception) {
            Log.e("HuertoApplication", "Error in Application onCreate", e)
        }
    }
}