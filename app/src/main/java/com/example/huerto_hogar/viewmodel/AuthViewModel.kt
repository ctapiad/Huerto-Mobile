package com.example.huerto_hogar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.huerto_hogar.data.LocalDataRepository
import com.example.huerto_hogar.data.enums.UserRole
import com.example.huerto_hogar.data.model.User
import com.example.huerto_hogar.database.repository.DatabaseRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    
    private val databaseRepository = DatabaseRepository(application)
    
    val currentUser: StateFlow<User?> = LocalDataRepository.currentUser
    
    fun login(email: String, password: String, callback: (Result<User>) -> Unit) {
        viewModelScope.launch {
            try {
                val user = databaseRepository.loginUser(email, password)
                if (user != null) {
                    LocalDataRepository.setCurrentUser(user)
                    callback(Result.success(user))
                } else {
                    callback(Result.failure(Exception("Credenciales incorrectas")))
                }
            } catch (e: Exception) {
                callback(Result.failure(e))
            }
        }
    }
    
    fun register(name: String, email: String, password: String, comunaId: Long, callback: (Result<User>) -> Unit) {
        viewModelScope.launch {
            try {
                val newUser = User(
                    id = 0, // Se auto-genera
                    name = name,
                    email = email,
                    password = password,
                    registrationDate = Date(),
                    address = null,
                    phone = null,
                    comunaId = comunaId,
                    role = UserRole.CLIENTE
                )
                
                val result = databaseRepository.registerUser(newUser)
                result.fold(
                    onSuccess = { user ->
                        LocalDataRepository.setCurrentUser(user)
                        callback(Result.success(user))
                    },
                    onFailure = { exception ->
                        callback(Result.failure(exception))
                    }
                )
            } catch (e: Exception) {
                callback(Result.failure(e))
            }
        }
    }
    
    fun logout() {
        LocalDataRepository.logout()
    }
}