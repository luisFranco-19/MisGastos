package com.misgastos.app.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.misgastos.app.data.local.PreferencesManager
import com.misgastos.app.data.local.database.MisGastosDatabase
import com.misgastos.app.notification.DailyReminderNotificationHelper
import com.misgastos.app.notification.NotificationTemplates
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Worker ejecutado periódicamente (aproximadamente cada 3 horas) para evaluar y emitir
 * notificaciones inteligentes y dinámicas sobre los gastos del día.
 *
 * Principios:
 * 1. Eficiente y respetuoso de batería (se ejecuta en segundos y finaliza).
 * 2. Horario inteligente: Solo emite notificaciones entre 08:00 y 22:00 (hora local).
 * 3. Anti-Spam: Límite de notificaciones diarias y ventana mínima de separación entre envíos.
 * 4. Contextual:
 *    - 0 gastos hoy -> Emite recordatorio dinámico con rotación de plantillas.
 *    - >0 gastos hoy -> Emite resumen dinámico con total y conteo del día local.
 */
class DailyReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        private const val MIN_NOTIFICATION_INTERVAL_HOURS = 4
        private const val MAX_DAILY_NOTIFICATIONS = 3
        private const val ACTIVE_HOUR_START = 8  // 08:00
        private const val ACTIVE_HOUR_END = 22   // 22:00
    }

    override suspend fun doWork(): Result {
        val preferencesManager = PreferencesManager(applicationContext)

        // 1. Comprobar si los recordatorios inteligentes están activados
        val isEnabled = preferencesManager.dailyReminderEnabled.first()
        if (!isEnabled) {
            return Result.success()
        }

        // 2. Comprobar horario inteligente local (08:00 - 22:00)
        val now = LocalDateTime.now()
        if (now.hour < ACTIVE_HOUR_START || now.hour >= ACTIVE_HOUR_END) {
            return Result.success()
        }

        // 3. Control de frecuencia y Anti-Spam
        val today = LocalDate.now()
        val todayStr = today.toString()
        val lastDate = preferencesManager.lastNotificationDate.first()
        val lastTimestamp = preferencesManager.lastNotificationTimestamp.first()
        val currentDailyCount = if (lastDate == todayStr) {
            preferencesManager.dailyNotificationCount.first()
        } else {
            0
        }

        // Máximo diario alcanzado
        if (currentDailyCount >= MAX_DAILY_NOTIFICATIONS) {
            return Result.success()
        }

        // Separación mínima entre notificaciones (al menos 4 horas)
        if (lastTimestamp > 0) {
            val elapsedMillis = System.currentTimeMillis() - lastTimestamp
            val elapsedHours = elapsedMillis / (1000 * 60 * 60)
            if (lastDate == todayStr && elapsedHours < MIN_NOTIFICATION_INTERVAL_HOURS) {
                return Result.success()
            }
        }

        // 4. Consultar Room con la fecha local exacta
        val database = MisGastosDatabase.getInstance(applicationContext)
        val expensesToday = try {
            database.expenseDao().getExpensesByDate(today).first()
        } catch (_: Exception) {
            emptyList()
        }

        val currencySymbol = preferencesManager.currency.first()
        val newDailyCount = currentDailyCount + 1

        if (expensesToday.isEmpty()) {
            // Caso A: 0 gastos hoy -> Recordatorio dinámico
            val lastReminderIndex = preferencesManager.lastReminderIndex.first()
            val content = NotificationTemplates.getReminderContent(lastReminderIndex)

            DailyReminderNotificationHelper.showSmartNotification(
                context = applicationContext,
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
            // Caso B: Ya existen gastos hoy -> Resumen dinámico con conteo y total
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
                context = applicationContext,
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

        return Result.success()
    }
}
