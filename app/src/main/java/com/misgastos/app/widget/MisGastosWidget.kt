package com.misgastos.app.widget

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.ButtonDefaults
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.misgastos.app.data.local.PreferencesManager
import com.misgastos.app.data.local.database.MisGastosDatabase
import com.misgastos.app.presentation.theme.ThemeMode
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.YearMonth

/**
 * Widget oficial de Mis Gastos:
 * Totalmente responsive y adaptable con 3 niveles de diseño según el tamaño seleccionado:
 * 1. Compacto: Información esencial y mínima (💰 Mis Gastos + Monto)
 * 2. Mediano: Resumen del mes, presupuesto vs disponible, barra de progreso y botón [+]
 * 3. Grande: Dashboard completo con métricas, porcentaje y barra de 3 accesos rápidos directos.
 */
class MisGastosWidget : GlanceAppWidget() {

    companion object {
        val SIZE_COMPACT = DpSize(100.dp, 50.dp)
        val SIZE_MEDIUM = DpSize(180.dp, 110.dp)
        val SIZE_LARGE = DpSize(260.dp, 180.dp)
    }

    override val sizeMode = SizeMode.Responsive(
        setOf(
            SIZE_COMPACT,
            SIZE_MEDIUM,
            SIZE_LARGE
        )
    )

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val database = MisGastosDatabase.getInstance(context)
        val preferencesManager = PreferencesManager(context)

        val now = LocalDate.now()
        val yearMonth = YearMonth.from(now)

        // 1. Total gastado este mes
        val monthlyTotal = try {
            database.expenseDao().getTotalByDateRange(
                yearMonth.atDay(1), yearMonth.atEndOfMonth()
            ).first()
        } catch (_: Exception) {
            0.0
        }

        // 2. Presupuesto configurado para el mes actual
        val budgetEntity = try {
            database.budgetDao().getBudgetOnce(now.year, now.monthValue)
        } catch (_: Exception) {
            null
        }
        val budgetAmount = budgetEntity?.monthlyAmount ?: 0.0

        // 3. Moneda y tema
        val currencySymbol = try {
            preferencesManager.currency.first()
        } catch (_: Exception) {
            "C$"
        }

        val themeMode = try {
            preferencesManager.themeMode.first()
        } catch (_: Exception) {
            ThemeMode.SYSTEM
        }

