package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
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
fun FluidDonutProgress(
    progress: Float, // 0.0 to 1.0+
    color: Color,
    backgroundColor: Color = Slate800,
    size: Dp = 70.dp,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "fluid_progress"
    )

    // Endless wave animation
    val infiniteTransition = rememberInfiniteTransition(label = "wave_transition")
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = this.size.width
            val height = this.size.height
            val radius = width / 2f
            val strokeWidth = 8.dp.toPx()

            // Draw background ring (donut)
            drawCircle(
                color = backgroundColor,
                radius = radius - strokeWidth / 2f,
                style = Stroke(width = strokeWidth)
            )

            // Draw active outer ring
            val sweepAngle = (animatedProgress * 360f)
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
                size = Size(width - strokeWidth, height - strokeWidth),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Inside the donut, draw the fluid wave fill
            val clipRadius = radius - strokeWidth
            val clipPath = Path().apply {
                addOval(Rect(center = Offset(radius, radius), radius = clipRadius))
            }

            clipPath(path = clipPath) {
                val waveHeight = 6.dp.toPx() // amplitude of wave
                val waveFrequency = (2 * Math.PI / width).toFloat()
                // Y=height when progress is 0, Y=0 when progress is 1
                val waterLevelY = clipRadius * 2 * (1f - animatedProgress) + strokeWidth

                val wavePath = Path().apply {
                    moveTo(strokeWidth, height - strokeWidth)
                    for (x in strokeWidth.toInt().. (width - strokeWidth).toInt()) {
                        val y = waterLevelY + waveHeight * kotlin.math.sin(waveFrequency * x + wavePhase)
                        lineTo(x.toFloat(), y)
                    }
                    lineTo(width - strokeWidth, height - strokeWidth)
                    close()
                }

                drawPath(
                    path = wavePath,
                    color = color.copy(alpha = 0.35f) // Transparent fluid fill
                )
            }
        }

        Box(
            modifier = Modifier.padding(10.dp),
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

@Composable
fun AnimatedGlass(
    isFilled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fillProgress by animateFloatAsState(
        targetValue = if (isFilled) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "glassFill"
    )

    // Wave phase animation for a subtle flow/ripple look when filled
    val infiniteTransition = rememberInfiniteTransition(label = "glassWave")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glassPhase"
    )

    Box(
        modifier = modifier
            .aspectRatio(0.7f)
            .clip(RoundedCornerShape(8.dp))
            .background(Slate950)
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Draw glass container path (trapezoid shape for a glass look)
            val glassOutline = Path().apply {
                moveTo(width * 0.15f, height * 0.05f)
                lineTo(width * 0.85f, height * 0.05f)
                lineTo(width * 0.75f, height * 0.95f)
                lineTo(width * 0.25f, height * 0.95f)
                close()
            }

            // 2. Draw glass water fill with clipping path so water stays inside glass shape
            if (fillProgress > 0.01f) {
                clipPath(glassOutline) {
                    val waterHeight = (height * 0.90f) * fillProgress
                    val waterTopY = height * 0.95f - waterHeight

                    val waterPath = Path().apply {
                        moveTo(0f, height)
                        lineTo(width, height)
                        lineTo(width, waterTopY)

                        // Generate a flow wave on the surface
                        val waveAmplitude = 4.dp.toPx() * (1f - fillProgress).coerceAtLeast(0.2f)
                        val pointsCount = 20
                        for (i in pointsCount downTo 0) {
                            val x = (width / pointsCount) * i
                            val y = waterTopY + waveAmplitude * kotlin.math.sin((i.toFloat() / pointsCount.toFloat() * 2f * Math.PI.toFloat()) + phase)
                            lineTo(x, y)
                        }
                        close()
                    }

                    drawPath(
                        path = waterPath,
                        color = Teal400.copy(alpha = 0.85f)
                    )
                }
            }

            // 3. Draw glass outline itself (semi-transparent border)
            drawPath(
                path = glassOutline,
                color = Slate400,
                style = Stroke(width = 2.dp.toPx())
            )

            // 4. Draw lip of the glass (top rim ellipse)
            drawOval(
                color = Slate400,
                topLeft = Offset(width * 0.15f, height * 0.025f),
                size = Size(width * 0.70f, height * 0.05f),
                style = Stroke(width = 1.5.dp.toPx())
            )
        }
    }
}
