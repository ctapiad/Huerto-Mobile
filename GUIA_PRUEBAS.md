# 🧪 Guía de Pruebas de APIs en Android Studio

## ✅ Configuración Completada

He configurado **3 formas diferentes** de probar la conexión con tus microservicios:

---

## 1️⃣ **Test Automático al Iniciar la App**

### ¿Cómo funciona?
Cada vez que inicias la app, se ejecuta automáticamente un test de conexión.

### ¿Dónde ver los resultados?
En el **Logcat de Android Studio**:

1. Abre Android Studio
2. Ve a la pestaña **Logcat** (abajo)
3. Filtra por: `NetworkTest`
4. Verás algo como:

```
==================================================
TEST DE CONEXIÓN CON MICROSERVICIOS
==================================================
✅ Products API: OK - API is running
✅ Products List: 15 productos disponibles
✅ Users API: 8 usuarios encontrados
==================================================
```

### También verás un Toast:
- ✅ **"APIs conectadas correctamente"** (si funciona)
- ❌ **"Error de conexión"** (si falla)

---

## 2️⃣ **Pantalla de Pruebas Interactiva** 🆕

### ¿Cómo acceder?
1. Ejecuta la app en el emulador
2. En la **pantalla de inicio (Home)**, verás un botón azul:
   
   ```
   🧪 Prueba de APIs
   Verifica la conexión con microservicios
   ```

3. Toca el botón

### ¿Qué puedes hacer?
La pantalla de pruebas tiene 3 secciones:

#### A. **Test de Conexión General**
- Botón: **"Probar Conexión"**
- Prueba ambos microservicios
- Muestra resultados en tiempo real con colores:
  - ✅ Verde = Éxito
  - ❌ Rojo = Error

#### B. **Test de Login**
- Campos: Email y Password
- Prueba login con usuarios reales
- Ejemplo de uso:
  ```
  Email: usuario@ejemplo.com
  Password: 123456
  ```

#### C. **Test de Búsqueda de Productos**
- Campo: Nombre del producto
- Ejemplo: "tomate"
- Muestra cuántos productos encontró

---

## 3️⃣ **Verificación Manual en Logcat**

### Pasos:
1. Abre **Logcat** en Android Studio
2. Ejecuta la app
3. Busca estos tags:
   - `NetworkTest` - Tests automáticos
   - `API` - Llamadas de red
   - `Retrofit` - Detalles HTTP

### Filtros útiles:
```
tag:NetworkTest
tag:API
package:mine level:debug
```

---

## 📱 **Ejecutar en el Emulador**

### Configuración actual:
✅ URLs configuradas para emulador:
- Usuarios: `http://10.0.2.2:8081`
- Productos: `http://10.0.2.2:8082`

### Pasos:
1. **Asegúrate que tus microservicios estén corriendo:**
   ```bash
   # En terminales separadas
   cd /ruta/microservicio-usuarios
   ./mvnw spring-boot:run
   
   cd /ruta/microservicio-productos
   ./mvnw spring-boot:run
   ```

2. **Ejecuta la app en Android Studio:**
   - Click en el botón ▶️ Run
   - Selecciona un emulador
   - Espera a que instale

3. **Verifica la conexión:**
   - Mira el Toast que aparece al iniciar
   - Revisa Logcat
   - O usa la pantalla de pruebas

---

## 📱 **Ejecutar en Dispositivo Físico** (Samsung S21)

### ⚠️ IMPORTANTE: Cambiar configuración

1. **Obtén tu IP de Mac:**
   ```bash
   ifconfig | grep "inet " | grep -v 127.0.0.1
   ```
   Ejemplo de resultado: `192.168.1.100`

2. **Actualiza `ApiConfig.kt`:**
   ```kotlin
   // Cambia de:
   const val USER_SERVICE_BASE_URL = "http://10.0.2.2:8081/"
   
   // A tu IP real:
   const val USER_SERVICE_BASE_URL = "http://192.168.1.100:8081/"
   ```

3. **Asegúrate:**
   - Mac y celular en la misma red WiFi
   - Microservicios corriendo en tu Mac
   - Firewall de Mac permite conexiones

4. **Genera APK:**
   ```bash
   ./gradlew assembleDebug
   ```

5. **Instala en tu Samsung S21**

---

## 🔍 **Resultados Esperados**

### ✅ **Si todo funciona:**
```
✅ Products API: OK - API is running
✅ Products List: 15 productos disponibles
✅ Users API: 8 usuarios encontrados
```

### ❌ **Si hay problemas:**

#### Error: "Error de conexión"
**Causa:** Microservicios no están corriendo
**Solución:** Inicia los servicios Spring Boot

#### Error: "Connection refused"
**Causa:** IP incorrecta o firewall
**Solución:** 
- Verifica IP en `ApiConfig.kt`
- Desactiva firewall temporalmente
- Verifica que ambos estén en la misma WiFi

#### Error: "Timeout"
**Causa:** Red lenta o servicio no responde
**Solución:**
- Verifica que los servicios estén corriendo
- Aumenta timeout en `ApiConfig.kt`

---

## 🎯 **Ejemplo Completo de Uso**

### Escenario: Primera ejecución

1. **Inicia microservicios:**
   ```bash
   # Terminal 1
   cd microservicio-usuarios
   ./mvnw spring-boot:run
   
   # Terminal 2  
   cd microservicio-productos
   ./mvnw spring-boot:run
   ```

2. **Ejecuta app en Android Studio**
   - Click ▶️ Run
   - Espera instalación

3. **Observa resultados:**
   - **Toast:** "✅ APIs conectadas correctamente"
   - **Logcat:** Ver detalles completos
   
4. **Prueba interactiva:**
   - Toca botón "🧪 Prueba de APIs"
   - Prueba login, búsqueda, etc.

---

## 📊 **Logs de Ejemplo**

### Logs exitosos:
```
D/NetworkTest: Testing Products API...
D/NetworkTest: Products API OK: API is running
D/NetworkTest: Testing Products List...
D/NetworkTest: Products List OK: 15 items
D/NetworkTest: Testing Users API...
D/NetworkTest: Users API OK: 8 users
```

### Logs con error:
```
E/NetworkTest: Products API Error: Connection refused
E/NetworkTest: Users API Error: java.net.ConnectException
```

---

## 🛠️ **Troubleshooting**

### Problema: No veo logs
**Solución:** Asegúrate de filtrar por `NetworkTest` en Logcat

### Problema: App no compila
**Solución:** Sync Gradle y limpia:
```bash
./gradlew clean
./gradlew build
```

### Problema: Error de imports
**Solución:** Android Studio → File → Invalidate Caches → Restart

---

## 📝 **Notas Adicionales**

- El test automático solo se ejecuta al **iniciar** la app
- La pantalla de pruebas está en el **Home** (scroll hacia abajo)
- Puedes ejecutar tests cuantas veces quieras
- Los tests **NO modifican datos**, solo leen

---

## ✨ **Archivos Creados**

1. `NetworkTestHelper.kt` - Helper de pruebas
2. `ApiTestScreen.kt` - Pantalla interactiva de pruebas
3. Modificaciones en `MainActivity.kt` - Test automático
4. Modificaciones en `HomeScreen.kt` - Botón de acceso

¡Todo listo para probar! 🚀
