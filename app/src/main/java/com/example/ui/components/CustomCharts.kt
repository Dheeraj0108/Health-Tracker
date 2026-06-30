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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.border
import kotlin.math.roundToInt
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

            // 1. Draw modern sleek profile glass container (beaker/capsule rounded trapezoid)
            val containerPath = Path().apply {
                moveTo(width * 0.15f, height * 0.05f) // Top left
                lineTo(width * 0.85f, height * 0.05f) // Top right
                lineTo(width * 0.85f, height * 0.82f) // Down to bottom-right curve start
                quadraticTo(width * 0.85f, height * 0.95f, width * 0.65f, height * 0.95f) // Smooth curve to bottom-right corner
                lineTo(width * 0.35f, height * 0.95f) // Bottom flat base edge
                quadraticTo(width * 0.15f, height * 0.95f, width * 0.15f, height * 0.82f) // Smooth curve to bottom-left corner
                close()
            }

            // 2. Draw glass water fill with clipping path so water stays perfectly inside the modern profile shape
            if (fillProgress > 0.01f) {
                clipPath(containerPath) {
                    val waterHeight = (height * 0.90f) * fillProgress
                    val waterTopY = height * 0.95f - waterHeight

                    val waterPath = Path().apply {
                        moveTo(width * 0.15f, height * 0.95f) // Start at bottom left
                        lineTo(width * 0.85f, height * 0.95f) // Line to bottom right
                        lineTo(width * 0.85f, waterTopY)      // Up to water surface level on the right
                        
                        // y = A * sin(b * x + c) — continuous mathematical sine wave executing continuously
                        val amplitude = 5.dp.toPx() * if (fillProgress < 0.98f) 1.2f else 0.15f
                        val frequency = (2f * Math.PI.toFloat()) / (width * 0.7f) // Wave parameter b (exactly matching container width)
                        val pointsCount = 40
                        
                        for (i in pointsCount downTo 0) {
                            val px = width * 0.15f + (width * 0.70f / pointsCount) * i
                            val py = waterTopY + amplitude * kotlin.math.sin(frequency * px - phase)
                            lineTo(px, py)
                        }
                        close()
                    }

                    // Smooth blue filling gradient transitioning from bottom to top
                    val oceanGradient = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF38BDF8).copy(alpha = 0.90f), // Luminous light-sky blue at ripple surface
                            Color(0xFF0284C7).copy(alpha = 0.95f)  // Deep rich ocean blue at bottom base
                        ),
                        startY = waterTopY,
                        endY = height * 0.95f
                    )

                    drawPath(
                        path = waterPath,
                        brush = oceanGradient
                    )

                    // Draw floating ambient bubbles inside the filled portion
                    if (fillProgress > 0.15f) {
                        val bubbleColor = Color.White.copy(alpha = 0.4f)
                        val bubbleX1 = width * 0.35f
                        val bubbleY1 = waterTopY + (waterHeight * 0.4f) + 4.dp.toPx() * kotlin.math.cos(phase * 1.5f)
                        drawCircle(color = bubbleColor, radius = 3.dp.toPx(), center = Offset(bubbleX1, bubbleY1))

                        val bubbleX2 = width * 0.65f
                        val bubbleY2 = waterTopY + (waterHeight * 0.7f) + 3.dp.toPx() * kotlin.math.sin(phase * 1.2f)
                        drawCircle(color = bubbleColor, radius = 2.dp.toPx(), center = Offset(bubbleX2, bubbleY2))

                        val bubbleX3 = width * 0.48f
                        val bubbleY3 = waterTopY + (waterHeight * 0.25f) + 5.dp.toPx() * kotlin.math.cos(phase)
                        drawCircle(color = bubbleColor, radius = 4.dp.toPx(), center = Offset(bubbleX3, bubbleY3))
                    }
                }
            }

            // 3. Draw glass container outline itself (semi-transparent elegant boundary)
            drawPath(
                path = containerPath,
                color = if (isFilled) Color(0xFF38BDF8).copy(alpha = 0.85f) else Color(0xFF64748B).copy(alpha = 0.45f),
                style = Stroke(width = 2.dp.toPx())
            )

            // 4. Draw modern minimalist flat rim lip of the glass at the top opening
            drawLine(
                color = if (isFilled) Color(0xFF38BDF8).copy(alpha = 0.85f) else Color(0xFF64748B).copy(alpha = 0.45f),
                start = Offset(width * 0.15f, height * 0.05f),
                end = Offset(width * 0.85f, height * 0.05f),
                strokeWidth = 2.5.dp.toPx()
            )
        }
    }
}

