package com.misgastos.app

import android.app.Application
import com.misgastos.app.di.AppContainer
import com.misgastos.app.notification.DailyReminderNotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MisGastosApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        container = AppContainer(this)

        // Aplicar el modo de tema guardado (Claro / Oscuro / Sistema) de forma asíncrona sin bloquear el arranque
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            try {
                val initialTheme = container.preferencesManager.themeMode.first()
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    container.preferencesManager.applyNightMode(initialTheme)
                }
            } catch (_: Exception) {}
        }

        // Crear el canal de notificaciones de recordatorios
        DailyReminderNotificationHelper.createNotificationChannel(this)

        // Sincronizar programación de alarmas para recordatorios diarios en segundo plano
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default).launch {
            try {
                com.misgastos.app.notification.DailyReminderScheduler.syncWithPreferences(this@MisGastosApplication)
            } catch (_: Exception) {}
        }
    }

    companion object {
        private lateinit var instance: MisGastosApplication

        /** Acceso estático al contexto de aplicación para casos donde no hay contexto disponible. */
        val appContext: android.content.Context
            get() = instance.applicationContext
    }
}
