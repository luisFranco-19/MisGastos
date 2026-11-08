package com.misgastos.app.presentation.components.charts

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.misgastos.app.presentation.components.AppDuration
import com.misgastos.app.presentation.components.AppEasing
import com.misgastos.app.presentation.theme.Spacing

@Composable
fun BarChart(
    data: Map<String, Double>,
    color: Color = chartColors[0],
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return
    val maxValue = data.values.maxOrNull() ?: return

    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(data) {
        animatedProgress.snapTo(0f)
        animatedProgress.animateTo(
            1f,
            tween(durationMillis = AppDuration.CHART, easing = AppEasing.EaseOut)
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        data.entries.forEachIndexed { index, (label, value) ->
            val barColor = chartColors[index % chartColors.size]
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(0.3f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Canvas(
                    modifier = Modifier
                        .weight(0.5f)
                        .height(18.dp)
                ) {
                    val barWidth = (value / maxValue * size.width * animatedProgress.value).toFloat()
                    // Track
                    drawRoundRect(
                        color = barColor.copy(alpha = 0.12f),
                        size = Size(size.width, size.height),
                        cornerRadius = CornerRadius(6f)
                    )
                    // Bar
                    if (barWidth > 0f) {
                        drawRoundRect(
                            color = barColor,
                            size = Size(barWidth, size.height),
                            cornerRadius = CornerRadius(6f)
                        )
                    }
                }
                Text(
                    text = String.format("%,.0f", value),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(0.2f)
                )
            }
        }
    }
}
