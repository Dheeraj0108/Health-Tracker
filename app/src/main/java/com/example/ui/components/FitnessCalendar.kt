package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExerciseLog
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FitnessCalendar(
    exerciseLogs: List<ExerciseLog>,
    dailyGoalMin: Double,
    modifier: Modifier = Modifier
) {
    var calendar by remember { mutableStateOf(Calendar.getInstance()) }
    val currentMonthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time)

    // Calculate completed days in current month
    val completedDays = remember(exerciseLogs, dailyGoalMin, calendar) {
        val completed = mutableSetOf<Int>()
        val cal = Calendar.getInstance()
        val logsByDay = exerciseLogs.groupBy {
            cal.timeInMillis = it.timestamp
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val day = cal.get(Calendar.DAY_OF_MONTH)
            Triple(year, month, day)
        }

        for ((triple, logs) in logsByDay) {
            val (year, month, day) = triple
            if (year == calendar.get(Calendar.YEAR) && month == calendar.get(Calendar.MONTH)) {
                val totalMins = logs.sumOf { it.durationMinutes }
                if (totalMins >= dailyGoalMin) {
                    completed.add(day)
                }
            }
        }
        completed
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Month selector header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    val newCal = Calendar.getInstance().apply {
                        time = calendar.time
                        add(Calendar.MONTH, -1)
                    }
                    calendar = newCal
                }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Prev Month", tint = Slate300)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = Teal400, modifier = Modifier.size(16.dp))
                    Text(
                        text = currentMonthName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Slate50
                    )
                }

                IconButton(onClick = {
                    val newCal = Calendar.getInstance().apply {
                        time = calendar.time
                        add(Calendar.MONTH, 1)
                    }
                    calendar = newCal
                }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Next Month", tint = Slate300)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Weekdays Header
            val daysOfWeek = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
            Row(modifier = Modifier.fillMaxWidth()) {
                daysOfWeek.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate400
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar Days Grid
            val tempCal = Calendar.getInstance().apply {
                time = calendar.time
                set(Calendar.DAY_OF_MONTH, 1)
            }
            val firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK) // 1 = Sunday, 2 = Monday...
            val daysInMonth = tempCal.getActualMaximum(Calendar.DAY_OF_MONTH)

            val totalCells = 42
            var dayCounter = 1

            for (row in 0 until 6) {
                if (dayCounter > daysInMonth) break
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (col in 1..7) {
                        val cellIndex = row * 7 + col
                        val isValidDay = cellIndex >= firstDayOfWeek && dayCounter <= daysInMonth
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isValidDay) {
                                val day = dayCounter
                                val isCompleted = completedDays.contains(day)
                                val isToday = isToday(day, calendar)

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            when {
                                                isCompleted -> Emerald500
                                                isToday -> Slate800
                                                else -> Color.Transparent
                                            }
                                        )
                                        .border(
                                            width = if (isToday) 2.dp else 0.dp,
                                            color = if (isToday) Teal400 else Color.Transparent,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day.toString(),
                                        fontSize = 13.sp,
                                        fontWeight = if (isToday || isCompleted) FontWeight.Bold else FontWeight.Normal,
                                        color = when {
                                            isCompleted -> Slate950
                                            isToday -> Teal400
                                            else -> Slate200
                                        }
                                    )
                                }
                                dayCounter++
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).background(Emerald500, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Exercise Goal Met (>= ${dailyGoalMin.toInt()}m)", fontSize = 11.sp, color = Slate300)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).border(1.5.dp, Teal400, CircleShape).background(Color.Transparent))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Today", fontSize = 11.sp, color = Slate300)
                }
            }
        }
    }
}

private fun isToday(day: Int, monthCal: Calendar): Boolean {
    val today = Calendar.getInstance()
    return today.get(Calendar.DAY_OF_MONTH) == day &&
            today.get(Calendar.MONTH) == monthCal.get(Calendar.MONTH) &&
            today.get(Calendar.YEAR) == monthCal.get(Calendar.YEAR)
}
