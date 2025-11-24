package com.example.huerto_hogar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huerto_hogar.network.CurrentWeather
import com.example.huerto_hogar.network.WeatherApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para manejo del clima desde API externa Open-Meteo
 * 
 * Diferencias con nuestros microservicios:
 * - Esta es una API de terceros (no la controlamos)
 * - Endpoint y modelo fijos (no podemos modificarlos)
 * - Retorna datos meteorológicos públicos
 */
class WeatherViewModel : ViewModel() {
    
    private val weatherApi = WeatherApiClient.apiService
    
    // Coordenadas de Viña del Mar, Chile (ubicación del proyecto)
    private val latitude = -33.0472
    private val longitude = -71.6127
    
    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()
    
    init {
        loadWeather()
    }
    
    /**
     * Cargar datos meteorológicos desde API externa
     */
    fun loadWeather() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            
            try {
                val response = weatherApi.getCurrentWeather(
                    latitude = latitude,
                    longitude = longitude
                )
                
                if (response.isSuccessful && response.body() != null) {
                    val weatherData = response.body()!!.current
                    _uiState.update { 
                        it.copy(
                            weather = weatherData,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "Error al obtener datos del clima"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error de conexión: ${e.message}"
                    )
                }
            }
        }
    }
}

/**
 * Estado de la UI para el clima
 */
data class WeatherUiState(
    val weather: CurrentWeather? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
