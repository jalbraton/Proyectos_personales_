# 🚀 Quick Start Guide - SecureVault

## Primeros Pasos para Usar la Aplicación

### 1. Abrir el Proyecto

```bash
# Navega al directorio
cd "C:\Users\JoseA\OneDrive\Documentos\Workspace_1\App_encrypt"

# Abre en Android Studio
start android-studio .
```

O simplemente abre Android Studio y selecciona "Open" → navega a la carpeta `App_encrypt`.

### 2. Sincronizar Gradle

Una vez abierto el proyecto:
1. Android Studio detectará automáticamente el proyecto Gradle
2. Clic en "Sync Now" cuando aparezca la notificación
3. Espera a que se descarguen las dependencias (puede tardar 2-5 minutos la primera vez)

### 3. Configurar Emulador o Dispositivo

**Opción A: Emulador**
1. Tools → Device Manager
2. Create Device → Selecciona un dispositivo (ej: Pixel 6)
3. Selecciona una imagen del sistema (recomendado: Android 13 - API 33)
4. Finish

**Opción B: Dispositivo Físico**
1. Habilita "Opciones de desarrollador" en tu Android
2. Habilita "Depuración USB"
3. Conecta el dispositivo por USB
4. Acepta la autorización en el dispositivo

### 4. Ejecutar la App

1. Selecciona el dispositivo/emulador en la barra superior
2. Clic en el botón verde "Run" (▶️) o presiona Shift+F10
3. La app se instalará y ejecutará automáticamente

## ⚠️ Estado Actual

### ✅ Lo que funciona:
- Sistema completo de criptografía AES-256-XTS
- Creación y apertura de volúmenes encriptados
- Lectura/escritura de datos cifrados
- Gestión de archivos dentro del volumen
- Sistema de sesiones con timeout
- Limpieza segura de memoria

### 🚧 Lo que falta:
- **Interfaz de usuario** (layouts y activities)
- File picker para seleccionar archivos
- RecyclerView para mostrar lista de archivos
- Diálogos de progreso
- Configuración de preferencias

## 📝 Próximos Pasos para Ti

### Paso 1: Crear el Main Activity UI (30 min)

Crea `app/src/main/res/layout/activity_main.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="24dp"
    android:background="#F5F5F5">

    <ImageView
        android:id="@+id/ivLogo"
        android:layout_width="100dp"
        android:layout_height="100dp"
        android:src="@android:drawable/ic_lock_lock"
        android:tint="#2196F3"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        android:layout_marginTop="60dp"/>

    <TextView
        android:id="@+id/tvTitle"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="SecureVault"
        android:textSize="36sp"
        android:textStyle="bold"
        android:textColor="#212121"
        app:layout_constraintTop_toBottomOf="@id/ivLogo"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        android:layout_marginTop="24dp"/>

    <TextView
        android:id="@+id/tvSubtitle"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Encriptación de Nivel Militar"
        android:textSize="14sp"
        android:textColor="#757575"
        app:layout_constraintTop_toBottomOf="@id/tvTitle"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        android:layout_marginTop="8dp"/>

    <com.google.android.material.card.MaterialCardView
        android:id="@+id/cardButtons"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        app:cardElevation="4dp"
        app:cardCornerRadius="16dp"
        app:layout_constraintTop_toBottomOf="@id/tvSubtitle"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        android:layout_marginTop="48dp">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:padding="16dp">

            <com.google.android.material.button.MaterialButton
                android:id="@+id/btnCreateVolume"
                android:layout_width="match_parent"
                android:layout_height="64dp"
                android:text="Crear Nuevo Volumen"
                android:textSize="16sp"
                android:textAllCaps="false"
                app:icon="@android:drawable/ic_input_add"
                app:iconGravity="textStart"
                app:iconSize="24dp"
                style="@style/Widget.Material3.Button.ElevatedButton"/>

            <com.google.android.material.button.MaterialButton
                android:id="@+id/btnOpenVolume"
                android:layout_width="match_parent"
                android:layout_height="64dp"
                android:text="Abrir Volumen Existente"
                android:textSize="16sp"
                android:textAllCaps="false"
                app:icon="@android:drawable/ic_menu_view"
                app:iconGravity="textStart"
                app:iconSize="24dp"
                style="@style/Widget.Material3.Button.OutlinedButton"
                android:layout_marginTop="12dp"/>

        </LinearLayout>

    </com.google.android.material.card.MaterialCardView>

    <TextView
        android:id="@+id/tvInfo"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:text="• AES-256-XTS\n• PBKDF2 100k iteraciones\n• Limpieza segura de memoria"
        android:textSize="12sp"
        android:textColor="#9E9E9E"
        android:gravity="center"
        android:lineSpacingExtra="4dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        android:layout_marginBottom="32dp"/>

</androidx.constraintlayout.widget.ConstraintLayout>
```

### Paso 2: Implementar MainActivity (20 min)

Crea `app/src/main/java/com/securevault/ui/MainActivity.kt`:

