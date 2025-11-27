package com.alarmapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getIntExtra("ALARM_ID", -1)
        val mode = intent.getStringExtra("ALARM_MODE") ?: "SINGLE_FILE"
        val audioUri = intent.getStringExtra("AUDIO_URI")
        val folderUri = intent.getStringExtra("FOLDER_URI")

        // Iniciar servicio para reproducir alarma
        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("ALARM_ID", alarmId)
            putExtra("ALARM_MODE", mode)
            putExtra("AUDIO_URI", audioUri)
            putExtra("FOLDER_URI", folderUri)
        }
        context.startForegroundService(serviceIntent)

        // Mostrar pantalla de alarma
        val alarmIntent = Intent(context, AlarmRingingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("ALARM_ID", alarmId)
        }
        context.startActivity(alarmIntent)
    }
}
