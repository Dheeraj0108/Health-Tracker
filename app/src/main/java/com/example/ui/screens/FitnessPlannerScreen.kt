package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
    var activePlannerTab by remember { mutableStateOf(0) } // 0 = 12-Week Plan, 1 = Routine Builder, 2 = Activity Logs
    
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
                text = { Text("12-Week Plan", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                unselectedContentColor = Slate400,
                selectedContentColor = Teal400
            )
            Tab(
                selected = activePlannerTab == 1,
                onClick = { activePlannerTab = 1 },
                text = { Text("Routines", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                unselectedContentColor = Slate400,
                selectedContentColor = Teal400
            )
            Tab(
                selected = activePlannerTab == 2,
                onClick = { activePlannerTab = 2 },
                text = { Text("Logs List", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                unselectedContentColor = Slate400,
                selectedContentColor = Teal400
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (activePlannerTab) {
                0 -> TwelveWeekPlanView(viewModel, plans, routines)
                1 -> RoutineBuilderView(viewModel, routines)
                2 -> LogsTab(viewModel)
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
    var showAddDialog by remember { mutableStateOf(false) }

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
                color = Color.White
            )
            Button(
                onClick = { showAddDialog = true },
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
                Text("No custom routines built yet. Create one above!", color = Slate500, fontSize = 14.sp)
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
                        colors = CardDefaults.cardColors(containerColor = Slate900)
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
                                        color = Color.White,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = routine.description,
                                        color = Slate400,
                                        fontSize = 12.sp,
                                        maxLines = if (isExpanded) Int.MAX_VALUE else 1
                                    )
                                }
                                IconButton(onClick = {
                                    viewModel.deleteRoutine(routine.id)
                                    Toast.makeText(context, "Routine Deleted", Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Crimson500, modifier = Modifier.size(20.dp))
                                }
                            }

                            AnimatedVisibility(visible = isExpanded) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Slate950)
                                        .padding(10.dp)
                                ) {
                                    Text("Exercises:", fontWeight = FontWeight.Bold, color = Teal400, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    val exercisesList = routine.exercisesListJson.split(";").filter { it.isNotBlank() }
                                    exercisesList.forEach { exercise ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(modifier = Modifier.size(6.dp).background(Teal400, RoundedCornerShape(100)))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(exercise.trim(), color = Slate100, fontSize = 12.sp)
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

    if (showAddDialog) {
        var routineName by remember { mutableStateOf("") }
        var routineDesc by remember { mutableStateOf("") }
        var exercisesText by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Create Custom Routine", color = Color.White) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = routineName,
                        onValueChange = { routineName = it },
                        label = { Text("Routine Name (e.g., Heavy Legs)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Teal400,
                            unfocusedBorderColor = Slate600,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = routineDesc,
                        onValueChange = { routineDesc = it },
                        label = { Text("Brief Description") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Teal400,
                            unfocusedBorderColor = Slate600,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = exercisesText,
                        onValueChange = { exercisesText = it },
                        label = { Text("Exercises (separate with semi-colon ';')") },
                        placeholder = { Text("Squat (4x8); Leg Press (3x12)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Teal400,
                            unfocusedBorderColor = Slate600,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (routineName.isNotBlank() && exercisesText.isNotBlank()) {
                            viewModel.addRoutine(routineName, routineDesc, exercisesText)
                            Toast.makeText(context, "Routine Created!", Toast.LENGTH_SHORT).show()
                            showAddDialog = false
                        } else {
                            Toast.makeText(context, "Please enter routine name and exercises.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Teal400)
                ) {
                    Text("Create", color = Slate950)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = Slate400)
                }
            },
            containerColor = Slate900
        )
    }
}