```kotlin
package com.securevault.ui

import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.securevault.R
import com.securevault.security.SessionManager

class MainActivity : AppCompatActivity() {
    
    private lateinit var sessionManager: SessionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        sessionManager = SessionManager.getInstance()
        
        // Protección de pantalla
        if (sessionManager.shouldProtectScreen()) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        }
        
        setupButtons()
    }
    
    private fun setupButtons() {
        findViewById<MaterialButton>(R.id.btnCreateVolume).setOnClickListener {
            Toast.makeText(this, "Crear Volumen - Por implementar", Toast.LENGTH_SHORT).show()
            // TODO: Navegar a CreateVolumeActivity
        }
        
        findViewById<MaterialButton>(R.id.btnOpenVolume).setOnClickListener {
            Toast.makeText(this, "Abrir Volumen - Por implementar", Toast.LENGTH_SHORT).show()
            // TODO: Navegar a OpenVolumeActivity
        }
    }
    
    override fun onResume() {
        super.onResume()
        sessionManager.updateActivity()
    }
}
```

### Paso 3: Crear strings.xml

Crea `app/src/main/res/values/strings.xml`:

```xml
<resources>
    <string name="app_name">SecureVault</string>
</resources>
```

### Paso 4: Crear themes.xml

Crea `app/src/main/res/values/themes.xml`:

```xml
<resources>
    <style name="Theme.SecureVault" parent="Theme.Material3.DayNight.NoActionBar">
        <item name="colorPrimary">#2196F3</item>
        <item name="colorPrimaryDark">#1976D2</item>
        <item name="colorAccent">#03DAC5</item>
        <item name="android:statusBarColor">@color/black</item>
    </style>
</resources>
```

### Paso 5: Probar

1. Sync Gradle si aún no lo has hecho
2. Run (▶️)
3. Deberías ver la pantalla principal con dos botones

## 🧪 Probar la Funcionalidad Core

Puedes crear un test simple para verificar que el cifrado funciona:

```kotlin
// En app/src/test/java/com/securevault/crypto/AESCipherTest.kt
package com.securevault.crypto

import org.junit.Test
import org.junit.Assert.*

class AESCipherTest {
    @Test
    fun testBasicEncryptionDecryption() {
        val plaintext = "Hello SecureVault!".toByteArray()
        val key1 = ByteArray(32) { it.toByte() }
        val key2 = ByteArray(32) { (it + 1).toByte() }
        
        val encrypted = AESCipher.encrypt(plaintext, key1, key2, 0)
        assertFalse(encrypted.contentEquals(plaintext))
        
        val decrypted = AESCipher.decrypt(encrypted, key1, key2, 0)
        assertArrayEquals(plaintext, decrypted)
    }
}
```

Ejecuta con: `./gradlew test`

## 📱 Estructura de Archivos Creados

```
App_encrypt/
├── README.md                          ✅ Documentación principal
├── IMPLEMENTATION_GUIDE.md            ✅ Guía de implementación
├── QUICK_START.md                     ✅ Esta guía
├── build.gradle.kts                   ✅ Configuración de build
├── settings.gradle.kts                ✅ Configuración de proyecto
├── gradle.properties                  ✅ Propiedades de Gradle
└── app/
    ├── build.gradle.kts               ✅ Build de la app
    ├── proguard-rules.pro             ✅ Reglas de ofuscación
    └── src/
        └── main/
            ├── AndroidManifest.xml    ✅ Manifest
            ├── java/com/securevault/
            │   ├── SecureVaultApp.kt  ✅ Clase de aplicación
            │   ├── crypto/
            │   │   ├── AESCipher.kt           ✅ Cifrado AES
            │   │   ├── KeyDerivation.kt       ✅ PBKDF2
            │   │   ├── SecureRandomGenerator.kt ✅ RNG
            │   │   └── CryptoUtils.kt         ✅ Utilidades
            │   ├── volume/
            │   │   ├── VolumeHeader.kt        ✅ Header
            │   │   ├── EncryptedVolume.kt     ✅ Volumen
            │   │   └── VolumeManager.kt       ✅ Gestor
            │   ├── storage/
            │   │   └── VolumeFileSystem.kt    ✅ Sistema de archivos
            │   ├── security/
            │   │   ├── SessionManager.kt      ✅ Sesiones
            │   │   └── SecureMemory.kt        ✅ Memoria segura
            │   ├── utils/
            │   │   ├── Constants.kt           ✅ Constantes
            │   │   ├── Logger.kt              ✅ Logging
            │   │   └── Extensions.kt          ✅ Extensiones
            │   └── ui/
            │       └── MainActivity.kt        🚧 Por completar
            └── res/
                ├── layout/
                │   └── activity_main.xml      🚧 Por crear
                ├── values/
                │   ├── strings.xml            🚧 Por crear
                │   └── themes.xml             🚧 Por crear
                └── drawable/                  🚧 Por crear
```

## 🎯 Objetivo Final

Tu aplicación permitirá:

1. ✅ Crear volúmenes encriptados de cualquier tamaño
2. ✅ Cifrar/descifrar archivos con AES-256-XTS
3. ✅ Proteger con contraseña maestra
4. 🚧 UI simple y intuitiva
5. 🚧 Agregar/extraer archivos fácilmente
6. ✅ Seguridad de nivel militar

## 💡 Tips

- **Usa el Logger**: Todas las operaciones importantes están logged
- **Revisa Constants.kt**: Ajusta parámetros como tamaño de buffer, iteraciones, etc.
- **Seguridad primero**: Las claves se limpian automáticamente de la memoria
- **Testing**: Siempre prueba con archivos pequeños primero

## 🆘 Necesitas Ayuda?

Si tienes problemas:

1. Revisa los logs en Logcat (filtro: "SecureVault")
2. Verifica que Gradle esté sincronizado
3. Asegúrate de tener Android SDK instalado
4. Revisa IMPLEMENTATION_GUIDE.md para más detalles

¡Éxito con tu aplicación de encriptación! 🔐
