package com.misgastos.app.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.misgastos.app.domain.model.Expense
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object ExpenseExporter {

    /**
     * Genera un archivo CSV compatible con Excel (con UTF-8 BOM) y abre el diálogo
     * nativo de Android para guardar en Google Drive, Excel, WhatsApp, etc.
     */
    fun exportAndShare(
        context: Context,
        expenses: List<Expense>,
        periodTitle: String = "Gastos",
        currency: String = "C$"
    ): Boolean {
        if (expenses.isEmpty()) return false

        try {
            // Carpeta de exportación en cache
            val exportDir = File(context.cacheDir, "exports")
            if (!exportDir.exists()) exportDir.mkdirs()

            val todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
            val sanitizedTitle = periodTitle.replace(" ", "_").lowercase()
            val fileName = "MisGastos_${sanitizedTitle}_$todayStr.csv"
            val file = File(exportDir, fileName)

            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("es"))

            FileOutputStream(file).use { fos ->
                // UTF-8 BOM para que Excel reconozca tildes y caracteres especiales automáticamente
                fos.write(byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()))

                OutputStreamWriter(fos, StandardCharsets.UTF_8).use { writer ->
                    // Encabezados
                    writer.appendLine("ID,Fecha,Categoría,Método de pago,Descripción,Monto ($currency)")

                    var totalAmount = 0.0

                    // Filas de gastos
                    for (expense in expenses) {
                        totalAmount += expense.amount
                        val desc = expense.description?.replace("\"", "\"\"") ?: ""
                        writer.appendLine(
                            "${expense.id}," +
                            "${expense.date.format(dateFormatter)}," +
                            "\"${expense.category}\"," +
                            "\"${expense.paymentMethod.displayName}\"," +
                            "\"$desc\"," +
                            String.format(Locale.US, "%.2f", expense.amount)
                        )
                    }

                    // Fila de resumen de totales
                    writer.appendLine(",,,,TOTAL,${String.format(Locale.US, "%.2f", totalAmount)}")
                    writer.flush()
                }
            }

            // Compartir mediante FileProvider
            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_SUBJECT, "Reporte de Gastos - $periodTitle")
                putExtra(Intent.EXTRA_TEXT, "Adjunto reporte de gastos ($periodTitle) en formato Excel / CSV.")
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooserIntent = Intent.createChooser(shareIntent, "Guardar en Drive / Compartir Excel").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooserIntent)
            return true
        } catch (e: Exception) {
            android.util.Log.e("ExpenseExporter", "Error exporting expenses", e)
            return false
        }
    }
}
