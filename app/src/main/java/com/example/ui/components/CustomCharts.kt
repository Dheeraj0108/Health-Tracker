package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.nativeCanvas
import com.example.ui.theme.*
import com.example.ui.viewmodel.DailySummaryPoint

@Composable
fun CircularProgressRing(
    progress: Float, // 0.0 to 1.0+
    color: Color,
    backgroundColor: Color = Slate800,
    strokeWidth: Dp = 10.dp,
    size: Dp = 80.dp,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceAtLeast(0f),
        animationSpec = tween(durationMillis = 800),
        label = "ring_progress"
    )

    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Draw background circle
            drawCircle(
                color = backgroundColor,
                radius = (this.size.minDimension - strokeWidth.toPx()) / 2f,
                style = Stroke(width = strokeWidth.toPx())
            )

            // Draw active arc
            val sweepAngle = (animatedProgress * 360f).coerceAtMost(360f)
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(strokeWidth.toPx() / 2f, strokeWidth.toPx() / 2f),
                size = Size(
                    this.size.width - strokeWidth.toPx(),
                    this.size.height - strokeWidth.toPx()
                ),
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }
        Box(
            modifier = Modifier.padding(strokeWidth),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}

@Composable
fun WeeklyBarChart(
    data: List<DailySummaryPoint>,
    metricType: String, // "WATER", "CAFFEINE", "CALORIES", "EXERCISE", "SLEEP", "SLEEP_QUALITY"
    modifier: Modifier = Modifier,
    barColor: Color = Teal400
) {
    if (data.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("No chart data available yet.", color = Slate400, fontSize = 14.sp)
        }
        return
    }

    // Find max value to scale chart
    val maxVal = data.maxOf { point ->
        when (metricType) {
            "WATER" -> point.waterMl
            "CAFFEINE" -> point.caffeineMg
            "CALORIES" -> point.caloriesKcal
            "EXERCISE" -> point.exerciseMin
            "SLEEP" -> point.sleepHours
            "SLEEP_QUALITY" -> point.sleepQuality
            else -> 1.0
        }
    }.coerceAtLeast(1.0)

    // Smooth bar-rise animation on view change/load
    var animationTarget by remember { androidx.compose.runtime.mutableStateOf(0f) }
    androidx.compose.runtime.LaunchedEffect(metricType, data) {
        animationTarget = 0f
        // Let it render 0f first, then animate to 1f
        kotlinx.coroutines.delay(20)
        animationTarget = 1f
    }
    val animProgress by animateFloatAsState(
        targetValue = animationTarget,
        animationSpec = tween(durationMillis = 800),
        label = "chart_bar_rise"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        val title = when (metricType) {
            "WATER" -> "Hydration (ml)"
            "CAFFEINE" -> "Caffeine (mg)"
            "CALORIES" -> "Fuel (kcal)"
            "EXERCISE" -> "Exercise (mins)"
            "SLEEP" -> "Sleep (hours)"
            "SLEEP_QUALITY" -> "Sleep Quality (1-5)"
            else -> ""
        }
        Text(
            text = "7-Day Progress: $title",
            style = MaterialTheme.typography.titleSmall,
            color = Slate50,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val spacing = 20f
            val barCount = data.size
            val barWidth = (canvasWidth - (spacing * (barCount + 1))) / barCount

            data.forEachIndexed { index, point ->
                val value = when (metricType) {
                    "WATER" -> point.waterMl
                    "CAFFEINE" -> point.caffeineMg
                    "CALORIES" -> point.caloriesKcal
                    "EXERCISE" -> point.exerciseMin
                    "SLEEP" -> point.sleepHours
                    "SLEEP_QUALITY" -> point.sleepQuality
                    else -> 0.0
                }

                // Scale bar height, leaving room for label
                val labelHeight = 35f
                val chartHeight = canvasHeight - labelHeight
                val barHeight = ((value / maxVal) * chartHeight * animProgress).toFloat().coerceAtLeast(10f)

                val x = spacing + index * (barWidth + spacing)
                val y = chartHeight - barHeight

                // Draw Bar
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(barColor.copy(alpha = 0.8f), barColor)
                    ),
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(8f, 8f)
                )

                // Label (Day Name)
                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        color = android.graphics.Color.parseColor("#94A3B8")
                        textSize = 28f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    drawText(
                        point.dayLabel,
                        x + barWidth / 2,
                        canvasHeight - 5f,
                        paint
                    )
                }
            }
        }
    }
}
