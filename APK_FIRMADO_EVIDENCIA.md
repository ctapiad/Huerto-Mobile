# 📦 Evidencia de APK Firmado - Huerto Hogar

## ✅ Generación Exitosa del APK Firmado

**Fecha de generación:** 24 de noviembre de 2025  
**Versión:** 1.0 (Release)  
**Tamaño:** 50 MB

---

## 🔐 Información de Firma Digital

### Certificado Utilizado
- **Archivo Keystore:** `huerto-hogar-key.jks`
- **Ubicación:** Raíz del proyecto
- **Key Alias:** `huerto-hogar`
- **Algoritmo:** RSA 2048 bits
- **Validez:** 10,000 días (hasta 2053)

### Datos del Certificado
```
CN: Huerto Hogar
OU: bencastroo y ctapiad
O: Duoc UC
L: Viña del Mar
ST: Valparaiso
C: CL
```

### Huellas Digitales (Fingerprints)
```
SHA-256: 5E:63:87:52:09:F3:77:40:DC:18:12:AD:91:BC:99:3F:3C:12:A6:76:7A:53:A9:2E:C7:31:B1:62:B2:CA:DD:84
SHA-1: 8A:37:5E:E5:A2:A4:47:77:79:1B:F4:5C:1A:44:BB:27:7C:08:9D:34
MD5: 5B:0D:29:A1:9C:AB:04:C2:69:49:CC:6F:FA:93:E7:08
```

---

## 📋 Proceso de Compilación

### Comando Utilizado
```bash
./gradlew assembleRelease
```

### Resultado de la Compilación
```
BUILD SUCCESSFUL in 25s
50 actionable tasks: 49 executed, 1 up-to-date
```

### Ubicación del APK Generado
```
app/build/outputs/apk/release/app-release.apk
```

---

## 🔧 Configuración en build.gradle.kts

La firma está configurada en el archivo `app/build.gradle.kts`:

```kotlin
signingConfigs {
    create("release") {
        storeFile = file("../huerto-hogar-key.jks")
        storePassword = "huerto2024"
        keyAlias = "huerto-hogar"
        keyPassword = "huerto2024"
    }
}

buildTypes {
    release {
        isMinifyEnabled = false
        signingConfig = signingConfigs.getByName("release")
    }
    debug {
        signingConfig = signingConfigs.getByName("release")
    }
}
```

---

## ✅ Cumplimiento de Requisitos (IE 3.3.1)

Según la rúbrica de evaluación:

> **IE 3.3.1**: Genera el archivo APK firmado, incluyendo su configuración técnica (build.gradle, .jks).

### Evidencias Entregadas:

- ✅ **APK Firmado**: `app-release.apk` (50 MB)
- ✅ **Archivo Keystore**: `huerto-hogar-key.jks` (2.8 KB)
- ✅ **Configuración**: `build.gradle.kts` con signingConfigs completos
- ✅ **Documentación**: `KEYSTORE_INFO.md` con credenciales y proceso
- ✅ **Metadata**: `output-metadata.json` generado automáticamente

---

## 📸 Capturas de Evidencia

### 1. Estructura de archivos del proyecto
```
HuertoHogar/
├── huerto-hogar-key.jks          ← Certificado de firma
├── KEYSTORE_INFO.md              ← Documentación del keystore
├── app/
│   ├── build.gradle.kts          ← Configuración de firma
│   └── build/outputs/apk/release/
│       └── app-release.apk       ← APK firmado (50 MB)
```

### 2. Archivos entregables
- **Ubicación original:** `app/build/outputs/apk/release/app-release.apk`
- **Copia para entrega:** `HuertoHogar-FIRMADO-Release.apk` (escritorio)
- **Keystore:** `huerto-hogar-key.jks` (escritorio)

### 3. Verificación de firma
El APK ha sido correctamente firmado con el certificado digital del equipo, cumpliendo con los estándares de seguridad de Android para distribución.

---

## 🎯 Propósito de la Firma

La firma digital del APK garantiza:

1. **Autenticidad**: Verifica que la app fue creada por los desarrolladores declarados
2. **Integridad**: Asegura que el APK no ha sido modificado después de la compilación
3. **Actualizaciones**: Permite publicar actualizaciones de la misma app
4. **Publicación**: Requisito obligatorio para Google Play Store y distribución oficial

---

## 📦 Archivos para Entrega en AVA

### Checklist de Entrega:

- [x] Código fuente en GitHub (con commits de ambos integrantes)
- [x] APK firmado (`app-release.apk`)
- [x] Archivo keystore (`huerto-hogar-key.jks`)
- [x] Configuración de firma en `build.gradle.kts`
- [x] Documentación del proceso (`KEYSTORE_INFO.md`)
- [x] Evidencia visual (este archivo)

---

**Estado:** ✅ **APK firmado correctamente y listo para entrega**

**Integrantes:**
- bencastroo
- ctapiad

**Fecha de entrega:** 24 de noviembre de 2025
