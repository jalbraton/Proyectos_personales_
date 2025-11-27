package com.alarmapp

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddAlarm: FloatingActionButton
    private lateinit var alarmAdapter: AlarmAdapter
    private lateinit var alarmManager: AlarmManager
    private val alarms = mutableListOf<Alarm>()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(this, "Se necesita permiso de notificaciones", Toast.LENGTH_SHORT).show()
        }
    }

    private val audioPickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            pendingAlarmUri = it
            Toast.makeText(this, "Archivo seleccionado", Toast.LENGTH_SHORT).show()
        }
    }

    private val folderPickerLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        uri?.let {
            contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            pendingFolderUri = it
            Toast.makeText(this, "Carpeta seleccionada", Toast.LENGTH_SHORT).show()
        }
    }

    private var pendingAlarmUri: Uri? = null
    private var pendingFolderUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        recyclerView = findViewById(R.id.recyclerView)
        fabAddAlarm = findViewById(R.id.fabAddAlarm)

        alarmAdapter = AlarmAdapter(alarms, ::onToggleAlarm, ::onDeleteAlarm)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = alarmAdapter

        fabAddAlarm.setOnClickListener {
            showAddAlarmDialog()
        }

        // Double tap functionality on background
        setupDoubleTapToAddAlarm()
        
        // GitHub link functionality
        setupGitHubLink()

        requestNotificationPermission()
        checkExactAlarmPermission()
        loadAlarms()
    }
    
    private fun setupDoubleTapToAddAlarm() {
        var lastTapTime = 0L
        val doubleTapDelay = 300L // milliseconds
        
        recyclerView.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastTapTime < doubleTapDelay) {
                // Double tap detected
                showAddAlarmDialog()
                lastTapTime = 0L
            } else {
                lastTapTime = currentTime
            }
        }
    }
    
    private fun setupGitHubLink() {
        val githubIcon = findViewById<ImageView>(R.id.githubIcon)
        githubIcon?.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/jalbraton/Reloj_cuco"))
            startActivity(intent)
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                AlertDialog.Builder(this)
                    .setTitle("Permiso requerido")
                    .setMessage("Para que las alarmas funcionen correctamente, necesitas permitir alarmas exactas")
                    .setPositiveButton("Ir a Ajustes") { _, _ ->
                        startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }
    }

    private fun showAddAlarmDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_alarm, null)
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroupMode)
        val btnSelectFile = dialogView.findViewById<Button>(R.id.btnSelectFile)
        val btnSelectFolder = dialogView.findViewById<Button>(R.id.btnSelectFolder)
        val tvSelectedFile = dialogView.findViewById<TextView>(R.id.tvSelectedFile)
        val tvSelectedFolder = dialogView.findViewById<TextView>(R.id.tvSelectedFolder)

        pendingAlarmUri = null
        pendingFolderUri = null

        btnSelectFile.setOnClickListener {
            audioPickerLauncher.launch("audio/*")
        }

        btnSelectFolder.setOnClickListener {
            folderPickerLauncher.launch(null)
        }

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioSingleFile -> {
                    btnSelectFile.isEnabled = true
                    btnSelectFolder.isEnabled = false
                }
                R.id.radioRandomFolder -> {
                    btnSelectFile.isEnabled = false
                    btnSelectFolder.isEnabled = true
                }
            }
        }

        AlertDialog.Builder(this)
            .setTitle("Nueva Alarma")
            .setView(dialogView)
            .setPositiveButton("Seleccionar Hora") { _, _ ->
                val selectedMode = when (radioGroup.checkedRadioButtonId) {
                    R.id.radioSingleFile -> AlarmMode.SINGLE_FILE
                    R.id.radioRandomFolder -> AlarmMode.RANDOM_FOLDER
                    else -> AlarmMode.SINGLE_FILE
                }

                if (selectedMode == AlarmMode.SINGLE_FILE && pendingAlarmUri == null) {
                    Toast.makeText(this, "Selecciona un archivo de audio", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (selectedMode == AlarmMode.RANDOM_FOLDER && pendingFolderUri == null) {
                    Toast.makeText(this, "Selecciona una carpeta", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                showTimePicker(selectedMode)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showTimePicker(mode: AlarmMode) {
        val calendar = Calendar.getInstance()
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(calendar.get(Calendar.HOUR_OF_DAY))
            .setMinute(calendar.get(Calendar.MINUTE))
            .setTitleText("Selecciona la hora")
            .build()

        picker.addOnPositiveButtonClickListener {
            val alarm = Alarm(
                id = System.currentTimeMillis().toInt(),
                hour = picker.hour,
                minute = picker.minute,
                enabled = true,
                mode = mode,
                audioUri = pendingAlarmUri?.toString(),
                folderUri = pendingFolderUri?.toString()
            )
            addAlarm(alarm)
        }

        picker.show(supportFragmentManager, "timePicker")
    }

    private fun addAlarm(alarm: Alarm) {
        alarms.add(alarm)
        alarms.sortBy { it.hour * 60 + it.minute }
        alarmAdapter.notifyDataSetChanged()
        saveAlarms()
        scheduleAlarm(alarm)
        Toast.makeText(this, "Alarma agregada: ${alarm.getTimeString()}", Toast.LENGTH_SHORT).show()
    }

    private fun onToggleAlarm(alarm: Alarm, enabled: Boolean) {
        alarm.enabled = enabled
        if (enabled) {
            scheduleAlarm(alarm)
        } else {
            cancelAlarm(alarm)
        }
        saveAlarms()
    }

    private fun onDeleteAlarm(alarm: Alarm) {
        cancelAlarm(alarm)
        alarms.remove(alarm)
        alarmAdapter.notifyDataSetChanged()
        saveAlarms()
        Toast.makeText(this, "Alarma eliminada", Toast.LENGTH_SHORT).show()
    }

    private fun scheduleAlarm(alarm: Alarm) {
        val alarmScheduler = AlarmScheduler(this)
        alarmScheduler.schedule(alarm)
    }

    private fun cancelAlarm(alarm: Alarm) {
        val alarmScheduler = AlarmScheduler(this)
        alarmScheduler.cancel(alarm)
    }

    private fun saveAlarms() {
        val prefs = getSharedPreferences("alarms", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val alarmsJson = alarms.joinToString("|") { alarm ->
            "${alarm.id},${alarm.hour},${alarm.minute},${alarm.enabled},${alarm.mode.name},${alarm.audioUri ?: ""},${alarm.folderUri ?: ""}"
        }
        editor.putString("alarms_list", alarmsJson)
        editor.apply()
    }

    private fun loadAlarms() {
        val prefs = getSharedPreferences("alarms", Context.MODE_PRIVATE)
        val alarmsJson = prefs.getString("alarms_list", "") ?: ""
        if (alarmsJson.isNotEmpty()) {
            alarms.clear()
            alarmsJson.split("|").forEach { alarmString ->
                val parts = alarmString.split(",")
                if (parts.size >= 7) {
                    val alarm = Alarm(
                        id = parts[0].toInt(),
                        hour = parts[1].toInt(),
                        minute = parts[2].toInt(),
                        enabled = parts[3].toBoolean(),
                        mode = AlarmMode.valueOf(parts[4]),
                        audioUri = parts[5].ifEmpty { null },
                        folderUri = parts[6].ifEmpty { null }
                    )
                    alarms.add(alarm)
                }
            }
            alarms.sortBy { it.hour * 60 + it.minute }
            alarmAdapter.notifyDataSetChanged()
        }
    }
}

// AlarmAdapter
class AlarmAdapter(
    private val alarms: List<Alarm>,
    private val onToggle: (Alarm, Boolean) -> Unit,
    private val onDelete: (Alarm) -> Unit
) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    class AlarmViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvMode: TextView = view.findViewById(R.id.tvMode)
        val tvRepeatDays: TextView = view.findViewById(R.id.tvRepeatDays)
        val tvVibrate: TextView = view.findViewById(R.id.tvVibrate)
        val switchEnabled: Switch = view.findViewById(R.id.switchEnabled)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alarm, parent, false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = alarms[position]
        holder.tvTime.text = alarm.getTimeString()
        
        // Show filename or "RANDOM" - NO EMOJIS
        holder.tvMode.text = when (alarm.mode) {
            AlarmMode.SINGLE_FILE -> {
                val uri = alarm.audioUri
                if (uri != null) {
                    val fullPath = uri.toString()
                    val filename = fullPath.substringAfterLast('/', "Archivo de audio")
                    if (filename.isNotEmpty()) filename else "Archivo de audio"
                } else {
                    "Archivo de audio"
                }
            }
            AlarmMode.RANDOM_FOLDER -> "RANDOM"
        }
        
        // Show repeat days (placeholder - would need to be stored in Alarm data class)
        holder.tvRepeatDays.text = "L, M, X, J, V"
        holder.tvRepeatDays.visibility = View.VISIBLE
        
        // Show vibration status (placeholder - would need to be stored in Alarm data class)
        holder.tvVibrate.text = "Vibración"
        holder.tvVibrate.visibility = View.VISIBLE
        
        holder.switchEnabled.isChecked = alarm.enabled
        holder.switchEnabled.setOnCheckedChangeListener { _, isChecked ->
            onToggle(alarm, isChecked)
        }
        holder.btnDelete.setOnClickListener {
            onDelete(alarm)
        }
    }

    override fun getItemCount() = alarms.size
}

// Data class
data class Alarm(
    val id: Int,
    val hour: Int,
    val minute: Int,
    var enabled: Boolean,
    val mode: AlarmMode,
    val audioUri: String?,
    val folderUri: String?
) {
    fun getTimeString(): String {
        return String.format("%02d:%02d", hour, minute)
    }
}

enum class AlarmMode {
    SINGLE_FILE,
    RANDOM_FOLDER
}
