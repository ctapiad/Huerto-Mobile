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
            setContentView(R.layout.activity_splash)
            Log.d("SplashActivity", "setContentView successful")

            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    Log.d("SplashActivity", "Starting MainActivity")
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    Log.e("SplashActivity", "Error starting MainActivity", e)
                }
            }, 2500) // 2.5 segundos
        } catch (e: Exception) {
            Log.e("SplashActivity", "Error in onCreate", e)

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