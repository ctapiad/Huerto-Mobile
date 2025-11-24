# 🔐 Información del Keystore - Huerto Hogar

## Archivo de Firma

**Ubicación**: `huerto-hogar-key.jks` (raíz del proyecto)

## Credenciales

⚠️ **IMPORTANTE**: Estas credenciales son solo para desarrollo/demostración. En producción, usar credenciales seguras y no commitearlas al repositorio.

- **Store Password**: `huerto2024`
- **Key Alias**: `huerto-hogar`
- **Key Password**: `huerto2024`

## Detalles del Certificado

- **Algoritmo**: RSA
- **Tamaño de clave**: 2048 bits
- **Validez**: 10,000 días
- **Organización**: Duoc UC
- **Unidad Organizacional**: bencastroo y ctapiad
- **Nombre Común**: Huerto Hogar
- **Ciudad**: Viña del Mar
- **Estado**: Valparaiso
- **País**: CL

## Compilar APK Firmado

### APK de Release (firmado)
```bash
./gradlew assembleRelease
```
El APK firmado se generará en:
```
app/build/outputs/apk/release/app-release.apk
```

### APK de Debug (firmado)
```bash
./gradlew assembleDebug
```
El APK firmado se generará en:
```
app/build/outputs/apk/debug/app-debug.apk
```

## Verificar Firma

Para verificar que el APK está correctamente firmado:

```bash
# Windows
& "$env:LOCALAPPDATA\Android\Sdk\build-tools\36.0.0\apksigner.bat" verify --print-certs app\build\outputs\apk\release\app-release.apk

# Linux/Mac
$ANDROID_HOME/build-tools/36.0.0/apksigner verify --print-certs app/build/outputs/apk/release/app-release.apk
```

### Certificado de Firma Actual
```
Signer #1 certificate DN: CN=Huerto Hogar, OU=bencastroo y ctapiad, O=Duoc UC, L=Viña del Mar, ST=Valparaiso, C=CL
SHA-256: 5E:63:87:52:09:F3:77:40:DC:18:12:AD:91:BC:99:3F:3C:12:A6:76:7A:53:A9:2E:C7:31:B1:62:B2:CA:DD:84
SHA-1: 8A:37:5E:E5:A2:A4:47:77:79:1B:F4:5C:1A:44:BB:27:7C:08:9D:34
MD5: 5B:0D:29:A1:9C:AB:04:C2:69:49:CC:6F:FA:93:E7:08
Válido hasta: 11 de abril de 2053
```

## App Bundle (para Google Play Store)

```bash
./gradlew bundleRelease
```
El bundle firmado se generará en:
```
app/build/outputs/bundle/release/app-release.aab
```

## Notas de Seguridad

1. ✅ El archivo `huerto-hogar-key.jks` debe estar en el `.gitignore`
2. ✅ Las contraseñas deben estar en `local.properties` o variables de entorno en producción
3. ✅ Nunca compartir el keystore públicamente
4. ✅ Hacer backup del keystore en lugar seguro
