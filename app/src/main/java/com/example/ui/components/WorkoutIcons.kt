package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DumbbellIcon(modifier: Modifier = Modifier, tint: Color = Color(0xFFFFD700)) {
    Canvas(modifier = modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        // Draw center bar
        drawLine(
            color = tint,
            start = androidx.compose.ui.geometry.Offset(w * 0.22f, h * 0.5f),
            end = androidx.compose.ui.geometry.Offset(w * 0.78f, h * 0.5f),
            strokeWidth = 3.dp.toPx()
        )
        // Draw left plates
        drawRoundRect(
            color = tint,
            topLeft = androidx.compose.ui.geometry.Offset(w * 0.14f, h * 0.22f),
            size = androidx.compose.ui.geometry.Size(w * 0.08f, h * 0.56f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.5.dp.toPx())
        )
        drawRoundRect(
            color = tint,
            topLeft = androidx.compose.ui.geometry.Offset(w * 0.04f, h * 0.32f),
            size = androidx.compose.ui.geometry.Size(w * 0.08f, h * 0.36f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.5.dp.toPx())
        )
        // Draw right plates
        drawRoundRect(
            color = tint,
            topLeft = androidx.compose.ui.geometry.Offset(w * 0.78f, h * 0.22f),
            size = androidx.compose.ui.geometry.Size(w * 0.08f, h * 0.56f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.5.dp.toPx())
        )
        drawRoundRect(
            color = tint,
            topLeft = androidx.compose.ui.geometry.Offset(w * 0.88f, h * 0.32f),
            size = androidx.compose.ui.geometry.Size(w * 0.08f, h * 0.36f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.5.dp.toPx())
        )
    }
}

@Composable
fun BarbellIcon(modifier: Modifier = Modifier, tint: Color = Color(0xFFFFD700)) {
    Canvas(modifier = modifier.size(24.dp)) {
        val w = size.width
        val h = size.height
        // Draw long bar
        drawLine(
            color = tint,
            start = androidx.compose.ui.geometry.Offset(w * 0.04f, h * 0.5f),
            end = androidx.compose.ui.geometry.Offset(w * 0.96f, h * 0.5f),
            strokeWidth = 2.dp.toPx()
        )
        // Draw left outer plate
        drawCircle(
            color = tint,
            radius = h * 0.28f,
            center = androidx.compose.ui.geometry.Offset(w * 0.16f, h * 0.5f)
        )
        drawCircle(
            color = tint,
            radius = h * 0.18f,
            center = androidx.compose.ui.geometry.Offset(w * 0.24f, h * 0.5f)
        )
        // Draw right outer plate
        drawCircle(
            color = tint,
            radius = h * 0.28f,
            center = androidx.compose.ui.geometry.Offset(w * 0.84f, h * 0.5f)
        )
        drawCircle(
            color = tint,
            radius = h * 0.18f,
            center = androidx.compose.ui.geometry.Offset(w * 0.76f, h * 0.5f)
        )
    }
}
