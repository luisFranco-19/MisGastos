package com.misgastos.app.notification

data class NotificationContent(
    val title: String,
    val message: String,
    val templateIndex: Int
)

object NotificationTemplates {

    private data class Template(
        val title: String,
        val messageBuilder: (currency: String, totalFormatted: String, count: Int) -> String
    )

    // ─── Plantillas para cuando NO hay gastos registrados hoy (gastos == 0) ──
    private val reminderTemplates = listOf(
        Template("💰 ¿Ya registraste tus gastos de hoy?") { _, _, _ ->
            "No olvides registrar tus gastos para mantener tu presupuesto al día."
        },
        Template("📝 Recuerda anotar tus gastos de hoy") { _, _, _ ->
            "Lleva el control de tus compras y consumos para cuidar tu economía."
        },
        Template("💸 ¿Has tenido algún gasto hoy?") { _, _, _ ->
            "Regístralo para mantener tus finanzas bajo control."
        },
        Template("📊 Lleva el control de tus gastos de hoy") { _, _, _ ->
            "Un registro constante te ayuda a cumplir tu presupuesto."
        },
        Template("💰 No olvides registrar lo que has gastado hoy") { _, _, _ ->
            "Mantén tu registro financiero actualizado en Mis Gastos."
        },
        Template("📒 Mantén tus gastos al día") { _, _, _ ->
            "¿Has realizado algún gasto hoy? Anótalo en un instante."
        }
    )

    // ─── Plantillas para cuando SÍ hay gastos registrados hoy (gastos > 0) ───
    private val summaryTemplates = listOf(
        Template("💰 Así van tus gastos hoy") { currency, total, count ->
            val countWord = if (count == 1) "gasto" else "gastos"
            "Hoy llevas $currency $total en $count $countWord."
        },
        Template("📊 Tu resumen de hoy") { currency, total, count ->
            val countWord = if (count == 1) "registro" else "registros"
            "Llevas $currency $total gastados en $count $countWord."
        },
        Template("💸 Gastos de hoy") { currency, total, count ->
            val countWord = if (count == 1) "gasto" else "gastos"
            "Ya registraste $count $countWord por un total de $currency $total."
        },
        Template("📒 Tu día en números") { currency, total, _ ->
            "Hoy has gastado $currency $total en tus registros del día."
        }
    )

    /**
     * Obtiene una plantilla de recordatorio rotativa evitando repetir la última utilizada.
     */
    fun getReminderContent(lastIndex: Int): NotificationContent {
        val nextIndex = if (reminderTemplates.size <= 1) {
            0
        } else {
            var candidate = (lastIndex + 1) % reminderTemplates.size
            if (candidate == lastIndex) {
                candidate = (candidate + 1) % reminderTemplates.size
            }
            candidate
        }
        val template = reminderTemplates[nextIndex]
        return NotificationContent(
            title = template.title,
            message = template.messageBuilder("", "", 0),
            templateIndex = nextIndex
        )
    }

    /**
     * Obtiene una plantilla de resumen rotativa evitando repetir la última utilizada.
     */
    fun getSummaryContent(
        currency: String,
        total: Double,
        count: Int,
        lastIndex: Int
    ): NotificationContent {
        val nextIndex = if (summaryTemplates.size <= 1) {
            0
        } else {
            var candidate = (lastIndex + 1) % summaryTemplates.size
            if (candidate == lastIndex) {
                candidate = (candidate + 1) % summaryTemplates.size
            }
            candidate
        }
        val template = summaryTemplates[nextIndex]
        val totalFormatted = String.format("%,.2f", total)
        return NotificationContent(
            title = template.title,
            message = template.messageBuilder(currency, totalFormatted, count),
            templateIndex = nextIndex
        )
    }
}
