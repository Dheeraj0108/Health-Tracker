package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ExerciseRoutine
import com.example.data.model.WeeklyPlan
import com.example.ui.theme.*
import com.example.ui.viewmodel.HealthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitnessPlannerScreen(viewModel: HealthViewModel) {
    val context = LocalContext.current
    var activePlannerTab by remember { mutableStateOf(0) } // 0 = Workout, 1 = 12-Week Plan, 2 = Routines
    
    val routines by viewModel.routines.collectAsStateWithLifecycle()
    val plans by viewModel.plans.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
    ) {
        // Tab Headers
        TabRow(
            selectedTabIndex = activePlannerTab,
            containerColor = Slate950,
            contentColor = Teal400,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activePlannerTab]),
                    color = Teal400
                )
            }
        ) {
            Tab(
                selected = activePlannerTab == 0,
                onClick = { activePlannerTab = 0 },
                text = { Text("Workout", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                unselectedContentColor = Slate400,
                selectedContentColor = Teal400
            )
            Tab(
                selected = activePlannerTab == 1,
                onClick = { activePlannerTab = 1 },
                text = { Text("12-Week Plan", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                unselectedContentColor = Slate400,
                selectedContentColor = Teal400
            )
            Tab(
                selected = activePlannerTab == 2,
                onClick = { activePlannerTab = 2 },
                text = { Text("Routines", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                unselectedContentColor = Slate400,
                selectedContentColor = Teal400
            )
        }

        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .shadow(12.dp, shape = RoundedCornerShape(24.dp), clip = true),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (AppThemeState.themeMode == "white") Color.White else Slate900
            ),
            border = BorderStroke(1.dp, if (AppThemeState.themeMode == "white") Color(0xFFE2E8F0) else Slate800)
        ) {
            AnimatedContent(
                targetState = activePlannerTab,
                transitionSpec = {
                    (scaleIn(animationSpec = spring(dampingRatio = 0.45f, stiffness = Spring.StiffnessMediumLow)) + fadeIn(animationSpec = tween(220)))
                        .togetherWith(scaleOut(animationSpec = spring(dampingRatio = 0.85f)) + fadeOut(animationSpec = tween(180)))
                },
                modifier = Modifier.fillMaxSize()
            ) { targetSubTab ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    when (targetSubTab) {
                        0 -> WorkoutSessionScreen(viewModel)
                        1 -> TwelveWeekPlanView(viewModel, plans, routines)
                        2 -> RoutineBuilderView(viewModel, routines)
                    }
                }
            }
        }
    }
}

@Composable
fun TwelveWeekPlanView(
    viewModel: HealthViewModel,
    plans: List<WeeklyPlan>,
    routines: List<ExerciseRoutine>
) {
    val context = LocalContext.current
    var selectedWeek by remember { mutableStateOf(1) }
    var showAssignDialog by remember { mutableStateOf<WeeklyPlan?>(null) }

    // Filter plans for the current selected week
    val weekPlans = plans.filter { it.weekNumber == selectedWeek }

    if (plans.isEmpty()) {
        // Empty State: Preload option
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.EventNote,
                contentDescription = null,
                tint = Slate500,
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No 12-Week Plan Generated",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Kickstart your 12-week fitness transformation with a structured plan.",
                style = MaterialTheme.typography.bodyMedium,
                color = Slate400,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    viewModel.generateDefault12WeekPlan()
                    Toast.makeText(context, "12-Week Schedule Generated!", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Teal400),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Slate950)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate 12-Week Schedule", color = Slate950, fontWeight = FontWeight.Bold)
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            // Horizontal Week Selector
            Text(
                text = "Select Week",
                style = MaterialTheme.typography.titleSmall,
                color = Slate400,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                items((1..12).toList()) { week ->
                    val isSelected = week == selectedWeek
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Teal400 else Slate900)
                            .clickable { selectedWeek = week }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Week $week",
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Slate950 else Slate300,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // Days of selected week
            val daysNames = listOf("", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
            
            LazyColumn(
                modifier = Modifier.fillMaxSize().weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(weekPlans.sortedBy { it.dayOfWeek }) { plan ->
                    val dayName = daysNames.getOrNull(plan.dayOfWeek) ?: "Day ${plan.dayOfWeek}"
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Slate900)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Circular Completed Indicator
                            IconButton(onClick = {
                                viewModel.updatePlanCompletion(plan.id, !plan.isCompleted)
                            }) {
                                Icon(
                                    imageVector = if (plan.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = "Toggle Complete",
                                    tint = if (plan.isCompleted) Emerald400 else Slate400,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = dayName,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FitnessCenter,
                                        contentDescription = null,
                                        tint = Teal400,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = plan.routineNameOrId,
                                        color = if (plan.isCompleted) Slate400 else Slate200,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            // Edit/Assign Button
                            IconButton(onClick = { showAssignDialog = plan }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Reassign",
                                    tint = Slate400,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            // Reset Button
            Button(
                onClick = { viewModel.clearAllPlans() },
                colors = ButtonDefaults.buttonColors(containerColor = Slate900),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = Crimson500)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset Planner", color = Crimson500)
            }
        }
    }

    // Reassign routine dialog
    if (showAssignDialog != null) {
        val plan = showAssignDialog!!
        var customRoutineInput by remember { mutableStateOf(plan.routineNameOrId) }

        AlertDialog(
            onDismissRequest = { showAssignDialog = null },
            title = { Text("Assign Routine", color = Color.White) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Select a routine or type a custom task for this day:", color = Slate400, fontSize = 12.sp)
                    
                    // Quick choose routine items
                    Text("Predefined Routines:", fontWeight = FontWeight.Bold, color = Slate300, fontSize = 12.sp)
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Slate800)
                                    .clickable { customRoutineInput = "Rest Day" }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("Rest Day", color = Slate200, fontSize = 11.sp)
                            }
                        }
                        items(routines) { routine ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Slate800)
                                    .clickable { customRoutineInput = routine.name }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(routine.name.take(20) + "...", color = Slate200, fontSize = 11.sp)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = customRoutineInput,
                        onValueChange = { customRoutineInput = it },
                        label = { Text("Routine Name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Teal400,
                            unfocusedBorderColor = Slate600,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addPlan(plan.weekNumber, plan.dayOfWeek, customRoutineInput)
                        // Room replace onConflict will override the previous plan
                        showAssignDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Teal400)
                ) {
                    Text("Assign", color = Slate950)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAssignDialog = null }) {
                    Text("Cancel", color = Slate400)
                }
            },
            containerColor = Slate900
        )
    }
}

@Composable
fun RoutineBuilderView(
    viewModel: HealthViewModel,
    routines: List<ExerciseRoutine>
) {
    val context = LocalContext.current
    val customExercises by viewModel.customExercises.collectAsStateWithLifecycle()

    var showRoutineDialog by remember { mutableStateOf(false) }
    var editingRoutineId by remember { mutableStateOf<Int?>(null) }
    var routineName by remember { mutableStateOf("") }
    var routineDesc by remember { mutableStateOf("") }
    val selectedExercises = remember { mutableStateListOf<SelectedExerciseEntry>() }

    var showExerciseSelector by remember { mutableStateOf(false) }
    var showCustomExercisePopup by remember { mutableStateOf(false) }

    val defaultExerciseRegistry = remember {
        listOf(
            "Squats", "Leg Press", "Deadlift", "Leg Curl", "Calf Raises",
            "Bench Press", "Dumbbell Flys", "Incline Press", "Overhead Press",
            "Lateral Raises", "Pull-ups", "Barbell Rows", "Bicep Curls",
            "Hammer Curls", "Tricep Pushdowns", "Plank", "Crunches"
        )
    }

    val allSelectableExercises = remember(customExercises) {
        defaultExerciseRegistry + customExercises.map { it.name }
    }

    // Function to open the routine creation/editing dialog
    val openRoutineDialog = { routine: ExerciseRoutine? ->
        if (routine != null) {
            editingRoutineId = routine.id
            routineName = routine.name
            routineDesc = routine.description
            selectedExercises.clear()
            // Parse exercisesListJson (format: Name|sets|reps|weight;Name|sets|reps|weight)
            routine.exercisesListJson.split(";").forEach { item ->
                if (item.isNotBlank()) {
                    val parts = item.split("|")
                    if (parts.isNotEmpty()) {
                        val name = parts[0]
                        val sets = parts.getOrNull(1) ?: "4"
                        val reps = parts.getOrNull(2) ?: "10"
                        val weight = parts.getOrNull(3) ?: "0"
                        selectedExercises.add(SelectedExerciseEntry(name, sets, reps, weight))
                    }
                }
            }
        } else {
            editingRoutineId = null
            routineName = ""
            routineDesc = ""
            selectedExercises.clear()
        }
        showRoutineDialog = true
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Routines",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = if (AppThemeState.themeMode == "white") Slate900 else Color.White
            )
            Button(
                onClick = { openRoutineDialog(null) },
                colors = ButtonDefaults.buttonColors(containerColor = Teal400),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Slate950, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Routine", color = Slate950, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (routines.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No custom routines built yet. Create one above! 🏋️‍♂️", color = Slate500, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(routines) { routine ->
                    var isExpanded by remember { mutableStateOf(false) }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isExpanded = !isExpanded },
                        colors = CardDefaults.cardColors(containerColor = if (AppThemeState.themeMode == "white") Color(0xFFF8FAFC) else Slate900),
                        border = BorderStroke(1.dp, if (AppThemeState.themeMode == "white") Color(0xFFE2E8F0) else Slate800)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = routine.name,
                                        fontWeight = FontWeight.Bold,
                                        color = if (AppThemeState.themeMode == "white") Slate900 else Color.White,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = routine.description,
                                        color = Slate400,
                                        fontSize = 12.sp,
                                        maxLines = if (isExpanded) Int.MAX_VALUE else 1
                                    )
                                }
                                Row {
                                    IconButton(onClick = { openRoutineDialog(routine) }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit Routine", tint = Teal400, modifier = Modifier.size(20.dp))
                                    }
                                    IconButton(onClick = {
                                        viewModel.deleteRoutine(routine.id)
                                        Toast.makeText(context, "Routine Deleted", Toast.LENGTH_SHORT).show()
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Crimson500, modifier = Modifier.size(20.dp))
                                    }
                                }
                            }

                            AnimatedVisibility(visible = isExpanded) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (AppThemeState.themeMode == "white") Color(0xFFEDF2F7) else Slate950)
                                        .padding(10.dp)
                                ) {
                                    Text("Exercises Matrix & Targets:", fontWeight = FontWeight.Bold, color = Teal400, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    val exercisesList = routine.exercisesListJson.split(";").filter { it.isNotBlank() }
                                    exercisesList.forEach { exercise ->
                                        val parts = exercise.split("|")
                                        val name = parts.getOrNull(0) ?: ""
                                        val sets = parts.getOrNull(1) ?: "4"
                                        val reps = parts.getOrNull(2) ?: "10"
                                        val weight = parts.getOrNull(3) ?: "0"

                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                                Box(modifier = Modifier.size(6.dp).background(Teal400, RoundedCornerShape(100)))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(name, color = if (AppThemeState.themeMode == "white") Slate900 else Slate100, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Text(
                                                text = "${sets}s × ${reps}r | ${weight}kg",
                                                color = Slate400,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
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

    // --- NEW ROUTINE / EDIT ROUTINE OVERLAY WINDOW ---
    if (showRoutineDialog) {
        AlertDialog(
            onDismissRequest = { showRoutineDialog = false },
            title = {
                Text(
                    text = if (editingRoutineId == null) "New Routine template" else "Edit Routine template",
                    color = if (AppThemeState.themeMode == "white") Slate900 else Color.White,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        OutlinedTextField(
                            value = routineName,
                            onValueChange = { routineName = it },
                            label = { Text("Routine Name (e.g., Heavy Legs)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Teal400,
                                unfocusedBorderColor = Slate600,
                                focusedTextColor = if (AppThemeState.themeMode == "white") Slate900 else Color.White,
                                unfocusedTextColor = if (AppThemeState.themeMode == "white") Slate900 else Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = routineDesc,
                            onValueChange = { routineDesc = it },
                            label = { Text("Brief Description") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Teal400,
                                unfocusedBorderColor = Slate600,
                                focusedTextColor = if (AppThemeState.themeMode == "white") Slate900 else Color.White,
                                unfocusedTextColor = if (AppThemeState.themeMode == "white") Slate900 else Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Exercises Allocation",
                                color = Teal400,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Button(
                                onClick = { showExerciseSelector = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Teal400, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Exercises", color = Teal400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Selected exercises dynamic sets/reps metric input matrix
                    if (selectedExercises.isEmpty()) {
                        item {
                            Text(
                                "No exercises added to this routine template yet.",
                                color = Slate500,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                            )
                        }
                    } else {
                        items(selectedExercises) { entry ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = if (AppThemeState.themeMode == "white") Color(0xFFEDF2F7) else Slate950),
                                border = BorderStroke(1.dp, Slate800)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            entry.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (AppThemeState.themeMode == "white") Slate900 else Color.White,
                                            modifier = Modifier.weight(1f)
                                        )
                                        IconButton(
                                            onClick = { selectedExercises.remove(entry) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = "Remove", tint = Crimson500, modifier = Modifier.size(16.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Dynamic Matrix Allocation: adjacent sets, reps, and weight inputs
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = entry.sets,
                                            onValueChange = { entry.sets = it },
                                            label = { Text("Sets", fontSize = 10.sp) },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Teal400, unfocusedBorderColor = Slate600,
                                                focusedTextColor = if (AppThemeState.themeMode == "white") Slate900 else Color.White,
                                                unfocusedTextColor = if (AppThemeState.themeMode == "white") Slate900 else Color.White
                                            ),
                                            modifier = Modifier.weight(1f)
                                        )

                                        OutlinedTextField(
                                            value = entry.reps,
                                            onValueChange = { entry.reps = it },
                                            label = { Text("Reps", fontSize = 10.sp) },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Teal400, unfocusedBorderColor = Slate600,
                                                focusedTextColor = if (AppThemeState.themeMode == "white") Slate900 else Color.White,
                                                unfocusedTextColor = if (AppThemeState.themeMode == "white") Slate900 else Color.White
                                            ),
                                            modifier = Modifier.weight(1f)
                                        )

                                        OutlinedTextField(
                                            value = entry.weight,
                                            onValueChange = { entry.weight = it },
                                            label = { Text("Wt (kg)", fontSize = 10.sp) },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Teal400, unfocusedBorderColor = Slate600,
                                                focusedTextColor = if (AppThemeState.themeMode == "white") Slate900 else Color.White,
                                                unfocusedTextColor = if (AppThemeState.themeMode == "white") Slate900 else Color.White
                                            ),
                                            modifier = Modifier.weight(1.2f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (routineName.isNotBlank() && selectedExercises.isNotEmpty()) {
                            // Serialize back to Name|sets|reps|weight;Name|sets|reps|weight format
                            val exercisesJson = selectedExercises.joinToString(";") {
                                "${it.name}|${it.sets.ifBlank { "4" }}|${it.reps.ifBlank { "10" }}|${it.weight.ifBlank { "0" }}"
                            }

                            if (editingRoutineId == null) {
                                viewModel.addRoutine(routineName, routineDesc, exercisesJson)
                                Toast.makeText(context, "Template Created! 🚀", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.updateRoutine(editingRoutineId!!, routineName, routineDesc, exercisesJson)
                                Toast.makeText(context, "Template Edited! 🚀", Toast.LENGTH_SHORT).show()
                            }
                            showRoutineDialog = false
                        } else {
                            Toast.makeText(context, "Please enter name and allocate exercises.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Teal400)
                ) {
                    Text(if (editingRoutineId == null) "Create Template" else "Save Changes", color = Slate950, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRoutineDialog = false }) {
                    Text("Cancel", color = Slate400)
                }
            },
            containerColor = if (AppThemeState.themeMode == "white") Color.White else Slate900
        )
    }

    // --- EXERCISE REGISTRY SELECTION MODAL ---
    if (showExerciseSelector) {
        var searchQuery by remember { mutableStateOf("") }
        val filteredList = allSelectableExercises.filter { it.contains(searchQuery, ignoreCase = true) }

        AlertDialog(
            onDismissRequest = { showExerciseSelector = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Select Exercises", color = if (AppThemeState.themeMode == "white") Slate900 else Color.White, fontWeight = FontWeight.Bold)
                    Button(
                        onClick = { showCustomExercisePopup = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Teal400),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Slate950, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Custom", color = Slate950, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Search exercise") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Slate400) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Teal400, unfocusedBorderColor = Slate600,
                            focusedTextColor = if (AppThemeState.themeMode == "white") Slate900 else Color.White,
                            unfocusedTextColor = if (AppThemeState.themeMode == "white") Slate900 else Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth().heightIn(max = 280.dp)
                    ) {
                        items(filteredList) { exerciseName ->
                            val isAlreadySelected = selectedExercises.any { it.name == exerciseName }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isAlreadySelected) Teal400.copy(alpha = 0.15f) else Color.Transparent)
                                    .clickable {
                                        if (isAlreadySelected) {
                                            selectedExercises.removeAll { it.name == exerciseName }
                                        } else {
                                            selectedExercises.add(SelectedExerciseEntry(exerciseName, "4", "10", "0"))
                                        }
                                    }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    exerciseName,
                                    color = if (AppThemeState.themeMode == "white") Slate900 else Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                if (isAlreadySelected) {
                                    Icon(Icons.Default.Check, contentDescription = "Selected", tint = Teal400, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showExerciseSelector = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Teal400)
                ) {
                    Text("OK", color = Slate950, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = if (AppThemeState.themeMode == "white") Color.White else Slate900
        )
    }

    // --- CUSTOM EXERCISE CREATION POPUP MODAL ---
    if (showCustomExercisePopup) {
        var cName by remember { mutableStateOf("") }
        var cMusclePart by remember { mutableStateOf("") }
        var cEquipment by remember { mutableStateOf("Barbell") }
        var cDifficulty by remember { mutableStateOf("Intermediate") }
        var cNotes by remember { mutableStateOf("") }

        val equipmentOptions = listOf("Barbell", "Dumbbell", "Cable", "Body Weight", "Medicine Ball", "Machine", "Free Weights")
        val difficultyOptions = listOf("Beginner", "Intermediate", "Advanced")

        var showEquipmentDropdown by remember { mutableStateOf(false) }
        var showDifficultyDropdown by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showCustomExercisePopup = false },
            title = { Text("Create Custom Exercise", color = if (AppThemeState.themeMode == "white") Slate900 else Color.White, fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    item {
                        OutlinedTextField(
                            value = cName,
                            onValueChange = { cName = it },
                            label = { Text("Exercise Name") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Teal400, unfocusedBorderColor = Slate600,
                                focusedTextColor = if (AppThemeState.themeMode == "white") Slate900 else Color.White,
                                unfocusedTextColor = if (AppThemeState.themeMode == "white") Slate900 else Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = cMusclePart,
                            onValueChange = { cMusclePart = it },
                            label = { Text("Muscle Group (e.g. Chest)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Teal400, unfocusedBorderColor = Slate600,
                                focusedTextColor = if (AppThemeState.themeMode == "white") Slate900 else Color.White,
                                unfocusedTextColor = if (AppThemeState.themeMode == "white") Slate900 else Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = cEquipment,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Equipment Required") },
                                trailingIcon = {
                                    IconButton(onClick = { showEquipmentDropdown = true }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Teal400, unfocusedBorderColor = Slate600,
                                    focusedTextColor = if (AppThemeState.themeMode == "white") Slate900 else Color.White,
                                    unfocusedTextColor = if (AppThemeState.themeMode == "white") Slate900 else Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            DropdownMenu(
                                expanded = showEquipmentDropdown,
                                onDismissRequest = { showEquipmentDropdown = false }
                            ) {
                                equipmentOptions.forEach { opt ->
                                    DropdownMenuItem(
                                        text = { Text(opt) },
                                        onClick = {
                                            cEquipment = opt
                                            showEquipmentDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = cDifficulty,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Difficulty Level") },
                                trailingIcon = {
                                    IconButton(onClick = { showDifficultyDropdown = true }) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Teal400, unfocusedBorderColor = Slate600,
                                    focusedTextColor = if (AppThemeState.themeMode == "white") Slate900 else Color.White,
                                    unfocusedTextColor = if (AppThemeState.themeMode == "white") Slate900 else Color.White
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            DropdownMenu(
                                expanded = showDifficultyDropdown,
                                onDismissRequest = { showDifficultyDropdown = false }
                            ) {
                                difficultyOptions.forEach { opt ->
                                    DropdownMenuItem(
                                        text = { Text(opt) },
                                        onClick = {
                                            cDifficulty = opt
                                            showDifficultyDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = cNotes,
                            onValueChange = { cNotes = it },
                            label = { Text("Notes/Instructions") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Teal400, unfocusedBorderColor = Slate600,
                                focusedTextColor = if (AppThemeState.themeMode == "white") Slate900 else Color.White,
                                unfocusedTextColor = if (AppThemeState.themeMode == "white") Slate900 else Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (cName.isNotBlank() && cMusclePart.isNotBlank()) {
                            viewModel.addCustomExercise(
                                name = cName,
                                bodyPart = cMusclePart,
                                equipment = cEquipment,
                                level = cDifficulty,
                                specificInstruction = cNotes.ifEmpty { null },
                                onSuccess = {},
                                onError = {}
                            )
                            Toast.makeText(context, "Custom Exercise Created! 🏋️‍♂️", Toast.LENGTH_SHORT).show()
                            showCustomExercisePopup = false
                        } else {
                            Toast.makeText(context, "Please complete Name and Muscle Group.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Teal400)
                ) {
                    Text("Save Exercise", color = Slate950, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomExercisePopup = false }) {
                    Text("Cancel", color = Slate400)
                }
            },
            containerColor = if (AppThemeState.themeMode == "white") Color.White else Slate900
        )
    }
}

// Data class representation for dynamic state allocation
class SelectedExerciseEntry(
    val name: String,
    initialSets: String = "4",
    initialReps: String = "10",
    initialWeight: String = "0"
) {
    var sets by mutableStateOf(initialSets)
    var reps by mutableStateOf(initialReps)
    var weight by mutableStateOf(initialWeight)
}

