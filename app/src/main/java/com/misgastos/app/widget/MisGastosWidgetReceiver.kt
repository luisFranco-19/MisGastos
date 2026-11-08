package com.misgastos.app.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class MisGastosWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = MisGastosWidget()

    companion object {

        /**
         * Solicita actualización del widget.
         * Solo envía broadcast si hay widgets instalados — no hace trabajo innecesario.
         */
        fun updateWidgets(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val component = ComponentName(context, MisGastosWidgetReceiver::class.java)
            val ids = manager.getAppWidgetIds(component)
            if (ids.isNotEmpty()) {
                val intent = android.content.Intent(context, MisGastosWidgetReceiver::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                }
                context.sendBroadcast(intent)
            }
        }

        /**
         * Alias de [updateWidgets] — usado desde ViewModels que tienen acceso al appContext.
         */
        fun scheduleWidgetUpdate(context: Context) = updateWidgets(context)
    }
}
