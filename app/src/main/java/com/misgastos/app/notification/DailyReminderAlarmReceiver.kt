package com.misgastos.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.misgastos.app.data.local.PreferencesManager
import com.misgastos.app.data.local.database.MisGastosDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * BroadcastReceiver que se despierta mediante AlarmManager incluso si la app
 * está cerrada o el usuario no la abrió en todo el día.
 *
 * Consulta la base de datos de Room en segundo plano, emite la notificación inteligente
 * correspondiente y programa automáticamente el siguiente horario.
 */
class DailyReminderAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_DAILY_REMINDER = "com.misgastos.app.action.DAILY_REMINDER"
        private const val TAG = "DailyReminderAlarm"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val preferencesManager = PreferencesManager(context)

                // 1. Verificar si las notificaciones están activadas
                val isEnabled = preferencesManager.dailyReminderEnabled.first()
                if (!isEnabled) {
                    Log.d(TAG, "Notificaciones desactivadas en preferencias")
                    return@launch
                }

                // 2. Obtener gastos del día actual
                val today = LocalDate.now()
                val todayStr = today.toString()
                val database = MisGastosDatabase.getInstance(context)
                val expensesToday = try {
                    database.expenseDao().getExpensesByDate(today).first()
                } catch (e: Exception) {
                    Log.e(TAG, "Error consultando gastos: ${e.message}")
                    emptyList()
                }

                val currencySymbol = preferencesManager.currency.first()
                val lastDate = preferencesManager.lastNotificationDate.first()
                val currentDailyCount = if (lastDate == todayStr) {
                    preferencesManager.dailyNotificationCount.first()
                } else {
                    0
                }
                val newDailyCount = currentDailyCount + 1

                if (expensesToday.isEmpty()) {
                    // Caso 0 gastos: Recordatorio para anotar gastos
                    val lastReminderIndex = preferencesManager.lastReminderIndex.first()
                    val content = NotificationTemplates.getReminderContent(lastReminderIndex)

                    DailyReminderNotificationHelper.showSmartNotification(
                        context = context,
                        title = content.title,
                        message = content.message,
                        isSummary = false
                    )

                    preferencesManager.recordNotificationSent(
                        timestamp = System.currentTimeMillis(),
                        date = todayStr,
                        count = newDailyCount,
                        reminderIndex = content.templateIndex
                    )
                } else {
                    // Caso con gastos: Resumen del día
                    val count = expensesToday.size
                    val total = expensesToday.sumOf { it.amount }
                    val lastSummaryIndex = preferencesManager.lastSummaryIndex.first()
                    val content = NotificationTemplates.getSummaryContent(
                        currency = currencySymbol,
                        total = total,
                        count = count,
                        lastIndex = lastSummaryIndex
                    )

                    DailyReminderNotificationHelper.showSmartNotification(
                        context = context,
                        title = content.title,
                        message = content.message,
                        isSummary = true
                    )

                    preferencesManager.recordNotificationSent(
                        timestamp = System.currentTimeMillis(),
                        date = todayStr,
                        count = newDailyCount,
                        summaryIndex = content.templateIndex
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error en DailyReminderAlarmReceiver: ${e.message}", e)
            } finally {
                // Programar el siguiente recordatorio y finalizar goAsync
                try {
                    DailyReminderScheduler.scheduleNextReminder(context)
                } catch (e: Exception) {
                    Log.e(TAG, "Error reprogramando siguiente alarma: ${e.message}", e)
                }
                pendingResult.finish()
            }
        }
    }
}
