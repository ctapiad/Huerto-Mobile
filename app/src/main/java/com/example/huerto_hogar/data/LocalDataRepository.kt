package com.example.huerto_hogar.data

import com.example.huerto_hogar.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


object LocalDataRepository {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()
    
    fun setCurrentUser(user: User?) {
        _currentUser.value = user
    }
    
    fun logout() {
        _currentUser.value = null
    }
}  