package com.example.huerto_hogar.network

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interfaz de servicios REST para Open-Meteo API (API Externa)
 * Documentación: https://open-meteo.com/
 * 
 * Esta es una API EXTERNA (diferente a nuestros microservicios propios)
 * - No requiere API key
 * - Gratuita
 * - Retorna datos meteorológicos en tiempo real
 */
interface WeatherApiService {
    
    /**
     * Obtener datos meteorológicos actuales
     * @param latitude Latitud de la ubicación
     * @param longitude Longitud de la ubicación
     * @param current Parámetros actuales a obtener (temperatura, viento, etc.)
     */
    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,wind_speed_10m"
    ): Response<WeatherResponse>
}

/**
 * Respuesta de la API de clima
 */
data class WeatherResponse(
    @SerializedName("current")
    val current: CurrentWeather
)

/**
 * Datos meteorológicos actuales
 */
data class CurrentWeather(
    @SerializedName("time")
    val time: String,
    
    @SerializedName("temperature_2m")
    val temperature: Double,
    
    @SerializedName("wind_speed_10m")
    val windSpeed: Double
)

/**
 * Cliente Retrofit para Open-Meteo API
 */
object WeatherApiClient {
    private const val BASE_URL = "https://api.open-meteo.com/"
    
    val apiService: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}
