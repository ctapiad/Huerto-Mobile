package com.example.huerto_hogar

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Usar el layout XML simple
        setContentView(R.layout.activity_splash)
        
        // Retrasar por 2.5 segundos y luego ir a MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Cerrar SplashActivity para que no se pueda volver
        }, 2500) // 2.5 segundos
    }
}