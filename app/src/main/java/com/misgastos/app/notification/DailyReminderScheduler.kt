package com.misgastos.app.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.misgastos.app.data.local.PreferencesManager
import kotlinx.coroutines.flow.first
import java.util.Calendar

object DailyReminderScheduler {

    private const val TAG = "DailyReminderScheduler"
    private const val REQUEST_CODE = 2001

    // Horarios para los recordatorios diarios inteligentes (14:00 y 20:30)
    private val REMINDER_HOURS = listOf(
        Pair(14, 0),  // 14:00 (2:00 PM)
        Pair(20, 30)  // 20:30 (8:30 PM)
    )

    /**
     * Programa la siguiente alarma utilizando AlarmManager.
     * Al ejecutarse con setAndAllowWhileIdle / setExactAndAllowWhileIdle,
     * despierta el dispositivo puntualmente incluso en modo Doze y con la app cerrada.
     */
    fun scheduleNextReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val intent = Intent(context, DailyReminderAlarmReceiver::class.java).apply {
            action = DailyReminderAlarmReceiver.ACTION_DAILY_REMINDER
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val nextTriggerMillis = calculateNextTriggerMillis()

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            nextTriggerMillis,
                            pendingIntent
                        )
                    } else {
                        alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            nextTriggerMillis,
                            pendingIntent
                        )
                    }
                } else {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        nextTriggerMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    nextTriggerMillis,
                    pendingIntent
                )
            }
            Log.d(TAG, "Próxima alarma programada para: $nextTriggerMillis")
        } catch (e: Exception) {
            Log.e(TAG, "Error al programar la alarma: ${e.message}", e)
        }
    }

    /**
     * Cancela cualquier alarma previamente programada.
     */
    fun cancelReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, DailyReminderAlarmReceiver::class.java).apply {
            action = DailyReminderAlarmReceiver.ACTION_DAILY_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            Log.d(TAG, "Alarma cancelada exitosamente")
        }
    }

    /**
     * Sincroniza la programación de alarmas con el valor guardado en DataStore.
     */
    suspend fun syncWithPreferences(context: Context) {
        val preferencesManager = PreferencesManager(context)
        val isEnabled = preferencesManager.dailyReminderEnabled.first()
        if (isEnabled) {
            scheduleNextReminder(context)
        } else {
            cancelReminder(context)
        }
    }

    /**
     * Calcula el milisegundo exacto para el próximo horario de recordatorio.
     */
    fun calculateNextTriggerMillis(): Long {
        val now = Calendar.getInstance()
        val candidate = Calendar.getInstance()

        for ((hour, minute) in REMINDER_HOURS) {
            candidate.set(Calendar.HOUR_OF_DAY, hour)
            candidate.set(Calendar.MINUTE, minute)
            candidate.set(Calendar.SECOND, 0)
            candidate.set(Calendar.MILLISECOND, 0)

            // Si el horario está en el futuro (al menos 1 minuto adelante)
            if (candidate.timeInMillis > now.timeInMillis + 60_000) {
                return candidate.timeInMillis
            }
        }

        // Si ya pasaron todas las horas de hoy, programar para la primera ventana de mañana
        val firstSlot = REMINDER_HOURS.first()
        candidate.add(Calendar.DAY_OF_YEAR, 1)
        candidate.set(Calendar.HOUR_OF_DAY, firstSlot.first)
        candidate.set(Calendar.MINUTE, firstSlot.second)
        candidate.set(Calendar.SECOND, 0)
        candidate.set(Calendar.MILLISECOND, 0)
        return candidate.timeInMillis
    }
}
