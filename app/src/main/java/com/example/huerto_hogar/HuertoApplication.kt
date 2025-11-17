package com.example.huerto_hogar

import android.app.Application
import com.example.huerto_hogar.data.LocalDataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class HuertoApplication : Application() {
    
    // Scope para operaciones de la aplicación
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    override fun onCreate() {
        super.onCreate()
        
        // Inicializar el repository con SQLite
        // Cambiar useDatabase a true para usar SQLite, false para datos en memoria
        val useDatabase = true
        LocalDataRepository.initialize(this, useDatabase)
        
        // Si se usa la base de datos, poblar con los datos iniciales
        if (useDatabase) {
            applicationScope.launch {
                LocalDataRepository.populateDatabaseFromLocalData()
            }
        }
    }
}