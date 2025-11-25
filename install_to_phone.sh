#!/bin/bash
export PATH="$PATH:/Users/administrador/Library/Android/sdk/platform-tools"
APK_PATH="/Users/administrador/AndroidStudioProjects/HuertoHogar/app/build/outputs/apk/debug/app-debug.apk"

echo "📱 Instalando Huerto Hogar en dispositivo Android..."
echo "🔍 Verificando dispositivos conectados..."

if adb devices | grep -q "device$"; then
    echo "✅ Dispositivo encontrado"
    echo "📲 Instalando APK..."
    
    if adb install -r "$APK_PATH"; then
        echo ""
        echo "🎉 ¡INSTALACIÓN EXITOSA!"
        echo "✅ La aplicación Huerto Hogar se ha instalado en tu Samsung S21"
        echo "🚀 Puedes encontrarla en el menú de aplicaciones"
    else
        echo "❌ Error en la instalación"
    fi
else
    echo "❌ No se encontró dispositivo conectado"
    echo "💡 Instrucciones:"
    echo "   1. Conecta tu Samsung S21 con cable USB"
    echo "   2. Habilita Depuración USB en Opciones de desarrollador"
    echo "   3. Acepta el diálogo de confianza en el teléfono"
    echo "   4. Ejecuta este script nuevamente"
fi
