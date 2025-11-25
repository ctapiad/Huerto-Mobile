package com.example.huerto_hogar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.huerto_hogar.data.LocalDataRepository
import com.example.huerto_hogar.data.model.User
import kotlinx.coroutines.flow.StateFlow

/**
 * AuthViewModel - Solo maneja el estado de usuario y logout
 * 
 * El login se maneja en LoginViewModel con la API de MongoDB.
 * No hay funcionalidad de registro en la app actual.
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {
    
    val currentUser: StateFlow<User?> = LocalDataRepository.currentUser
    
    fun logout() {
        LocalDataRepository.logout()
    }
}