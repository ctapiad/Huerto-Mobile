# 🌤️ Integración de API Externa - Open-Meteo
## Huerto Hogar - Evidencia de Consumo API de Terceros

**Fecha de integración:** 24 de noviembre de 2025  
**Requisito de rúbrica:** IE 3.1.4 (15% de la nota)

---

## 📋 Resumen

Se integró la **API pública Open-Meteo** para mostrar información meteorológica en tiempo real en el HomeScreen de la aplicación. Esta es una **API de terceros** (diferente a nuestros microservicios propios).

---

## 🎯 Requisito Cumplido

**IE 3.1.4**: "Consume una API externa desde la aplicación móvil, integrándola al flujo visual sin interferir con los microservicios propios"

✅ **API externa integrada:** Open-Meteo Weather API  
✅ **Consumida vía Retrofit:** Cliente Retrofit separado  
✅ **Mostrada en UI:** Widget compacto en HomeScreen  
✅ **No interfiere:** Datos independientes de microservicios  

---

## 🔧 Implementación Técnica

### 1. API Utilizada

**Nombre:** Open-Meteo Weather API  
**URL Base:** `https://api.open-meteo.com/`  
**Endpoint:** `GET /v1/forecast`  
**Autenticación:** No requiere API key (pública)  
**Documentación:** https://open-meteo.com/

### 2. Parámetros Utilizados

```
latitude: -33.0472  (Viña del Mar, Chile)
longitude: -71.6127
current: temperature_2m,wind_speed_10m
```

### 3. Respuesta de Ejemplo

```json
{
  "current": {
    "time": "2025-11-24T15:00",
    "temperature_2m": 18.5,
    "wind_speed_10m": 12.3
  }
}
```

---

## 📁 Archivos Creados

### WeatherApiService.kt

**Ubicación:** `app/src/main/java/com/example/huerto_hogar/network/WeatherApiService.kt`

**Contenido principal:**
```kotlin
interface WeatherApiService {
    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,wind_speed_10m"
    ): Response<WeatherResponse>
}

data class WeatherResponse(
    @SerializedName("current")
    val current: CurrentWeather
)

data class CurrentWeather(
    @SerializedName("temperature_2m")
    val temperature: Double,
    
    @SerializedName("wind_speed_10m")
    val windSpeed: Double
)

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
```

### WeatherViewModel.kt

**Ubicación:** `app/src/main/java/com/example/huerto_hogar/viewmodel/WeatherViewModel.kt`

**Características:**
- Consume WeatherApiService
- Maneja estados: loading, success, error
- Coordenadas fijas de Viña del Mar
- Integración con corrutinas (viewModelScope)

### Integración en HomeScreen.kt

**Modificaciones:**
- Agregado `weatherViewModel: WeatherViewModel` como parámetro
- Creado composable `WeatherWidget`
- Widget mostrado arriba de "Productos Destacados"
- Diseño compacto (60dp de altura)

---

## 🎨 Diseño UI

### Widget del Clima

**Características visuales:**
- Altura: 70dp (compacto pero legible)
- Color: Azul claro sólido (`Color(0xFFE3F2FD)`) - Fondo visible con buen contraste
- Elevación: 4dp para destacar del fondo
- Iconos: Emojis grandes (🌤️ 🌡️ 💨) de 18sp
- Posición: Entre bienvenida y productos destacados

**Información mostrada:**
- Ubicación: "Viña del Mar" (texto azul oscuro bold)
- Temperatura actual en °C (16sp, azul oscuro)
- Velocidad del viento en km/h (14sp, azul oscuro)
- Label: "API Externa: Open-Meteo" (10sp, gris oscuro)

**Paleta de colores optimizada:**
- Fondo: `Color(0xFFE3F2FD)` - Azul claro sólido, no transparente
- Textos principales: `Color(0xFF0D47A1)` - Azul oscuro para máximo contraste
- Texto secundario: `Color(0xFF424242)` - Gris oscuro legible

**Estados:**
- Loading: CircularProgressIndicator pequeño (20dp)
- Error: "No disponible" (gris oscuro)
- Success: Temperatura + Viento con datos en tiempo real

---

## 🆚 Diferencias con Microservicios Propios

| Aspecto | Microservicios Propios | API Externa (Open-Meteo) |
|---------|------------------------|--------------------------|
| **Control** | 100% controlado por nosotros | No lo controlamos |
| **Endpoints** | Podemos agregar/modificar | Fijos, predefinidos |
| **Modelos** | Diseñamos estructura | Debemos adaptarnos |
| **Despliegue** | AWS EC2 (nuestro servidor) | Servicio de terceros |
| **Base de datos** | MongoDB Atlas (nuestra) | No tenemos acceso |
| **URL** | http://34.193.190.24:8081 | https://api.open-meteo.com |
| **Autenticación** | Podemos implementar | Pública (sin auth) |
| **Propósito** | Lógica de negocio (usuarios, productos) | Datos públicos (clima) |
| **Latencia** | ~50ms (misma región AWS) | Variable (servicio externo) |
| **Disponibilidad** | Lo gestionamos nosotros | Depende del proveedor |

