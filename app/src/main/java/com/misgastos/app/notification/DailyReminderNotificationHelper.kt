package com.misgastos.app.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.misgastos.app.MainActivity
import com.misgastos.app.R

object DailyReminderNotificationHelper {

    const val CHANNEL_ID = "smart_expense_reminder_channel"
    private const val CHANNEL_NAME = "Recordatorios de gastos"
    private const val CHANNEL_DESCRIPTION = "Recordatorios inteligentes y resúmenes de tus gastos diarios"
    private const val NOTIFICATION_ID = 1001

    /**
     * Crea o actualiza el canal de notificaciones requerido para Android 8.0+ (API 26+).
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                setShowBadge(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    /**
     * Muestra una notificación dinámica e interactiva con acciones directas.
     *
     * @param context Contexto de la aplicación
     * @param title Título dinámico
     * @param message Cuerpo del mensaje generado dinámicamente
     * @param isSummary Indica si es un resumen de gastos (true) o un recordatorio (false)
     */
    fun showSmartNotification(
        context: Context,
        title: String,
        message: String,
        isSummary: Boolean
    ) {
        // En Android 13+ (API 33+), verificar permiso POST_NOTIFICATIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionGranted = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!permissionGranted) {
                return
            }
        }

        createNotificationChannel(context)

        // Acción 1: Registrar gasto (Paso 1: ¿Cuánto gastaste?)
        val registerIntent = Intent(Intent.ACTION_VIEW, Uri.parse("misgastos://register")).apply {
            setClass(context, MainActivity::class.java)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val registerPendingIntent = PendingIntent.getActivity(
            context,
            101,
            registerIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Acción 2: Ver gastos de hoy
        val expensesIntent = Intent(Intent.ACTION_VIEW, Uri.parse("misgastos://expenses")).apply {
            setClass(context, MainActivity::class.java)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val expensesPendingIntent = PendingIntent.getActivity(
            context,
            102,
            expensesIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Al pulsar en el cuerpo de la notificación:
        val mainPendingIntent = if (isSummary) expensesPendingIntent else registerPendingIntent

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_reminder)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(mainPendingIntent)
            .setAutoCancel(true)
            .addAction(
                R.drawable.ic_notification_reminder,
                "Registrar gasto",
                registerPendingIntent
            )
            .addAction(
                R.drawable.ic_notification_reminder,
                "Ver gastos",
                expensesPendingIntent
            )
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        } catch (_: SecurityException) {
            // Manejo seguro en caso de revocación de permisos
        }
    }
}
