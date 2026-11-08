package com.misgastos.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.misgastos.app.domain.model.Expense
import com.misgastos.app.presentation.theme.Spacing
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface

import androidx.compose.material3.Icon

/**
 * Tarjeta de Gasto estilo Fintech / Apple.
 * - Contenedor con bordes redondeados y borde sutil
 * - Avatar circular con icono vectorial de categoría
 * - Nombre de categoría, descripción y fecha/método legibles
 * - Monto destacado en negrita con prefijo de egreso '-'
 */
@Composable
fun ExpenseCard(
    expense: Expense,
    currency: String = "C$",
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(18.dp)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.lg, vertical = Spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Contenedor circular con Emoji de categoría
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getCategoryEmoji(expense.category, expense.categoryIcon),
                    fontSize = 22.sp
                )
            }

            Spacer(modifier = Modifier.width(Spacing.md))

            // Información principal del gasto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (!expense.description.isNullOrBlank()) expense.description else expense.category,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${formatRelativeDate(expense.date)} · ${expense.category} · ${expense.paymentMethod.displayName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(Spacing.sm))

            // Monto egreso con signo negativo estilizado
            Text(
                text = "-$currency ${String.format("%,.2f", expense.amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.85f)
            )
        }
    }
}

/**
 * Formats date as relative text for better scanning.
 * "Hoy", "Ayer", or "dd MMM" for older dates.
 */
private fun formatRelativeDate(date: LocalDate): String {
    val today = LocalDate.now()
    val daysBetween = ChronoUnit.DAYS.between(date, today)
    return when {
        daysBetween == 0L -> "Hoy"
        daysBetween == 1L -> "Ayer"
        daysBetween < 7L -> "Hace ${daysBetween}d"
        else -> date.format(DateTimeFormatter.ofPattern("dd MMM", Locale("es")))
    }
}
