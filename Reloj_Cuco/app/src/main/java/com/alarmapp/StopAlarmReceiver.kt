package com.alarmapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class StopAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Detener el servicio de alarma
        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            action = "STOP_ALARM"
        }
        context.startService(serviceIntent)
    }
}
