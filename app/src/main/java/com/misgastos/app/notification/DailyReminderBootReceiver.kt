package com.misgastos.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Receiver para restaurar la programación de las alarmas de recordatorios tras el reinicio
 * del dispositivo, actualización de la app o cambio de hora/zona horaria.
 */
class DailyReminderBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED -> {
                val pendingResult = goAsync()
                CoroutineScope(Dispatchers.Default).launch {
                    try {
                        DailyReminderScheduler.syncWithPreferences(context)
                    } finally {
                        pendingResult.finish()
                    }
                }
            }
        }
    }
}
