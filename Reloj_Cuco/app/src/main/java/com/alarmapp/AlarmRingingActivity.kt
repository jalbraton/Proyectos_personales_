package com.alarmapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AlarmRingingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Mostrar sobre la pantalla de bloqueo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }
        
        setContentView(R.layout.activity_alarm_ringing)

        val btnStop = findViewById<Button>(R.id.btnStopAlarm)
        val btnSnooze = findViewById<Button>(R.id.btnSnooze)

        btnStop.setOnClickListener {
            stopAlarmService()
            finish()
        }

        btnSnooze.setOnClickListener {
            stopAlarmService()
            // TODO: Implementar snooze (posponer 5 minutos)
            finish()
        }
    }

    private fun stopAlarmService() {
        // Detener el servicio de alarma
        val serviceIntent = Intent(this, AlarmService::class.java).apply {
            action = "STOP_ALARM"
        }
        startService(serviceIntent)
    }

    override fun onBackPressed() {
        // No permitir cerrar con botón atrás sin detener la alarma
    }
}
