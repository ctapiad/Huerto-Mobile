#!/bin/bash
export PATH="$PATH:/Users/administrador/Library/Android/sdk/platform-tools"
echo "🔍 Buscando dispositivos Android conectados..."
adb devices -l
echo ""
if adb devices | grep -q "device$"; then
    echo "✅ Dispositivos encontrados"
else
    echo "❌ No se encontraron dispositivos"
    echo "💡 Asegúrate de:"
    echo "   1. Haber habilitado Depuración USB"
    echo "   2. Conectar el cable USB"
    echo "   3. Aceptar el diálogo de confianza"
fi
