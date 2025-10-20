package com.example.huerto_hogar

import android.app.Application
import com.example.huerto_hogar.data.LocalDataRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class HuertoApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Inicializar el repository con SQLite
        // Cambiar useDatabase a true para usar SQLite, false para datos en memoria
        val useDatabase = true
        LocalDataRepository.initialize(this, useDatabase)
        
        // Si se usa la base de datos, poblar con los datos iniciales
        if (useDatabase) {
            kotlinx.coroutines.GlobalScope.launch {
                LocalDataRepository.populateDatabaseFromLocalData()
            }
        }
    }
}