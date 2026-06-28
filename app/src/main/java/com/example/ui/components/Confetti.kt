package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val color: Color,
    val size: Float,
    val speedX: Float,
    val speedY: Float,
    val rotation: Float,
    val rotationSpeed: Float
)

@Composable
fun ConfettiOverlay(
    modifier: Modifier = Modifier,
    isTriggered: Boolean,
    onFinished: () -> Unit
) {
    if (!isTriggered) return

    val infiniteTransition = rememberInfiniteTransition(label = "Confetti")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Progress"
    )

    var particles by remember { mutableStateOf<List<ConfettiParticle>>(emptyList()) }

    LaunchedEffect(isTriggered) {
        if (isTriggered) {
            val colors = listOf(
                Color(0xFFFF3366), Color(0xFF33FF99), Color(0xFF3399FF),
                Color(0xFFFFCC00), Color(0xFF9933FF), Color(0xFFFF9933),
                Color(0xFF33FFFF)
            )
            particles = List(100) {
                ConfettiParticle(
                    x = Random.nextFloat(), // fractional width 0..1
                    y = Random.nextFloat() * -0.6f - 0.1f, // starting above the screen
                    color = colors.random(),
                    size = Random.nextFloat() * 16f + 10f,
                    speedX = Random.nextFloat() * 4f - 2f,
                    speedY = Random.nextFloat() * 10f + 6f,
                    rotation = Random.nextFloat() * 360f,
                    rotationSpeed = Random.nextFloat() * 12f - 6f
                )
            }
        }
    }

    LaunchedEffect(isTriggered) {
        kotlinx.coroutines.delay(3500)
        onFinished()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        particles = particles.map { particle ->
            var newX = particle.x + (particle.speedX / 150f)
            if (newX < 0f) newX = 1f
            if (newX > 1f) newX = 0f
            
            var newY = particle.y + (particle.speedY / 600f)
            if (newY > 1.1f) {
                newY = -0.1f
            }

            val newRot = (particle.rotation + particle.rotationSpeed) % 360f

            particle.copy(x = newX, y = newY, rotation = newRot)
        }

        particles.forEach { p ->
            drawContext.canvas.save()
            val drawX = p.x * width
            val drawY = p.y * height
            
            drawContext.transform.rotate(p.rotation, Offset(drawX, drawY))
            drawRect(
                color = p.color,
                topLeft = Offset(drawX - p.size / 2, drawY - p.size / 2),
                size = Size(p.size, p.size / 1.5f)
            )
            drawContext.canvas.restore()
        }
    }
}