---

## ✅ Validación de Funcionamiento

### Prueba Manual

```bash
# Test directo a la API
curl "https://api.open-meteo.com/v1/forecast?latitude=-33.0472&longitude=-71.6127&current=temperature_2m,wind_speed_10m"
```

**Resultado esperado:** JSON con temperatura y velocidad del viento

### Compilación Exitosa

```bash
./gradlew assembleDebug
```

**Resultado:** ✅ BUILD SUCCESSFUL in 4s

### Verificación en App

1. Abrir HomeScreen
2. Observar widget del clima arriba de productos
3. Ver temperatura y viento actualizados
4. Verificar label "API Externa: Open-Meteo"

---

## 📊 Justificación de la Integración

### ¿Por qué Open-Meteo?

1. **Gratuita:** No requiere tarjeta de crédito ni API key
2. **Simple:** Endpoint claro y respuesta directa
3. **Confiable:** Servicio establecido y documentado
4. **Sin límites:** No tiene rate limiting estricto
5. **HTTPS:** Conexión segura por defecto

### ¿Por qué clima en HomeScreen?

1. **Relevante:** Huerto Hogar vende productos del campo, el clima es contextual
2. **No invasivo:** Widget compacto que no distrae
3. **Valor agregado:** Información útil para el usuario
4. **Independiente:** No afecta funcionalidad core de la app

### Cumplimiento de Requisitos

✅ **API externa:** Open-Meteo es un servicio de terceros  
✅ **Consumida vía Retrofit:** WeatherApiClient con Retrofit 2.9.0  
✅ **Mostrada en interfaz:** WeatherWidget visible en HomeScreen  
✅ **No interfiere:** Datos independientes de usuarios/productos  
✅ **Flujo visual:** Integrado naturalmente en diseño existente  

---

## 🎤 Explicación para Defensa

### Para decir durante presentación:

> "Además de nuestros dos microservicios propios, consumimos la API pública Open-Meteo para mostrar clima en tiempo real.
>
> La diferencia clave es el control: nuestros microservicios los diseñamos y modificamos libremente, pero con Open-Meteo debemos adaptarnos a su estructura fija.
>
> Integramos el clima en el HomeScreen mediante un widget compacto que muestra temperatura y viento de Viña del Mar. No interfiere con los datos de productos ni usuarios, es información complementaria.
>
> Esto demuestra que sabemos consumir tanto APIs propias como de terceros, adaptándonos a diferentes estructuras de datos."

### Preguntas frecuentes esperadas:

**P: ¿Por qué no usaron una API más compleja?**  
R: "Priorizamos funcionalidad sobre complejidad. Open-Meteo cumple el requisito de API externa y aporta valor real sin complicar el código."

**P: ¿Qué pasa si la API falla?**  
R: "El ViewModel maneja errores con try-catch. Si falla, muestra 'No disponible' sin afectar el resto de la app."

**P: ¿Cómo se diferencia de sus microservicios?**  
R: "Control. Podemos agregar endpoints a nuestros microservicios, pero no a Open-Meteo. Diseñamos nuestros modelos, pero debemos adaptarnos a los de Open-Meteo."

---

## 🚀 Próximos Pasos (Post-Entrega)

### Posibles Mejoras Futuras

1. **Caché local:** Guardar último clima conocido para offline
2. **Múltiples ubicaciones:** Permitir al usuario elegir ciudad
3. **Pronóstico extendido:** Mostrar clima de próximos días
4. **Iconos dinámicos:** Cambiar icono según condiciones (sol, lluvia, nublado)
5. **Notificaciones:** Alertar si llueve (importante para productos del campo)

---

## 📝 Conclusión

La integración de Open-Meteo Weather API cumple exitosamente con el requisito IE 3.1.4 de la rúbrica:

- ✅ **Consume API externa:** Open-Meteo es un servicio de terceros
- ✅ **Integrada al flujo visual:** Widget en HomeScreen
- ✅ **Sin interferir con microservicios:** Datos independientes
- ✅ **Mediante Retrofit:** Cliente Retrofit dedicado
- ✅ **Mostrada en interfaz:** UI compacta y clara

**Impacto en evaluación:** +15% por cumplir IE 3.1.4

---

**Desarrollado por:** ctapiad y bencastroo  
**Asignatura:** Desarrollo de Aplicaciones Móviles  
**Institución:** Duoc UC  
**Fecha:** 24 de noviembre de 2025