        provideContent {
            GlanceTheme {
                WidgetContainer(
                    monthlyTotal = monthlyTotal,
                    budgetAmount = budgetAmount,
                    currencySymbol = currencySymbol,
                    themeMode = themeMode
                )
            }
        }
    }

    @Composable
    private fun WidgetContainer(
        monthlyTotal: Double,
        budgetAmount: Double,
        currencySymbol: String,
        themeMode: ThemeMode
    ) {
        val size = LocalSize.current

        // Paleta de colores adaptable (Claro / Oscuro / Sistema)
        val isForcedDark = themeMode == ThemeMode.DARK
        val isForcedLight = themeMode == ThemeMode.LIGHT

        val bgColor = when {
            isForcedDark -> ColorProvider(day = Color(0xFF161A20), night = Color(0xFF161A20))
            isForcedLight -> ColorProvider(day = Color.White, night = Color.White)
            else -> ColorProvider(day = Color.White, night = Color(0xFF161A20))
        }

        val cardSurfaceColor = when {
            isForcedDark -> ColorProvider(day = Color(0xFF222730), night = Color(0xFF222730))
            isForcedLight -> ColorProvider(day = Color(0xFFF1F5F9), night = Color(0xFFF1F5F9))
            else -> ColorProvider(day = Color(0xFFF1F5F9), night = Color(0xFF222730))
        }

        val textPrimaryColor = when {
            isForcedDark -> ColorProvider(day = Color.White, night = Color.White)
            isForcedLight -> ColorProvider(day = Color(0xFF1A1A1A), night = Color(0xFF1A1A1A))
            else -> ColorProvider(day = Color(0xFF1A1A1A), night = Color.White)
        }

        val textSecondaryColor = when {
            isForcedDark -> ColorProvider(day = Color(0xFF9E9E9E), night = Color(0xFF9E9E9E))
            isForcedLight -> ColorProvider(day = Color(0xFF666666), night = Color(0xFF666666))
            else -> ColorProvider(day = Color(0xFF666666), night = Color(0xFF9E9E9E))
        }

        val tealAccentColor = when {
            isForcedDark -> ColorProvider(day = Color(0xFF26A69A), night = Color(0xFF26A69A))
            isForcedLight -> ColorProvider(day = Color(0xFF00796B), night = Color(0xFF00796B))
            else -> ColorProvider(day = Color(0xFF00796B), night = Color(0xFF26A69A))
        }

        val openAppIntent = Intent(Intent.ACTION_VIEW, Uri.parse("misgastos://expenses")).apply {
            setClassName("com.misgastos.app", "com.misgastos.app.MainActivity")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val registerIntent = Intent(Intent.ACTION_VIEW, Uri.parse("misgastos://register")).apply {
            setClassName("com.misgastos.app", "com.misgastos.app.MainActivity")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val expensesIntent = Intent(Intent.ACTION_VIEW, Uri.parse("misgastos://expenses")).apply {
            setClassName("com.misgastos.app", "com.misgastos.app.MainActivity")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val statsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("misgastos://statistics")).apply {
            setClassName("com.misgastos.app", "com.misgastos.app.MainActivity")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val remainingBudget = if (budgetAmount > 0) (budgetAmount - monthlyTotal).coerceAtLeast(0.0) else 0.0
        val budgetProgress = if (budgetAmount > 0) (monthlyTotal / budgetAmount).coerceIn(0.0, 1.0).toFloat() else 0f
        val budgetPercentage = if (budgetAmount > 0) ((monthlyTotal / budgetAmount) * 100).toInt() else 0

        val progressColor = when {
            budgetPercentage >= 100 -> ColorProvider(day = Color(0xFFD32F2F), night = Color(0xFFEF5350))
            budgetPercentage >= 80 -> ColorProvider(day = Color(0xFFE65100), night = Color(0xFFFFA726))
            else -> ColorProvider(day = Color(0xFF2E7D32), night = Color(0xFF4CAF50))
        }

        // Selección dinámica del layout según el tamaño disponible en pantalla
        when {
            size.width >= 260.dp && size.height >= 170.dp -> {
                LargeWidgetLayout(
                    monthlyTotal = monthlyTotal,
                    budgetAmount = budgetAmount,
                    remainingBudget = remainingBudget,
                    budgetProgress = budgetProgress,
                    budgetPercentage = budgetPercentage,
                    currencySymbol = currencySymbol,
                    bgColor = bgColor,
                    cardSurfaceColor = cardSurfaceColor,
                    textPrimaryColor = textPrimaryColor,
                    textSecondaryColor = textSecondaryColor,
                    tealAccentColor = tealAccentColor,
                    progressColor = progressColor,
                    openAppIntent = openAppIntent,
                    registerIntent = registerIntent,
                    expensesIntent = expensesIntent,
                    statsIntent = statsIntent
                )
            }
            size.width >= 160.dp && size.height >= 95.dp -> {
                MediumWidgetLayout(
                    monthlyTotal = monthlyTotal,
                    budgetAmount = budgetAmount,
                    remainingBudget = remainingBudget,
                    budgetProgress = budgetProgress,
                    currencySymbol = currencySymbol,
                    bgColor = bgColor,
                    textPrimaryColor = textPrimaryColor,
                    textSecondaryColor = textSecondaryColor,
                    tealAccentColor = tealAccentColor,
                    progressColor = progressColor,
                    openAppIntent = openAppIntent,
                    registerIntent = registerIntent
                )
            }
            else -> {
                CompactWidgetLayout(
                    monthlyTotal = monthlyTotal,
                    currencySymbol = currencySymbol,
                    bgColor = bgColor,
                    textPrimaryColor = textPrimaryColor,
                    tealAccentColor = tealAccentColor,
                    openAppIntent = openAppIntent
                )
            }
        }
    }

    // ─── 1. WIDGET COMPACTO ──────────────────────────────────────────────────
    @Composable
    private fun CompactWidgetLayout(
        monthlyTotal: Double,
        currencySymbol: String,
        bgColor: ColorProvider,
        textPrimaryColor: ColorProvider,
        tealAccentColor: ColorProvider,
        openAppIntent: Intent
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(bgColor)
                .cornerRadius(20.dp)
                .padding(12.dp)
                .clickable(actionStartActivity(openAppIntent)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💰",
                    style = TextStyle(fontSize = 13.sp)
                )
                Spacer(modifier = GlanceModifier.width(4.dp))
                Text(
                    text = "Mis Gastos",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = tealAccentColor
                    )
                )
            }

            Spacer(modifier = GlanceModifier.height(4.dp))

            Text(
                text = "$currencySymbol ${String.format("%,.2f", monthlyTotal)}",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = textPrimaryColor
                )
            )
        }
    }

    // ─── 2. WIDGET MEDIANO ───────────────────────────────────────────────────
    @Composable
    private fun MediumWidgetLayout(
        monthlyTotal: Double,
        budgetAmount: Double,
        remainingBudget: Double,
        budgetProgress: Float,
        currencySymbol: String,
        bgColor: ColorProvider,
        textPrimaryColor: ColorProvider,
        textSecondaryColor: ColorProvider,
        tealAccentColor: ColorProvider,
        progressColor: ColorProvider,
        openAppIntent: Intent,
        registerIntent: Intent
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(bgColor)
                .cornerRadius(24.dp)
                .padding(14.dp)
                .clickable(actionStartActivity(openAppIntent)),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.Start
        ) {
            // Fila de encabezado: Título + Botón [+]
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mis Gastos",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = tealAccentColor
                    )
                )

                Spacer(modifier = GlanceModifier.defaultWeight())

                // Botón [+] para registrar gasto directamente
                Button(
                    text = "+",
                    onClick = actionStartActivity(registerIntent),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = tealAccentColor,
                        contentColor = ColorProvider(day = Color.White, night = Color.White)
                    ),
                    modifier = GlanceModifier
                        .height(30.dp)
                        .width(40.dp)
                        .cornerRadius(10.dp)
                )
            }

            Spacer(modifier = GlanceModifier.height(4.dp))

            // Gastado este mes
            Text(
                text = "Gastado este mes",
                style = TextStyle(
                    fontSize = 11.sp,
                    color = textSecondaryColor
                )
            )
            Text(
                text = "$currencySymbol ${String.format("%,.2f", monthlyTotal)}",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    color = textPrimaryColor
                )
            )

            // Si hay presupuesto configurado, muestra desglose y barra
            if (budgetAmount > 0) {
                Spacer(modifier = GlanceModifier.height(6.dp))

                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ppto: $currencySymbol ${String.format("%,.0f", budgetAmount)}",
                        style = TextStyle(
                            fontSize = 11.sp,
                            color = textSecondaryColor
                        )
                    )
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    Text(
                        text = "Disp: $currencySymbol ${String.format("%,.0f", remainingBudget)}",
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp,
                            color = progressColor
                        )
                    )
                }

                Spacer(modifier = GlanceModifier.height(4.dp))

                LinearProgressIndicator(
                    progress = budgetProgress,
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .cornerRadius(3.dp),
                    color = progressColor,
                    backgroundColor = ColorProvider(day = Color(0xFFE0E0E0), night = Color(0xFF2C2C2E))
                )
            }
        }
    }

    // ─── 3. WIDGET GRANDE ────────────────────────────────────────────────────
    @Composable
    private fun LargeWidgetLayout(
        monthlyTotal: Double,
        budgetAmount: Double,
        remainingBudget: Double,
        budgetProgress: Float,
        budgetPercentage: Int,
        currencySymbol: String,
        bgColor: ColorProvider,
        cardSurfaceColor: ColorProvider,
        textPrimaryColor: ColorProvider,
        textSecondaryColor: ColorProvider,
        tealAccentColor: ColorProvider,
        progressColor: ColorProvider,
        openAppIntent: Intent,
        registerIntent: Intent,
        expensesIntent: Intent,
        statsIntent: Intent
    ) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(bgColor)
                .cornerRadius(24.dp)
                .padding(16.dp)
                .clickable(actionStartActivity(openAppIntent)),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.Start
        ) {
            // 1. Header
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💰",
                        style = TextStyle(fontSize = 14.sp)
                    )
                    Spacer(modifier = GlanceModifier.width(6.dp))
                    Text(
                        text = "Mis Gastos",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = tealAccentColor
                        )
                    )
                }

                Spacer(modifier = GlanceModifier.defaultWeight())

                if (budgetAmount > 0) {
                    Text(
                        text = "$budgetPercentage% utilizado",
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp,
                            color = progressColor
                        )
                    )
                }
            }

            Spacer(modifier = GlanceModifier.height(8.dp))

            // 2. Gastado este mes (Hero format)
            Text(
                text = "Gastado este mes",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = textSecondaryColor
                )
            )
            Text(
                text = "$currencySymbol ${String.format("%,.2f", monthlyTotal)}",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = textPrimaryColor
                )
            )

            // 3. Presupuesto & Disponible
            if (budgetAmount > 0) {
                Spacer(modifier = GlanceModifier.height(6.dp))

                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Presupuesto: $currencySymbol ${String.format("%,.2f", budgetAmount)}",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = textSecondaryColor
                        )
                    )
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    Text(
                        text = "Disponible: $currencySymbol ${String.format("%,.2f", remainingBudget)}",
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            color = progressColor
                        )
                    )
                }

                Spacer(modifier = GlanceModifier.height(6.dp))

                LinearProgressIndicator(
                    progress = budgetProgress,
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .cornerRadius(4.dp),
                    color = progressColor,
                    backgroundColor = ColorProvider(day = Color(0xFFE0E0E0), night = Color(0xFF2C2C2E))
                )
            }

            Spacer(modifier = GlanceModifier.defaultWeight())

            // 4. Barra de Acciones Rápidas Directas
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Acción 1: + Registrar
                Button(
                    text = "+ Registrar",
                    onClick = actionStartActivity(registerIntent),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = tealAccentColor,
                        contentColor = ColorProvider(day = Color.White, night = Color.White)
                    ),
                    modifier = GlanceModifier
                        .defaultWeight()
                        .height(36.dp)
                        .cornerRadius(10.dp)
                )

                Spacer(modifier = GlanceModifier.width(6.dp))

                // Acción 2: ☷ Gastos
                Button(
                    text = "☷ Gastos",
                    onClick = actionStartActivity(expensesIntent),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = cardSurfaceColor,
                        contentColor = textPrimaryColor
                    ),
                    modifier = GlanceModifier
                        .defaultWeight()
                        .height(36.dp)
                        .cornerRadius(10.dp)
                )

                Spacer(modifier = GlanceModifier.width(6.dp))

                // Acción 3: ◔ Estadísticas
                Button(
                    text = "◔ Stats",
                    onClick = actionStartActivity(statsIntent),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = cardSurfaceColor,
                        contentColor = textPrimaryColor
                    ),
                    modifier = GlanceModifier
                        .defaultWeight()
                        .height(36.dp)
                        .cornerRadius(10.dp)
                )
            }
        }
    }
}
