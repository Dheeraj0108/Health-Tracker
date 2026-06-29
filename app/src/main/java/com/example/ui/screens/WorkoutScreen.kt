package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.WorkoutSession
import com.example.data.model.PersonalRecord
import com.example.ui.components.ConfettiOverlay
import com.example.ui.theme.*
import com.example.ui.viewmodel.HealthViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

// Structures for runtime active session
data class ActiveSet(
    val id: String = UUID.randomUUID().toString(),
    var type: String = "Normal", // "Normal", "Warm-up", "Drop Set", "Failure"
    var weight: String = "",
    var reps: String = "",
    var isCompleted: Boolean = false,
    val previous: String = "—"
)

data class ActiveExercise(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val isBodyweight: Boolean = false,
    var customNote: String? = null,
    val sets: MutableList<ActiveSet> = mutableStateListOf()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutSessionScreen(viewModel: HealthViewModel) {
    val context = LocalContext.current
    var activeSessionName by remember { mutableStateOf<String?>(null) }
    var activeSessionNotes by remember { mutableStateOf("") }
    val activeExercises = remember { mutableStateListOf<ActiveExercise>() }
    
    // Stopwatch & timer states
    var stopwatchSeconds by remember { mutableStateOf(0) }
    var isStopwatchRunning by remember { mutableStateOf(false) }
    
    // Rest timer states
    var restTimeRemaining by remember { mutableStateOf<Int?>(null) }
    var restTimeTotal by remember { mutableStateOf(90) } // Default 90 seconds
    
    // Dialog overlays
    var showAddExerciseSelector by remember { mutableStateOf(false) }
    var showCancelConfirmation by remember { mutableStateOf(false) }
    var showRestTimerSettings by remember { mutableStateOf(false) }

    // Personal Records States
    var activePrRecord by remember { mutableStateOf<PersonalRecord?>(null) }
    var showPrConfetti by remember { mutableStateOf(false) }

    // Minimized and temporal states
    var isWorkoutMinimized by remember { mutableStateOf(false) }
    var activeRestingSetIndex by remember { mutableStateOf<Pair<String, Int>?>(null) }
    var showRenameDialog by remember { mutableStateOf(false) }
    var showTimeAdjustDialog by remember { mutableStateOf(false) }
    var showNoteDialog by remember { mutableStateOf(false) }
    var showRestSettingsDialog by remember { mutableStateOf(false) }
    var showTopBarMenu by remember { mutableStateOf(false) }

    val completedWorkouts by viewModel.workoutSessions.collectAsStateWithLifecycle()
    val userRoutines by viewModel.routines.collectAsStateWithLifecycle()

    // Helper for temporal workout naming
    val getTemporalWorkoutName = {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            hour in 5..11 -> "Morning Workout"
            hour in 12..16 -> "Afternoon Workout"
            hour in 17..21 -> "Evening Workout"
            else -> "Night Workout"
        }
    }

    // Launch active timer
    LaunchedEffect(isStopwatchRunning) {
        if (isStopwatchRunning) {
            while (true) {
                delay(1000L)
                stopwatchSeconds++
            }
        }
    }

    // Launch rest countdown timer
    LaunchedEffect(restTimeRemaining) {
        if (restTimeRemaining != null) {
            while (restTimeRemaining!! > 0) {
                delay(1000L)
                restTimeRemaining = restTimeRemaining!! - 1
            }
            if (restTimeRemaining == 0) {
                Toast.makeText(context, "Rest over! Next set!", Toast.LENGTH_SHORT).show()
                restTimeRemaining = null
            }
        }
    }

    // PR achieved banner/dialog
    activePrRecord?.let { pr ->
        AlertDialog(
            onDismissRequest = { activePrRecord = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star Trophy",
                        tint = Amber400,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "NEW RECORD! 🏆",
                        fontWeight = FontWeight.Bold,
                        color = Amber400,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Congratulations! You just smashed a new personal record for:",
                        color = Slate300,
                        fontSize = 14.sp
                    )
                    Text(
                        text = pr.exerciseName,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        fontSize = 20.sp
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Surface(
                        color = Slate800,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (pr.metricType == "weight") "Max Weight Lifted" else "Max Reps Achieved",
                                color = Slate400,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (pr.metricType == "weight") "${pr.value} kg" else "${pr.value.toInt()} Reps",
                                color = Teal400,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black
                            )
                            if (pr.metricType == "weight" && pr.reps > 0) {
                                Text(
                                    text = "for ${pr.reps} reps",
                                    color = Slate400,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { activePrRecord = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Amber400)
                ) {
                    Text("Awesome!", color = Slate950, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Slate900,
            shape = RoundedCornerShape(20.dp)
        )
    }

    ConfettiOverlay(
        isTriggered = showPrConfetti,
        onFinished = { showPrConfetti = false }
    )

    if (activeSessionName != null && !isWorkoutMinimized) {
        // --- ACTIVE WORKOUT SESSION SHEET VIEW ---
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = activeSessionName ?: "Workout",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            
                            Box {
                                IconButton(onClick = { showTopBarMenu = true }) {
                                    Icon(Icons.Default.MoreVert, contentDescription = "More Options", tint = Slate300)
                                }
                                DropdownMenu(
                                    expanded = showTopBarMenu,
                                    onDismissRequest = { showTopBarMenu = false },
                                    modifier = Modifier.background(Slate900)
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Edit Workout Name", color = Color.White) },
                                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = Teal400) },
                                        onClick = { showRenameDialog = true; showTopBarMenu = false }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Adjust Session Time", color = Color.White) },
                                        leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null, tint = Teal400) },
                                        onClick = { showTimeAdjustDialog = true; showTopBarMenu = false }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Add Custom Text Note", color = Color.White) },
                                        leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null, tint = Teal400) },
                                        onClick = { showNoteDialog = true; showTopBarMenu = false }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Rest Timer Settings", color = Color.White) },
                                        leadingIcon = { Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = Teal400) },
                                        onClick = { showRestSettingsDialog = true; showTopBarMenu = false }
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = Teal400,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = formatStopwatch(stopwatchSeconds),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Teal400
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { isWorkoutMinimized = true }) {
                            Icon(Icons.Default.Minimize, contentDescription = "Minimize Context", tint = Slate300)
                        }
                    },
                    actions = {
                        Button(
                            onClick = {
                                // Save & Finish Workout
                                if (activeExercises.isEmpty()) {
                                    Toast.makeText(context, "Add at least one exercise to finish!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                
                                val serializedList = activeExercises.map { ex ->
                                    val setsStr = ex.sets.map { s -> "${s.type}:${s.weight}kg x ${s.reps}" }.joinToString(",")
                                    "${ex.name}|${ex.customNote ?: ""}|$setsStr"
                                }.joinToString(";")

                                val finalDuration = stopwatchSeconds / 60
                                viewModel.logWorkoutSession(
                                    name = activeSessionName ?: "Workout",
                                    durationMinutes = if (finalDuration > 0) finalDuration else 1,
                                    exercisesJson = serializedList,
                                    notes = activeSessionNotes.ifEmpty { null }
                                )

                                Toast.makeText(context, "Workout Completed! 🎉", Toast.LENGTH_LONG).show()
                                activeSessionName = null
                                isStopwatchRunning = false
                                stopwatchSeconds = 0
                                activeExercises.clear()
                                activeSessionNotes = ""
                                restTimeRemaining = null
                                isWorkoutMinimized = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Amber400),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("Finish", color = Slate950, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Slate950)
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Slate950)
                    .padding(padding)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Session Heading & Notes block
                    item {
                        Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                            Text(
                                text = activeSessionName ?: "Workout Session",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            OutlinedTextField(
                                value = activeSessionNotes,
                                onValueChange = { activeSessionNotes = it },
                                placeholder = { Text("Workout Notes (e.g. Felt strong on Bench today!)", color = Slate500, fontSize = 12.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 2,
                                textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 13.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Teal400,
                                    unfocusedBorderColor = Slate800,
                                    focusedContainerColor = Slate900,
                                    unfocusedContainerColor = Slate900
                                )
                            )
                        }
                    }

                    // Render Active Exercises
                    itemsIndexed(activeExercises) { index, exercise ->
                        ActiveExerciseCard(
                            exercise = exercise,
                            onAddSet = {
                                val prevSet = exercise.sets.lastOrNull()
                                exercise.sets.add(
                                    ActiveSet(
                                        weight = prevSet?.weight ?: "",
                                        reps = prevSet?.reps ?: "",
                                        type = "Normal"
                                    )
                                )
                            },
                            onRemoveSet = { setIndex ->
                                if (exercise.sets.size > setIndex) {
                                    exercise.sets.removeAt(setIndex)
                                }
                            },
                            onRemoveExercise = {
                                activeExercises.removeAt(index)
                            },
                            onSetCheckedChange = { setIdx, isChecked ->
                                val set = exercise.sets[setIdx]
                                set.isCompleted = isChecked
                                if (isChecked) {
                                    val weightVal = set.weight.toDoubleOrNull() ?: 0.0
                                    val repsVal = set.reps.toIntOrNull() ?: 0
                                    if (weightVal > 0.0 || repsVal > 0) {
                                        viewModel.checkAndLogPersonalRecord(
                                            exerciseName = exercise.name,
                                            weight = weightVal,
                                            reps = repsVal,
                                            onNewRecord = { pr ->
                                                activePrRecord = pr
                                                showPrConfetti = true
                                            }
                                        )
                                    }
                                    activeRestingSetIndex = Pair(exercise.name, setIdx)
                                    restTimeRemaining = restTimeTotal
                                } else {
                                    if (activeRestingSetIndex == Pair(exercise.name, setIdx)) {
                                        activeRestingSetIndex = null
                                        restTimeRemaining = null
                                    }
                                }
                            },
                            onSetTypeChange = { setIdx, newType ->
                                val set = exercise.sets[setIdx]
                                set.type = newType
                            },
                            activeRestingSetIndex = activeRestingSetIndex,
                            restTimeRemaining = restTimeRemaining,
                            restTimeTotal = restTimeTotal,
                            onSkipRest = {
                                restTimeRemaining = null
                                activeRestingSetIndex = null
                            }
                        )
                    }

                    // Footer Buttons
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                        ) {
                            Button(
                                onClick = { showAddExerciseSelector = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Slate900),
                                border = BorderStroke(1.dp, Teal400),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Teal400)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add Exercises", color = Teal400, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { showCancelConfirmation = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Slate950),
                                border = BorderStroke(1.dp, Crimson500.copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = Crimson500)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Cancel Workout", color = Crimson500, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Sliding Rest Timer overlay at bottom
                AnimatedVisibility(
                    visible = restTimeRemaining != null,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    restTimeRemaining?.let { sec ->
                        RestTimerOverlay(
                            secondsLeft = sec,
                            totalSeconds = restTimeTotal,
                            onAddSeconds = { restTimeRemaining = (restTimeRemaining ?: 0) + 30 },
                            onSubtractSeconds = { restTimeRemaining = maxOf(0, (restTimeRemaining ?: 0) - 30) },
                            onSkip = { restTimeRemaining = null }
                        )
                    }
                }
            }
        }

        // --- ACTIVE SESSION PARAMETER DIALOGS ---
        if (showRenameDialog) {
            var tempName by remember { mutableStateOf(activeSessionName ?: "") }
            AlertDialog(
                onDismissRequest = { showRenameDialog = false },
                title = { Text("Edit Workout Name", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Teal400, unfocusedBorderColor = Slate700, focusedTextColor = Color.White),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (tempName.isNotBlank()) {
                                activeSessionName = tempName.trim()
                            }
                            showRenameDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Teal400)
                    ) {
                        Text("Save", color = Slate950)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRenameDialog = false }) {
                        Text("Cancel", color = Slate400)
                    }
                },
                containerColor = Slate900
            )
        }

        if (showTimeAdjustDialog) {
            var tempMins by remember { mutableStateOf((stopwatchSeconds / 60).toString()) }
            AlertDialog(
                onDismissRequest = { showTimeAdjustDialog = false },
                title = { Text("Adjust Session Time", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Modify elapsed session duration in minutes:", color = Slate300, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = tempMins,
                            onValueChange = { tempMins = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Teal400, unfocusedBorderColor = Slate700, focusedTextColor = Color.White),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val mins = tempMins.toIntOrNull() ?: 0
                            stopwatchSeconds = maxOf(0, mins * 60)
                            showTimeAdjustDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Teal400)
                    ) {
                        Text("Apply", color = Slate950)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTimeAdjustDialog = false }) {
                        Text("Cancel", color = Slate400)
                    }
                },
                containerColor = Slate900
            )
        }

        if (showNoteDialog) {
            var tempNote by remember { mutableStateOf(activeSessionNotes) }
            AlertDialog(
                onDismissRequest = { showNoteDialog = false },
                title = { Text("Add Custom Text Note", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    OutlinedTextField(
                        value = tempNote,
                        onValueChange = { tempNote = it },
                        placeholder = { Text("Enter custom note text...") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Teal400, unfocusedBorderColor = Slate700, focusedTextColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            activeSessionNotes = tempNote.trim()
                            showNoteDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Teal400)
                    ) {
                        Text("Save", color = Slate950)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showNoteDialog = false }) {
                        Text("Cancel", color = Slate400)
                    }
                },
                containerColor = Slate900
            )
        }

        if (showRestSettingsDialog) {
            var tempRest by remember { mutableStateOf(restTimeTotal.toString()) }
            AlertDialog(
                onDismissRequest = { showRestSettingsDialog = false },
                title = { Text("Rest Timer Settings", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Configure rest countdown duration in seconds:", color = Slate300, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = tempRest,
                            onValueChange = { tempRest = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Teal400, unfocusedBorderColor = Slate700, focusedTextColor = Color.White),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val rSecs = tempRest.toIntOrNull() ?: 90
                            restTimeTotal = maxOf(5, rSecs)
                            showRestSettingsDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Teal400)
                    ) {
                        Text("Save", color = Slate950)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRestSettingsDialog = false }) {
                        Text("Cancel", color = Slate400)
                    }
                },
                containerColor = Slate900
            )
        }

        // --- CANCEL WORKOUT CONFIRMATION ---
        if (showCancelConfirmation) {
            AlertDialog(
                onDismissRequest = { showCancelConfirmation = false },
                title = { Text("Cancel Workout?", color = Color.White, fontWeight = FontWeight.Bold) },
                text = { Text("Are you sure you want to discard your progress? This session won't be saved.", color = Slate300) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showCancelConfirmation = false
                            activeSessionName = null
                            isStopwatchRunning = false
                            stopwatchSeconds = 0
                            activeExercises.clear()
                            activeSessionNotes = ""
                            restTimeRemaining = null
                        }
                    ) {
                        Text("Discard", color = Crimson500, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCancelConfirmation = false }) {
                        Text("Continue Workout", color = Teal400)
                    }
                },
                containerColor = Slate900
            )
        }

        // --- SEARCH & ADD EXERCISES DIALOG PANEL ---
        if (showAddExerciseSelector) {
            AddExercisesDialog(
                onDismiss = { showAddExerciseSelector = false },
                onExercisesSelected = { chosenList ->
                    chosenList.forEach { name ->
                        val defaultSets = mutableStateListOf(
                            ActiveSet(weight = "60", reps = "10", type = "Normal"),
                            ActiveSet(weight = "60", reps = "8", type = "Normal"),
                            ActiveSet(weight = "60", reps = "8", type = "Normal")
                        )
                        activeExercises.add(
                            ActiveExercise(
                                name = name,
                                sets = defaultSets
                            )
                        )
                    }
                    showAddExerciseSelector = false
                }
            )
        }

    } else {
        // --- WORKOUT LAUNCHER / TEMPLATES HOME VIEW ---
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Persistent Active Workout floating resume banner when minimized
            if (activeSessionName != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isWorkoutMinimized = false },
                        colors = CardDefaults.cardColors(containerColor = Teal400.copy(alpha = 0.12f)),
                        border = BorderStroke(1.dp, Teal400),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Teal400, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Active Workout: $activeSessionName", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Tap to resume workout in progress", color = Slate400, fontSize = 11.sp)
                                }
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = "Resume", tint = Teal400)
                        }
                    }
                }
            }

            // 1. Quick Start Section
            item {
                Text(
                    text = "Quick Start",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate300
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = {
                        activeSessionName = getTemporalWorkoutName()
                        isStopwatchRunning = true
                        stopwatchSeconds = 0
                        isWorkoutMinimized = false
                        activeExercises.clear()
                        // Seed basic empty exercise to start
                        activeExercises.add(
                            ActiveExercise(
                                name = "Barbell Bench Press",
                                sets = mutableStateListOf(
                                    ActiveSet(weight = "60", reps = "10", type = "Normal"),
                                    ActiveSet(weight = "60", reps = "8", type = "Normal")
                                )
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("start_empty_workout_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Teal400),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("START AN EMPTY WORKOUT", color = Slate950, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                }
            }

            // 2. Completed Workouts Registry
            item {
                Text(
                    text = "Completed Workouts Registry",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate300,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (completedWorkouts.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Slate900.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            Text("No workout sessions completed yet. Finish a workout to log details here!", color = Slate500, fontSize = 12.sp, textAlign = TextAlign.Center)
                        }
                    }
                }
            } else {
                items(completedWorkouts.take(5)) { session ->
                    var isExpanded by remember { mutableStateOf(false) }
                    val sdf = remember { SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault()) }
                    val dateStr = sdf.format(Date(session.timestamp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isExpanded = !isExpanded },
                        colors = CardDefaults.cardColors(containerColor = Slate900),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(session.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text(dateStr, color = Slate400, fontSize = 11.sp)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = Amber400, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${session.durationMinutes}m", color = Amber400, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = "Details toggle",
                                        tint = Slate400
                                    )
                                }
                            }

                            if (session.notes != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Note: ${session.notes}", color = Slate300, fontSize = 11.sp, style = TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic))
                            }

                            AnimatedVisibility(visible = isExpanded) {
                                Column(modifier = Modifier.padding(top = 10.dp)) {
                                    Divider(color = Slate800, thickness = 1.dp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Logged Exercises:", color = Teal400, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Parse and render exercise records
                                    val exercises = session.exercisesJson.split(";").filter { it.isNotBlank() }
                                    exercises.forEach { ex ->
                                        val parts = ex.split("|")
                                        if (parts.size >= 3) {
                                            val exName = parts[0]
                                            val sets = parts[2].split(",").filter { it.isNotBlank() }
                                            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                                Text(exName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                sets.forEach { setDetails ->
                                                    Text("  • $setDetails", color = Slate300, fontSize = 11.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. User Templates Block
            item {
                Text(
                    text = "User Templates Block",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate300,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (userRoutines.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Slate900.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            Text("No custom routines built yet. Create one under 'Routines' tab to use as templates here!", color = Slate500, fontSize = 12.sp, textAlign = TextAlign.Center)
                        }
                    }
                }
            } else {
                items(userRoutines) { routine ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                activeSessionName = routine.name
                                isStopwatchRunning = true
                                stopwatchSeconds = 0
                                activeExercises.clear()
                                val exercisesList = routine.exercisesListJson.split(";").filter { it.isNotBlank() }
                                exercisesList.forEach { exName ->
                                    activeExercises.add(
                                        ActiveExercise(
                                            name = exName.trim(),
                                            sets = mutableStateListOf(
                                                ActiveSet(weight = "60", reps = "10"),
                                                ActiveSet(weight = "60", reps = "8")
                                            )
                                        )
                                    )
                                }
                                isWorkoutMinimized = false
                                Toast.makeText(context, "Started ${routine.name} Template", Toast.LENGTH_SHORT).show()
                            },
                        colors = CardDefaults.cardColors(containerColor = Slate900),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(routine.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(routine.description, color = Slate400, fontSize = 11.sp, maxLines = 1)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Start Template", tint = Teal400)
                            }
                        }
                    }
                }
            }

            // 4. Suggested Templates Block
            item {
                Text(
                    text = "Suggested Templates Block",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Slate300,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Beautiful Static Preloaded Templates from Image 3
            item {
                TemplateCategoryGroup(
                    categoryName = "Beginner Full Body (2 templates)",
                    templates = listOf(
                        TemplateItem("Full body A", listOf("Barbell Squats", "Barbell Bench Press", "Barbell Row", "Dumbbell Curls")),
                        TemplateItem("Full body B", listOf("Deadlift", "Overhead Press", "Leg Press", "Triceps Pushdown"))
                    ),
                    onStartTemplate = { name, exercisesList ->
                        activeSessionName = name
                        isStopwatchRunning = true
                        stopwatchSeconds = 0
                        isWorkoutMinimized = false
                        activeExercises.clear()
                        exercisesList.forEach { exName ->
                            activeExercises.add(
                                ActiveExercise(
                                    name = exName,
                                    sets = mutableStateListOf(
                                        ActiveSet(weight = "50", reps = "10"),
                                        ActiveSet(weight = "50", reps = "10"),
                                        ActiveSet(weight = "50", reps = "8")
                                    )
                                )
                            )
                        }
                    }
                )
            }

            item {
                TemplateCategoryGroup(
                    categoryName = "Strength Program (5x5)",
                    templates = listOf(
                        TemplateItem("Strong 5x5 - Workout A", listOf("Barbell Squats", "Barbell Bench Press", "Barbell Row")),
                        TemplateItem("Strong 5x5 - Workout B", listOf("Barbell Squats", "Overhead Press", "Deadlift"))
                    ),
                    onStartTemplate = { name, exercisesList ->
                        activeSessionName = name
                        isStopwatchRunning = true
                        stopwatchSeconds = 0
                        isWorkoutMinimized = false
                        activeExercises.clear()
                        exercisesList.forEach { exName ->
                            activeExercises.add(
                                ActiveExercise(
                                    name = exName,
                                    sets = mutableStateListOf(
                                        ActiveSet(weight = "80", reps = "5"),
                                        ActiveSet(weight = "80", reps = "5"),
                                        ActiveSet(weight = "80", reps = "5"),
                                        ActiveSet(weight = "80", reps = "5"),
                                        ActiveSet(weight = "80", reps = "5")
                                    )
                                )
                            )
                        }
                    }
                )
            }

            item {
                TemplateCategoryGroup(
                    categoryName = "Intermediate Push/Pull/Legs",
                    templates = listOf(
                        TemplateItem("PPL - Push Focus", listOf("Incline Dumbbell Press", "Overhead Press", "Tricep Dips", "Lateral Raises")),
                        TemplateItem("PPL - Pull Focus", listOf("Lat Pulldown", "Seated Cable Row", "Bicep Curl", "Face Pulls"))
                    ),
                    onStartTemplate = { name, exercisesList ->
                        activeSessionName = name
                        isStopwatchRunning = true
                        stopwatchSeconds = 0
                        isWorkoutMinimized = false
                        activeExercises.clear()
                        exercisesList.forEach { exName ->
                            activeExercises.add(
                                ActiveExercise(
                                    name = exName,
                                    sets = mutableStateListOf(
                                        ActiveSet(weight = "65", reps = "8"),
                                        ActiveSet(weight = "65", reps = "8"),
                                        ActiveSet(weight = "65", reps = "8")
                                    )
                                )
                            )
                        }
                    }
                )
            }

            // Margin bottom
            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

// Data holder for Templates UI mapping
data class TemplateItem(val name: String, val exercises: List<String>)

@Composable
fun TemplateCategoryGroup(
    categoryName: String,
    templates: List<TemplateItem>,
    onStartTemplate: (String, List<String>) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Slate900)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = categoryName,
                color = Slate400,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                templates.forEach { temp ->
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onStartTemplate(temp.name, temp.exercises) },
                        colors = CardDefaults.cardColors(containerColor = Slate800)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = temp.name,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = temp.exercises.joinToString(", "),
                                color = Slate400,
                                fontSize = 11.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- ACTIVE EXERCISE CARD COMPOSABLE ---
@Composable
fun ActiveExerciseCard(
    exercise: ActiveExercise,
    onAddSet: () -> Unit,
    onRemoveSet: (Int) -> Unit,
    onRemoveExercise: () -> Unit,
    onSetCheckedChange: (Int, Boolean) -> Unit,
    onSetTypeChange: (Int, String) -> Unit,
    activeRestingSetIndex: Pair<String, Int>?,
    restTimeRemaining: Int?,
    restTimeTotal: Int,
    onSkipRest: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var exerciseNoteText by remember { mutableStateOf("") }
    var noteExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Slate900)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Exercise Header line
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = Slate400)
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier.background(Slate800)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Add Exercise Note", color = Color.White) },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = Teal400) },
                            onClick = {
                                noteExpanded = true
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Add Warm-up Set", color = Color.White) },
                            leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = Amber400) },
                            onClick = {
                                exercise.sets.add(0, ActiveSet(type = "Warm-up", weight = "20", reps = "12"))
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Remove Exercise", color = Color.White) },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Crimson500) },
                            onClick = {
                                onRemoveExercise()
                                showMenu = false
                            }
                        )
                    }
                }
            }

            // Exercise Note Field
            if (noteExpanded || !exercise.customNote.isNullOrEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Amber400, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedTextField(
                        value = exerciseNoteText,
                        onValueChange = { 
                            exerciseNoteText = it
                            exercise.customNote = it
                        },
                        placeholder = { Text("Add a note (e.g. Left knee doesn't hurt today)", color = Slate500, fontSize = 11.sp) },
                        textStyle = LocalTextStyle.current.copy(color = Color.White, fontSize = 12.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Teal400,
                            unfocusedBorderColor = Slate800,
                            focusedContainerColor = Slate950,
                            unfocusedContainerColor = Slate950
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { 
                        noteExpanded = false
                        exercise.customNote = null
                        exerciseNoteText = ""
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear Note", tint = Slate500, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // Smart dynamic advice based on exercise name!
            val adviceText = when {
                exercise.name.contains("Bench", ignoreCase = true) -> "Rest bar lightly on lower chest. Do not bounce off ribs."
                exercise.name.contains("Squat", ignoreCase = true) -> "Keep your chest high and drive upward through your heels."
                exercise.name.contains("Deadlift", ignoreCase = true) -> "Engage lats and keep back flat. Avoid rounding lower spine!"
                exercise.name.contains("Press", ignoreCase = true) -> "Keep core stabilized. Lock arms at peak contraction."
                exercise.name.contains("Curl", ignoreCase = true) -> "Elbows pinned to your ribs, avoid swinging body momentum."
                else -> "Ensure controlled form and steady breathing cycles."
            }

            // Lightbulb hint block (Yellow/Amber tip card)
            Card(
                colors = CardDefaults.cardColors(containerColor = Amber400.copy(alpha = 0.08f)),
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
            ) {
                Row(modifier = Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Amber400, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(adviceText, color = Color(0xFFFCD34D), fontSize = 11.sp, lineHeight = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sets Table Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Set", color = Slate400, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(48.dp), textAlign = TextAlign.Center)
                Text("Previous", color = Slate400, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.1f), textAlign = TextAlign.Center)
                Text("kg", color = Slate400, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text("Reps", color = Slate400, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.width(44.dp)) // space for tick button
            }

            // Sets Rows
            exercise.sets.forEachIndexed { sIdx, set ->
                val isRowChecked = set.isCompleted
                val rowBgColor = if (isRowChecked) Color(0xFF064E3B).copy(alpha = 0.25f) else Color.Transparent
                val borderModifier = if (isRowChecked) Modifier.border(1.dp, Emerald400.copy(alpha = 0.5f), RoundedCornerShape(8.dp)) else Modifier

                var weightState by remember { mutableStateOf(set.weight) }
                var repsState by remember { mutableStateOf(set.reps) }
                var showTypeSelector by remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(rowBgColor, RoundedCornerShape(8.dp))
                        .then(borderModifier)
                        .padding(horizontal = 4.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Set designation index/badge
                    Box(modifier = Modifier.width(48.dp), contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    when (set.type) {
                                        "Warm-up" -> Amber400.copy(alpha = 0.2f)
                                        "Drop Set" -> Indigo400.copy(alpha = 0.2f)
                                        "Failure" -> Crimson500.copy(alpha = 0.2f)
                                        else -> Slate800
                                    }
                                )
                                .clickable { showTypeSelector = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (set.type) {
                                    "Warm-up" -> "W"
                                    "Drop Set" -> "D"
                                    "Failure" -> "F"
                                    else -> "${sIdx + 1}"
                                },
                                fontWeight = FontWeight.Bold,
                                color = when (set.type) {
                                    "Warm-up" -> Amber400
                                    "Drop Set" -> Indigo400
                                    "Failure" -> Crimson500
                                    else -> Color.White
                                },
                                fontSize = 12.sp
                            )
                        }

                        // Set type picker dropdown
                        DropdownMenu(
                            expanded = showTypeSelector,
                            onDismissRequest = { showTypeSelector = false },
                            modifier = Modifier.background(Slate800)
                        ) {
                            DropdownMenuItem(text = { Text("Normal Set", color = Color.White) }, onClick = { onSetTypeChange(sIdx, "Normal"); showTypeSelector = false })
                            DropdownMenuItem(text = { Text("Warm-up (W)", color = Amber400) }, onClick = { onSetTypeChange(sIdx, "Warm-up"); showTypeSelector = false })
                            DropdownMenuItem(text = { Text("Drop Set (D)", color = Indigo400) }, onClick = { onSetTypeChange(sIdx, "Drop Set"); showTypeSelector = false })
                            DropdownMenuItem(text = { Text("Failure (F)", color = Crimson500) }, onClick = { onSetTypeChange(sIdx, "Failure"); showTypeSelector = false })
                            HorizontalDivider(color = Slate700)
                            DropdownMenuItem(text = { Text("Delete Set", color = Crimson500) }, onClick = { onRemoveSet(sIdx); showTypeSelector = false })
                        }
                    }

                    // Previous metric string
                    Text(
                        text = set.previous,
                        color = Slate400,
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1.1f),
                        textAlign = TextAlign.Center
                    )

                    // Weight input field
                    Box(modifier = Modifier.weight(1f).padding(horizontal = 4.dp)) {
                        BasicTextField(
                            value = weightState,
                            onValueChange = { 
                                weightState = it
                                set.weight = it
                            },
                            textStyle = TextStyle(
                                color = if (isRowChecked) Color(0xFF6EE7B7) else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Slate950, RoundedCornerShape(6.dp))
                                .border(1.dp, if (isRowChecked) Emerald400.copy(alpha = 0.5f) else Slate800, RoundedCornerShape(6.dp))
                                .padding(vertical = 6.dp)
                        )
                    }

                    // Reps input field
                    Box(modifier = Modifier.weight(1f).padding(horizontal = 4.dp)) {
                        BasicTextField(
                            value = repsState,
                            onValueChange = { 
                                repsState = it
                                set.reps = it
                            },
                            textStyle = TextStyle(
                                color = if (isRowChecked) Color(0xFF6EE7B7) else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Slate950, RoundedCornerShape(6.dp))
                                .border(1.dp, if (isRowChecked) Emerald400.copy(alpha = 0.5f) else Slate800, RoundedCornerShape(6.dp))
                                .padding(vertical = 6.dp)
                        )
                    }

                    // Checkbox ticker button
                    IconButton(
                        onClick = { onSetCheckedChange(sIdx, !set.isCompleted) },
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(32.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isRowChecked) Emerald400 else Slate800)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Complete Set",
                            tint = if (isRowChecked) Slate950 else Slate400,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                if (activeRestingSetIndex == Pair(exercise.name, sIdx) && restTimeRemaining != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 12.dp)
                            .background(Teal500.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .border(1.dp, Teal400.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = Teal400, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Resting: ${restTimeRemaining}s / ${restTimeTotal}s",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        TextButton(
                            onClick = onSkipRest,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Skip Timer", color = Crimson400, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Add Set button
            Button(
                onClick = onAddSet,
                modifier = Modifier.fillMaxWidth().height(36.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Slate300, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Set", color = Slate300, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

// --- REST TIMER HUD COMPOSABLE ---
@Composable
fun RestTimerOverlay(
    secondsLeft: Int,
    totalSeconds: Int,
    onAddSeconds: () -> Unit,
    onSubtractSeconds: () -> Unit,
    onSkip: () -> Unit
) {
    val progress = secondsLeft.toFloat() / totalSeconds.toFloat()
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        border = BorderStroke(1.dp, Teal400),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = Teal400, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "REST TIMER: ${secondsLeft / 60}:${String.format(Locale.US, "%02d", secondsLeft % 60)}",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onSubtractSeconds, contentPadding = PaddingValues(0.dp), modifier = Modifier.height(28.dp)) {
                        Text("-30s", color = Slate400, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    TextButton(onClick = onAddSeconds, contentPadding = PaddingValues(0.dp), modifier = Modifier.height(28.dp)) {
                        Text("+30s", color = Teal400, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Button(
                        onClick = onSkip,
                        colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("Skip", color = Crimson400, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Blue horizontal progress line
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = Teal400,
                trackColor = Slate800
            )
        }
    }
}

// --- POPUP DIALOG PANEL FOR ADDING EXERCISES ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExercisesDialog(
    onDismiss: () -> Unit,
    onExercisesSelected: (List<String>) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val selectedItems = remember { mutableStateListOf<String>() }
    var selectedFilter by remember { mutableStateOf("All") }
    var isSortAscending by remember { mutableStateOf(true) }
    var showCustomExercisePrompt by remember { mutableStateOf(false) }
    var showArchiveRegistryDialog by remember { mutableStateOf(false) }
    var showOverflowMenu by remember { mutableStateOf(false) }
    val customCreatedExercises = remember { mutableStateListOf<String>() }

    val exerciseCategories = mapOf(
        "Chest" to listOf("Barbell Bench Press", "Incline Dumbbell Press", "Dumbbell Flyes", "Chest Fly (Pec Deck)", "Push-ups", "Cable Crossover", "Decline Bench Press", "Chest Dips"),
        "Back" to listOf("Barbell Row", "Lat Pulldown", "Seated Cable Row", "Pull-ups", "Chin-ups", "Lat Pullover", "Hyperextensions", "Face Pulls"),
        "Legs" to listOf("Barbell Squats", "Leg Press", "Lying Leg Curls", "Leg Extensions", "Romanian Deadlift", "Calf Raises"),
        "Arms" to listOf("Overhead Press", "Overhead Dumbbell Press", "Lateral Raises", "Triceps Rope Pushdown", "Barbell Bicep Curl", "Hammer Curls", "Front Raises", "Reverse Pec Deck", "Bicep Curl (Dumbbell)", "Concentration Curl", "Preacher Curl", "Tricep Overhead Extension", "Skull Crushers", "Close Grip Bench Press"),
        "Core" to listOf("Abs Crunches", "Plank", "Leg Raises", "Russian Twists"),
        "Cardio" to listOf("Treadmill Running", "Elliptical Cardio")
    )

    val allExercisesCombined = remember(customCreatedExercises) {
        val base = listOf(
            "Barbell Squats", "Barbell Bench Press", "Barbell Row", "Overhead Press", "Deadlift",
            "Incline Dumbbell Press", "Overhead Dumbbell Press", "Lateral Raises", "Triceps Rope Pushdown",
            "Lat Pulldown", "Seated Cable Row", "Barbell Bicep Curl", "Hammer Curls", "Dumbbell Flyes",
            "Chest Fly (Pec Deck)", "Push-ups", "Cable Crossover", "Decline Bench Press", "Chest Dips",
            "Pull-ups", "Chin-ups", "Lat Pullover", "Hyperextensions", "Face Pulls", "Front Raises",
            "Reverse Pec Deck", "Bicep Curl (Dumbbell)", "Concentration Curl", "Preacher Curl",
            "Tricep Overhead Extension", "Skull Crushers", "Close Grip Bench Press", "Leg Press",
            "Lying Leg Curls", "Leg Extensions", "Romanian Deadlift", "Calf Raises", "Abs Crunches",
            "Plank", "Leg Raises", "Russian Twists", "Treadmill Running", "Elliptical Cardio"
        )
        (base + customCreatedExercises).distinct()
    }

    val filteredList = remember(searchQuery, selectedFilter, isSortAscending, allExercisesCombined) {
        allExercisesCombined.filter { name ->
            val matchesSearch = name.contains(searchQuery, ignoreCase = true)
            val matchesFilter = if (selectedFilter == "All") true else {
                exerciseCategories[selectedFilter]?.contains(name) == true
            }
            matchesSearch && matchesFilter
        }.sortedWith(if (isSortAscending) compareBy { it } else compareByDescending { it })
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Add Exercises", color = Color.White, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { isSortAscending = !isSortAscending }) {
                        Icon(
                            imageVector = if (isSortAscending) Icons.Default.SortByAlpha else Icons.Default.Sort,
                            contentDescription = "Sort exercises",
                            tint = Teal400
                        )
                    }
                    Box {
                        IconButton(onClick = { showOverflowMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More Actions", tint = Slate400)
                        }
                        DropdownMenu(
                            expanded = showOverflowMenu,
                            onDismissRequest = { showOverflowMenu = false },
                            modifier = Modifier.background(Slate900)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Create Custom Exercise", color = Color.White) },
                                leadingIcon = { Icon(Icons.Default.Add, contentDescription = null, tint = Teal400) },
                                onClick = { showCustomExercisePrompt = true; showOverflowMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Browse Archived Registry", color = Color.White) },
                                leadingIcon = { Icon(Icons.Default.Archive, contentDescription = null, tint = Teal400) },
                                onClick = { showArchiveRegistryDialog = true; showOverflowMenu = false }
                            )
                        }
                    }
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().height(420.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search exercises...", color = Slate500) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Slate400) },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Teal400,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Color.White
                    )
                )

                // Filter scrollable Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedFilter == "All",
                            onClick = { selectedFilter = "All" },
                            label = { Text("All") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Teal400,
                                selectedLabelColor = Slate950,
                                containerColor = Slate800,
                                labelColor = Slate300
                            )
                        )
                    }
                    exerciseCategories.keys.forEach { cat ->
                        item {
                            FilterChip(
                                selected = selectedFilter == cat,
                                onClick = { selectedFilter = cat },
                                label = { Text(cat) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Teal400,
                                    selectedLabelColor = Slate950,
                                    containerColor = Slate800,
                                    labelColor = Slate300
                                )
                            )
                        }
                    }
                }

                if (filteredList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        Text("No matching exercises found.", color = Slate500, fontSize = 14.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredList) { name ->
                            val isSelected = selectedItems.contains(name)
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isSelected) selectedItems.remove(name) else selectedItems.add(name)
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Teal400.copy(alpha = 0.12f) else Slate800
                                ),
                                border = BorderStroke(1.dp, if (isSelected) Teal400 else Color.Transparent)
                            ) {
                                Box(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Text(name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        val catLabel = exerciseCategories.entries.find { it.value.contains(name) }?.key ?: "Custom"
                                        Text(catLabel, color = Slate400, fontSize = 11.sp, modifier = Modifier.padding(top = 2.dp))
                                    }
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Selected",
                                            tint = Teal400,
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .size(22.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onExercisesSelected(selectedItems) },
                colors = ButtonDefaults.buttonColors(containerColor = Teal400)
            ) {
                Text("Add Selected (${selectedItems.size})", color = Slate950, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Slate400)
            }
        },
        containerColor = Slate900
    )

    if (showCustomExercisePrompt) {
        var customName by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showCustomExercisePrompt = false },
            title = { Text("Create Custom Exercise", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter the name of your custom exercise:", color = Slate300, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customName,
                        onValueChange = { customName = it },
                        placeholder = { Text("e.g. Incline Cable Flyes") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Teal400,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customName.isNotBlank()) {
                            customCreatedExercises.add(customName.trim())
                            selectedItems.add(customName.trim())
                            showCustomExercisePrompt = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Teal400)
                ) {
                    Text("Create", color = Slate950, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomExercisePrompt = false }) {
                    Text("Cancel", color = Slate400)
                }
            },
            containerColor = Slate900
        )
    }

    if (showArchiveRegistryDialog) {
        AlertDialog(
            onDismissRequest = { showArchiveRegistryDialog = false },
            title = { Text("Archived Exercises Registry", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("The following exercises are currently archived to keep your list clean:", color = Slate300, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    listOf("Machine Hack Squat (Archived)", "Seated Calf Press (Archived)", "Decline Cable Flyes (Archived)").forEach { archivedName ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Archive, contentDescription = null, tint = Slate500, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(archivedName, color = Slate400, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showArchiveRegistryDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = Teal400)) {
                    Text("Close", color = Slate950)
                }
            },
            containerColor = Slate900
        )
    }
}

// Stopwatch formatting logic
fun formatStopwatch(totalSecs: Int): String {
    val hrs = totalSecs / 3600
    val mins = (totalSecs % 3600) / 60
    val secs = totalSecs % 60
    return if (hrs > 0) {
        String.format(Locale.US, "%02d:%02d:%02d", hrs, mins, secs)
    } else {
        String.format(Locale.US, "%02d:%02d", mins, secs)
    }
}


// --- GORGEOUS CUSTOM CANVAS CHARTS COMPOSABLE (IMAGE 4 STYLE) ---
@Composable
fun WorkoutProgressCharts(viewModel: HealthViewModel) {
    val workoutSessions by viewModel.workoutSessions.collectAsStateWithLifecycle()
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Render Tony's Profile Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Slate900)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Purple letter avatar
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF8B5CF6), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("T", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text("Tony", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    Text("Streak Tracker • 701 Completed Workouts", color = Slate400, fontSize = 11.sp)
                }
            }
        }

        // --- BAR CHART: WORKOUTS PER WEEK ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Slate900)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Workouts per week",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "Historical consistency in last 4 weeks",
                    color = Slate400,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Render Canvas
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    val width = size.width
                    val height = size.height
                    
                    // Draw gridlines
                    val gridPaint = Paint().apply {
                        color = Slate800
                        strokeWidth = 1f
                    }
                    drawLine(Slate800, Offset(0f, height * 0.33f), Offset(width, height * 0.33f))
                    drawLine(Slate800, Offset(0f, height * 0.66f), Offset(width, height * 0.66f))
                    drawLine(Slate800, Offset(0f, height - 2.dp.toPx()), Offset(width, height - 2.dp.toPx()))

                    // Weeks mock or real values: say [4, 5, 3, 4] workouts completed
                    val dataPoints = listOf(4, 5, 3, 4)
                    val weekLabels = listOf("Week 1", "Week 2", "Week 3", "Week 4")
                    val maxVal = 6f
                    
                    val barWidth = 36.dp.toPx()
                    val gap = (width - (barWidth * dataPoints.size)) / (dataPoints.size + 1)

                    dataPoints.forEachIndexed { idx, value ->
                        val x = gap + idx * (barWidth + gap)
                        val pct = value.toFloat() / maxVal
                        val barHeight = height * pct
                        val y = height - barHeight

                        // Drawing rounded bar with purple-indigo gradient
                        val rectBrush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF8B5CF6), Color(0xFF6366F1))
                        )
                        
                        drawRoundRect(
                            brush = rectBrush,
                            topLeft = Offset(x, y),
                            size = Size(barWidth, barHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
                        )

                        // Value labels above bars
                        drawContext.canvas.nativeCanvas.apply {
                            val paint = android.graphics.Paint().apply {
                                color = android.graphics.Color.WHITE
                                textSize = 11.sp.toPx()
                                typeface = android.graphics.Typeface.DEFAULT_BOLD
                                textAlign = android.graphics.Paint.Align.CENTER
                            }
                            drawText("$value", x + barWidth/2, y - 6.dp.toPx(), paint)
                            
                            // X labels below bars
                            val labelPaint = android.graphics.Paint().apply {
                                color = android.graphics.Color.parseColor("#94A3B8") // Slate400
                                textSize = 10.sp.toPx()
                                textAlign = android.graphics.Paint.Align.CENTER
                            }
                            drawText(weekLabels[idx], x + barWidth/2, height - 4.dp.toPx(), labelPaint)
                        }
                    }
                }
            }
        }

        // --- LINE CHART: BENCH PRESS BEST SET 1RM ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Slate900)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Bench Press (Barbell) Best Set",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "Estimated 1-Rep Max (1RM) Progress",
                    color = Slate400,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Line Chart Canvas
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    val width = size.width
                    val height = size.height

                    // Grid lines
                    drawLine(Slate800, Offset(0f, height * 0.25f), Offset(width, height * 0.25f))
                    drawLine(Slate800, Offset(0f, height * 0.5f), Offset(width, height * 0.5f))
                    drawLine(Slate800, Offset(0f, height * 0.75f), Offset(width, height * 0.75f))
                    drawLine(Slate800, Offset(0f, height), Offset(width, height))

                    // Simulated 1RM values over time: e.g. [70kg, 72.5kg, 75kg, 80kg, 82.5kg]
                    val points = listOf(70f, 72.5f, 75f, 80f, 82.5f)
                    val dateLabels = listOf("Jan", "Feb", "Mar", "Apr", "May")
                    
                    val minWeight = 60f
                    val maxWeight = 90f
                    val weightRange = maxWeight - minWeight

                    val pointsCount = points.size
                    val spacing = width / (pointsCount - 1)

                    val path = Path()
                    val fillPath = Path()

                    points.forEachIndexed { idx, valW ->
                        val x = idx * spacing
                        val ratio = (valW - minWeight) / weightRange
                        val y = height - (height * ratio)

                        if (idx == 0) {
                            path.moveTo(x, y)
                            fillPath.moveTo(x, height)
                            fillPath.lineTo(x, y)
                        } else {
                            path.lineTo(x, y)
                            fillPath.lineTo(x, y)
                        }

                        if (idx == pointsCount - 1) {
                            fillPath.lineTo(x, height)
                            fillPath.close()
                        }
                    }

                    // 1. Draw glowing gradient fill under the line
                    val areaBrush = Brush.verticalGradient(
                        colors = listOf(Teal400.copy(alpha = 0.22f), Color.Transparent)
                    )
                    drawPath(path = fillPath, brush = areaBrush)

                    // 2. Draw outline curve
                    drawPath(
                        path = path,
                        color = Teal400,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // 3. Draw dots and data labels
                    points.forEachIndexed { idx, valW ->
                        val x = idx * spacing
                        val ratio = (valW - minWeight) / weightRange
                        val y = height - (height * ratio)

                        // Outer ring glow
                        drawCircle(
                            color = Teal400.copy(alpha = 0.35f),
                            radius = 6.dp.toPx(),
                            center = Offset(x, y)
                        )
                        // Inner dot
                        drawCircle(
                            color = Color.White,
                            radius = 3.dp.toPx(),
                            center = Offset(x, y)
                        )

                        // Text weights & month labels
                        drawContext.canvas.nativeCanvas.apply {
                            val textPaint = android.graphics.Paint().apply {
                                color = android.graphics.Color.WHITE
                                textSize = 9.sp.toPx()
                                typeface = android.graphics.Typeface.DEFAULT_BOLD
                                textAlign = android.graphics.Paint.Align.CENTER
                            }
                            drawText("${valW.toInt()}kg", x, y - 8.dp.toPx(), textPaint)

                            val lblPaint = android.graphics.Paint().apply {
                                color = android.graphics.Color.parseColor("#94A3B8") // Slate400
                                textSize = 9.sp.toPx()
                                textAlign = android.graphics.Paint.Align.CENTER
                            }
                            // Avoid drawing label too close to edges
                            val labelX = when(idx) {
                                0 -> x + 8.dp.toPx()
                                pointsCount - 1 -> x - 8.dp.toPx()
                                else -> x
                            }
                            drawText(dateLabels[idx], labelX, height - 4.dp.toPx(), lblPaint)
                        }
                    }
                }
            }
        }

        // --- PERSONAL RECORDS LIST CARD ---
        val personalRecords by viewModel.personalRecords.collectAsStateWithLifecycle()
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Slate900)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🏆 Personal Records (PRs)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "Your all-time high achievements",
                            color = Slate400,
                            fontSize = 11.sp
                        )
                    }
                    if (personalRecords.isNotEmpty()) {
                        Text(
                            text = "${personalRecords.size} Records",
                            color = Teal400,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (personalRecords.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No PRs logged yet. Check off workout sets to achieve a record!",
                            color = Slate500,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val grouped = personalRecords.groupBy { it.exerciseName }
                        grouped.forEach { (exerciseName, records) ->
                            val bestWeight = records.filter { it.metricType == "weight" }.maxByOrNull { it.value }
                            val bestReps = records.filter { it.metricType == "reps" }.maxByOrNull { it.value }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Slate800.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = exerciseName,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        if (bestWeight != null) {
                                            Text(
                                                text = "Weight: ${bestWeight.value} kg (${bestWeight.reps} reps)",
                                                color = Teal400,
                                                fontSize = 11.sp
                                            )
                                        }
                                        if (bestReps != null) {
                                            Text(
                                                text = "Reps: ${bestReps.value.toInt()} reps",
                                                color = Amber400,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                                
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    records.forEach { r ->
                                        IconButton(
                                            onClick = { viewModel.deletePersonalRecord(r.id) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete Record",
                                                tint = Crimson500.copy(alpha = 0.6f),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
