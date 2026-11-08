package com.misgastos.app.presentation.components.charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.misgastos.app.presentation.components.AppDuration
import com.misgastos.app.presentation.components.AppEasing
import com.misgastos.app.presentation.theme.Amber400
import com.misgastos.app.presentation.theme.Amber600
import com.misgastos.app.presentation.theme.Spacing
import com.misgastos.app.presentation.theme.Teal300
import com.misgastos.app.presentation.theme.Teal500
import com.misgastos.app.presentation.theme.Teal700

val chartColors = listOf(
    Teal700, Amber600, Color(0xFF7B1FA2), Color(0xFFC62828),
    Teal300, Amber400, Color(0xFF1565C0), Color(0xFF2E7D32),
    Teal500, Color(0xFFEF6C00), Color(0xFF4527A0), Color(0xFF00838F)
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PieChart(
    data: Map<String, Double>,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    val total = data.values.sum()
    if (total == 0.0) return

    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(data) {
        animatedProgress.snapTo(0f)
        animatedProgress.animateTo(
            1f,
            tween(durationMillis = AppDuration.CHART, easing = AppEasing.EaseOut)
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(180.dp)) {
                var startAngle = -90f
                data.entries.forEachIndexed { index, (_, value) ->
                    val sweepAngle = (value / total * 360f * animatedProgress.value).toFloat()
                    val color = chartColors[index % chartColors.size]
                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = 36f),
                        topLeft = Offset(18f, 18f),
                        size = Size(size.width - 36f, size.height - 36f)
                    )
                    startAngle += sweepAngle
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = data.size.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "categorías",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(Spacing.md))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs),
            modifier = Modifier.padding(horizontal = Spacing.sm)
        ) {
            data.entries.forEachIndexed { index, (label, value) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Canvas(modifier = Modifier.size(8.dp)) {
                        drawCircle(chartColors[index % chartColors.size])
                    }
                    Spacer(modifier = Modifier.width(Spacing.xs))
                    Text(
                        text = "$label (${String.format("%.0f", value / total * 100)}%)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