@Composable
fun WeeklyTrendChart(
    data: List<DailySummaryPoint>,
    goalWater: Double,
    goalCaffeine: Double,
    goalExercise: Double,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(Slate900, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("No trend data available yet.", color = Slate400, fontSize = 14.sp)
        }
        return
    }

    // Animation progress for smooth path loading
    var animationTarget by remember { androidx.compose.runtime.mutableStateOf(0f) }
    androidx.compose.runtime.LaunchedEffect(data) {
        animationTarget = 0f
        kotlinx.coroutines.delay(50)
        animationTarget = 1f
    }
    val animProgress by animateFloatAsState(
        targetValue = animationTarget,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "trend_lines_anim"
    )

    // Selection index for tap-to-inspect interaction
    var selectedIndex by remember { androidx.compose.runtime.mutableStateOf(-1) }

    // Colors
    val waterColor = Color(0xFF2196F3)   // Fluid Blue
    val caffeineColor = Color(0xFF6F4E37) // Coffee Bean Brown
    val exerciseColor = Color(0xFFB7410E) // Rust Orange

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "7-Day Health Trends",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Interactive Trend Lines (Tap to Inspect)",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate400
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Beautiful interactive legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(label = "Water", color = waterColor, icon = "💧")
                LegendItem(label = "Caffeine", color = caffeineColor, icon = "☕")
                LegendItem(label = "Exercise", color = exerciseColor, icon = "🏃")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // If a day is selected, display a beautiful summary panel
            if (selectedIndex in data.indices) {
                val point = data[selectedIndex]
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .border(1.dp, Slate700, RoundedCornerShape(8.dp)),
                    colors = CardDefaults.cardColors(containerColor = Slate950),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Inspection: ${point.dayLabel}",
                                fontWeight = FontWeight.Bold,
                                color = Teal400,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "💧 ${point.waterMl.toInt()} ml / ${goalWater.toInt()}ml",
                                    color = Color.White,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "☕ ${point.caffeineMg.toInt()} mg / ${goalCaffeine.toInt()}mg",
                                    color = Color.White,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "🏃 ${point.exerciseMin.toInt()} m / ${goalExercise.toInt()}m",
                                    color = Color.White,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Text(
                            text = "Clear",
                            color = Slate400,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { selectedIndex = -1 }
                                .padding(4.dp)
                        )
                    }
                }
            }

            // The Chart Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Slate950, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 16.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(data) {
                            detectTapGestures { offset ->
                                val width = size.width
                                val stepX = width / 6f
                                val index = (offset.x / stepX).roundToInt().coerceIn(0, 6)
                                selectedIndex = index
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height

                    // Leave 20px at the bottom for day labels
                    val labelAreaHeight = 24.dp.toPx()
                    val chartHeight = height - labelAreaHeight

                    // 1. Draw Grid Lines
                    val gridLines = 3
                    val gridSpacing = chartHeight / gridLines
                    for (i in 0..gridLines) {
                        val y = i * gridSpacing
                        drawLine(
                            color = Slate800,
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )
                    }

                    // 2. Compute max values for normalisation/scaling
                    val maxWater = data.maxOf { it.waterMl }.coerceAtLeast(goalWater).coerceAtLeast(1.0)
                    val maxCaffeine = data.maxOf { it.caffeineMg }.coerceAtLeast(goalCaffeine).coerceAtLeast(1.0)
                    val maxExercise = data.maxOf { it.exerciseMin }.coerceAtLeast(goalExercise).coerceAtLeast(1.0)

                    val stepX = width / 6f

                    // Path objects
                    val waterPath = Path()
                    val waterAreaPath = Path()

                    val caffeinePath = Path()
                    val caffeineAreaPath = Path()

                    val exercisePath = Path()
                    val exerciseAreaPath = Path()

                    data.forEachIndexed { index, point ->
                        val x = index * stepX
                        
                        // Y positions scaled dynamically
                        val yWater = chartHeight - ((point.waterMl / maxWater) * chartHeight * animProgress).toFloat().coerceIn(0f, chartHeight)
                        val yCaffeine = chartHeight - ((point.caffeineMg / maxCaffeine) * chartHeight * animProgress).toFloat().coerceIn(0f, chartHeight)
                        val yExercise = chartHeight - ((point.exerciseMin / maxExercise) * chartHeight * animProgress).toFloat().coerceIn(0f, chartHeight)

                        // --- Water Paths ---
                        if (index == 0) {
                            waterPath.moveTo(x, yWater)
                            waterAreaPath.moveTo(x, chartHeight)
                            waterAreaPath.lineTo(x, yWater)

                            caffeinePath.moveTo(x, yCaffeine)
                            caffeineAreaPath.moveTo(x, chartHeight)
                            caffeineAreaPath.lineTo(x, yCaffeine)

                            exercisePath.moveTo(x, yExercise)
                            exerciseAreaPath.moveTo(x, chartHeight)
                            exerciseAreaPath.lineTo(x, yExercise)
                        } else {
                            waterPath.lineTo(x, yWater)
                            waterAreaPath.lineTo(x, yWater)

                            caffeinePath.lineTo(x, yCaffeine)
                            caffeineAreaPath.lineTo(x, yCaffeine)

                            exercisePath.lineTo(x, yExercise)
                            exerciseAreaPath.lineTo(x, yExercise)
                        }

                        if (index == data.size - 1) {
                            waterAreaPath.lineTo(x, chartHeight)
                            waterAreaPath.close()

                            caffeineAreaPath.lineTo(x, chartHeight)
                            caffeineAreaPath.close()

                            exerciseAreaPath.lineTo(x, chartHeight)
                            exerciseAreaPath.close()
                        }
                    }

                    // Draw Areas with gradients
                    if (data.isNotEmpty()) {
                        drawPath(
                            path = waterAreaPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(waterColor.copy(alpha = 0.15f), Color.Transparent)
                            )
                        )
                        drawPath(
                            path = caffeineAreaPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(caffeineColor.copy(alpha = 0.10f), Color.Transparent)
                            )
                        )
                        drawPath(
                            path = exerciseAreaPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(exerciseColor.copy(alpha = 0.15f), Color.Transparent)
                            )
                        )

                        // Draw lines
                        drawPath(
                            path = waterPath,
                            color = waterColor,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawPath(
                            path = caffeinePath,
                            color = caffeineColor,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawPath(
                            path = exercisePath,
                            color = exerciseColor,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }

                    // Draw dots on vertex points & vertical selection indicator
                    data.forEachIndexed { index, point ->
                        val x = index * stepX
                        val yWater = chartHeight - ((point.waterMl / maxWater) * chartHeight * animProgress).toFloat().coerceIn(0f, chartHeight)
                        val yCaffeine = chartHeight - ((point.caffeineMg / maxCaffeine) * chartHeight * animProgress).toFloat().coerceIn(0f, chartHeight)
                        val yExercise = chartHeight - ((point.exerciseMin / maxExercise) * chartHeight * animProgress).toFloat().coerceIn(0f, chartHeight)

                        // If index is selected, draw a vertical dash line
                        if (selectedIndex == index) {
                            drawLine(
                                color = Color.White.copy(alpha = 0.35f),
                                start = Offset(x, 0f),
                                end = Offset(x, chartHeight),
                                strokeWidth = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
                            )
                        }

                        // Draw individual dots on paths
                        drawCircle(
                            color = waterColor,
                            radius = if (selectedIndex == index) 5.dp.toPx() else 3.5.dp.toPx(),
                            center = Offset(x, yWater)
                        )
                        drawCircle(
                            color = caffeineColor,
                            radius = if (selectedIndex == index) 5.dp.toPx() else 3.5.dp.toPx(),
                            center = Offset(x, yCaffeine)
                        )
                        drawCircle(
                            color = exerciseColor,
                            radius = if (selectedIndex == index) 5.dp.toPx() else 3.5.dp.toPx(),
                            center = Offset(x, yExercise)
                        )

                        // Draw white rings around selected dots for extra pop
                        if (selectedIndex == index) {
                            drawCircle(
                                color = Color.White,
                                radius = 6.dp.toPx(),
                                center = Offset(x, yWater),
                                style = Stroke(width = 1.5.dp.toPx())
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 6.dp.toPx(),
                                center = Offset(x, yCaffeine),
                                style = Stroke(width = 1.5.dp.toPx())
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 6.dp.toPx(),
                                center = Offset(x, yExercise),
                                style = Stroke(width = 1.5.dp.toPx())
                            )
                        }

                        // Draw text labels for days at bottom
                        drawContext.canvas.nativeCanvas.apply {
                            val paint = android.graphics.Paint().apply {
                                color = if (selectedIndex == index) android.graphics.Color.parseColor("#FACC15") else android.graphics.Color.parseColor("#94A3B8")
                                textSize = 24f
                                typeface = android.graphics.Typeface.DEFAULT_BOLD
                                textAlign = android.graphics.Paint.Align.CENTER
                            }
                            drawText(
                                point.dayLabel,
                                x,
                                height - 2f,
                                paint
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LegendItem(label: String, color: Color, icon: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Text(
            text = "$icon $label",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = Slate200
        )
    }
}
