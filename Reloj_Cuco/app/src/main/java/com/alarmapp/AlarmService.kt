package com.alarmapp

import android.app.*
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import java.io.File

class AlarmService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Manejar acción de detener alarma
        if (intent?.action == "STOP_ALARM") {
            stopAlarm()
            return START_NOT_STICKY
        }

        val alarmId = intent?.getIntExtra("ALARM_ID", -1) ?: -1
        val mode = intent?.getStringExtra("ALARM_MODE") ?: "SINGLE_FILE"
        val audioUri = intent?.getStringExtra("AUDIO_URI")
        val folderUri = intent?.getStringExtra("FOLDER_URI")

        // Adquirir WakeLock
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "AlarmApp::AlarmWakeLock"
        )
        wakeLock?.acquire(10 * 60 * 1000L) // 10 minutos máximo

        // Crear notificación de foreground
        startForeground(NOTIFICATION_ID, createNotification())

        // Reproducir alarma
        playAlarm(mode, audioUri, folderUri)

        return START_NOT_STICKY
    }

    private fun playAlarm(mode: String, audioUri: String?, folderUri: String?) {
        try {
            val uri = when (mode) {
                "SINGLE_FILE" -> {
                    audioUri?.let { Uri.parse(it) }
                }
                "RANDOM_FOLDER" -> {
                    folderUri?.let { getRandomAudioFromFolder(Uri.parse(it)) }
                }
                else -> null
            }

            if (uri != null) {
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .build()
                    )
                    setDataSource(applicationContext, uri)
                    isLooping = true
                    prepare()
                    start()
                }
            } else {
                // Usar sonido predeterminado del sistema
                playDefaultAlarm()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            playDefaultAlarm()
        }
    }

    private fun playDefaultAlarm() {
        try {
            val alarmUri = android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                )
                setDataSource(applicationContext, alarmUri)
                isLooping = true
                prepare()
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getRandomAudioFromFolder(folderUri: Uri): Uri? {
        val audioFiles = mutableListOf<Uri>()
        val childrenUri = androidx.documentfile.provider.DocumentFile.fromTreeUri(this, folderUri)

        childrenUri?.listFiles()?.forEach { file ->
            if (file.isFile && isAudioFile(file.name ?: "")) {
                audioFiles.add(file.uri)
            }
        }

        return if (audioFiles.isNotEmpty()) {
            audioFiles.random()
        } else {
            null
        }
    }

    private fun isAudioFile(fileName: String): Boolean {
        val audioExtensions = listOf("mp3", "flac", "wav", "ogg", "m4a", "aac", "wma")
        val extension = fileName.substringAfterLast(".", "").lowercase()
        return extension in audioExtensions
    }

    private fun createNotification(): Notification {
        createNotificationChannel()

        // Intent para detener desde la notificación usando BroadcastReceiver
        val stopIntent = Intent(this, StopAlarmReceiver::class.java)
        val stopPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Intent para abrir la actividad de alarma
        val openIntent = Intent(this, AlarmRingingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openPendingIntent = PendingIntent.getActivity(
            this,
            1,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Alarma Sonando")
            .setContentText("Toca para detener")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .setAutoCancel(false)
            .setContentIntent(openPendingIntent)
            .addAction(
                android.R.drawable.ic_media_pause,
                "Detener",
                stopPendingIntent
            )
            .setFullScreenIntent(openPendingIntent, true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarmas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para alarmas"
                setSound(null, null)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun stopAlarm() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaPlayer = null
        }

        wakeLock?.let {
            if (it.isHeld) {
                it.release()
            }
        }
        wakeLock = null

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        stopAlarm()
        super.onDestroy()
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "alarm_channel"
    }
}
