package com.example.huerto_hogar

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("SplashActivity", "onCreate started")
        
        try {
            // Usar el layout XML simple
            setContentView(R.layout.activity_splash)
            Log.d("SplashActivity", "setContentView successful")
            
            // Retrasar por 2.5 segundos y luego ir a MainActivity
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    Log.d("SplashActivity", "Starting MainActivity")
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Cerrar SplashActivity para que no se pueda volver
                } catch (e: Exception) {
                    Log.e("SplashActivity", "Error starting MainActivity", e)
                }
            }, 2500) // 2.5 segundos
        } catch (e: Exception) {
            Log.e("SplashActivity", "Error in onCreate", e)
            // Si falla, intentar ir directo a MainActivity
            try {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } catch (e2: Exception) {
                Log.e("SplashActivity", "Fatal error", e2)
            }
        }
    }
}