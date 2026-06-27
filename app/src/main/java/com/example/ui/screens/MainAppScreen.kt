package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import android.speech.RecognizerIntent
import android.content.Intent
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.R
import com.example.data.api.NutritionResult
import com.example.data.model.CaffeineLog
import com.example.data.model.ExerciseLog
import com.example.data.model.FoodLog
import com.example.data.model.WaterLog
import com.example.data.model.Goal
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import com.example.ui.components.CircularProgressRing
import com.example.ui.components.WeeklyBarChart
import com.example.ui.components.FitnessCalendar
import com.example.ui.theme.*
import com.example.ui.viewmodel.HealthViewModel
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: HealthViewModel) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(0) }

    // Observers
    val waterLogs by viewModel.waterLogs.collectAsStateWithLifecycle()
    val caffeineLogs by viewModel.caffeineLogs.collectAsStateWithLifecycle()
    val exerciseLogs by viewModel.exerciseLogs.collectAsStateWithLifecycle()
    val foodLogs by viewModel.foodLogs.collectAsStateWithLifecycle()
    val goals by viewModel.goals.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Teal500, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Logo",
                                tint = Slate950,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = "Health Tracker",
                            fontWeight = FontWeight.Bold,
                            color = Slate50,
                            fontSize = 20.sp
                        )
                    }
                },
                actions = {
                    var showThemeMenu by remember { mutableStateOf(false) }
                    Box {
                        IconButton(
                            onClick = { showThemeMenu = true },
                            modifier = Modifier.testTag("theme_selector_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Change Theme",
                                tint = Teal400
                            )
                        }
                        DropdownMenu(
                            expanded = showThemeMenu,
                            onDismissRequest = { showThemeMenu = false },
                            modifier = Modifier.background(Slate900)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Dark Gold Theme", color = Slate50, fontWeight = FontWeight.SemiBold) },
                                leadingIcon = { Icon(Icons.Default.DarkMode, contentDescription = null, tint = Color(0xFFFACC15)) },
                                onClick = {
                                    AppThemeState.themeMode = "dark_yellow"
                                    showThemeMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Warm Nude Theme", color = Slate50, fontWeight = FontWeight.SemiBold) },
                                leadingIcon = { Icon(Icons.Default.LightMode, contentDescription = null, tint = Color(0xFFB5835A)) },
                                onClick = {
                                    AppThemeState.themeMode = "light_nude"
                                    showThemeMenu = false
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Slate950,
                    titleContentColor = Slate50
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Slate950,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                    label = { Text("Dashboard", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Teal400,
                        selectedTextColor = Teal400,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400,
                        indicatorColor = Slate900
                    ),
                    modifier = Modifier.testTag("nav_dashboard")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.EventNote, contentDescription = "Planner") },
                    label = { Text("Planner", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Teal400,
                        selectedTextColor = Teal400,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400,
                        indicatorColor = Slate900
                    ),
                    modifier = Modifier.testTag("nav_planner")
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.CameraAlt, contentDescription = "NutriSnap") },
                    label = { Text("NutriSnap", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Teal400,
                        selectedTextColor = Teal400,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400,
                        indicatorColor = Slate900
                    ),
                    modifier = Modifier.testTag("nav_nutrisnap")
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Body Track") },
                    label = { Text("Body Track", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Teal400,
                        selectedTextColor = Teal400,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400,
                        indicatorColor = Slate900
                    ),
                    modifier = Modifier.testTag("nav_bodytrack")
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Default.FitnessCenter, contentDescription = "Explorer") },
                    label = { Text("Explorer", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Teal400,
                        selectedTextColor = Teal400,
                        unselectedIconColor = Slate400,
                        unselectedTextColor = Slate400,
                        indicatorColor = Slate900
                    ),
                    modifier = Modifier.testTag("nav_explorer")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Slate950)
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> DashboardTab(viewModel)
                1 -> FitnessPlannerScreen(viewModel)
                2 -> NutriSnapTab(viewModel)
                3 -> BodyTrackerScreen(viewModel)
                4 -> ExplorerCompositeScreen(viewModel)
            }
        }
    }
}

// Helper to resolve generated drawable IDs
fun getDrawableId(name: String?, context: android.content.Context): Int {
    if (name == null) return 0
    return context.resources.getIdentifier(name, "drawable", context.packageName)
}

@Composable
fun DashboardTab(viewModel: HealthViewModel) {
    val context = LocalContext.current

    var showAddExerciseDialog by remember { mutableStateOf(false) }
    var exerciseType by remember { mutableStateOf("GYM") } // "GYM", "RUN", "WALK"
    var gymName by remember { mutableStateOf("") }
    var gymSets by remember { mutableStateOf("3") }
    var gymReps by remember { mutableStateOf("10") }
    var durationMins by remember { mutableStateOf("30") }
    var distanceKm by remember { mutableStateOf("5.0") }

    val exerciseSpeechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = results?.getOrNull(0) ?: ""
            if (spokenText.isNotEmpty()) {
                gymName = spokenText
            }
        }
    }

    // Calculated totals
    val todayWater = viewModel.getTodayWaterTotal()
    val todayCaffeine = viewModel.getTodayCaffeineTotal()
    val todayCalories = viewModel.getTodayCaloriesTotal()
    val todayExercise = viewModel.getTodayExerciseMinutesTotal()

    // Goals
    val goalWater = viewModel.getGoalValue("water_ml", 2000.0).toInt()
    val goalCaffeine = viewModel.getGoalValue("caffeine_mg", 400.0).toInt()
    val goalCalories = viewModel.getGoalValue("calories_kcal", 2000.0).toInt()
    val goalExercise = viewModel.getGoalValue("exercise_min", 30.0).toInt()

    // Macros
    val todayProtein = viewModel.getTodayProteinTotal()
    val todayCarbs = viewModel.getTodayCarbsTotal()
    val todayFat = viewModel.getTodayFatTotal()

    val goalProtein = viewModel.getGoalValue("protein_g", 120.0)
    val goalCarbs = viewModel.getGoalValue("carbs_g", 250.0)
    val goalFat = viewModel.getGoalValue("fat_g", 70.0)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Good morning!",
                            style = MaterialTheme.typography.titleMedium,
                            color = Slate400
                        )
                        Text(
                            text = "Track Your Vitals",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .background(Teal500.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Heart Icon",
                            tint = Teal400,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        // Circular progress rings Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Water Ring
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Slate900),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Water", style = MaterialTheme.typography.labelMedium, color = Slate400)
                        CircularProgressRing(
                            progress = if (goalWater > 0) todayWater.toFloat() / goalWater else 0f,
                            color = Teal400,
                            size = 70.dp
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalDrink,
                                contentDescription = "Water",
                                tint = Teal400,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            "$todayWater / $goalWater ml",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Exercise Ring
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Slate900),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Exercise", style = MaterialTheme.typography.labelMedium, color = Slate400)
                        CircularProgressRing(
                            progress = if (goalExercise > 0) todayExercise.toFloat() / goalExercise else 0f,
                            color = Indigo400,
                            size = 70.dp
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsRun,
                                contentDescription = "Exercise",
                                tint = Indigo400,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            "$todayExercise / $goalExercise m",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // Caffeine Ring
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Slate900),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Caffeine", style = MaterialTheme.typography.labelMedium, color = Slate400)
                        CircularProgressRing(
                            progress = if (goalCaffeine > 0) todayCaffeine.toFloat() / goalCaffeine else 0f,
                            color = Amber400,
                            size = 70.dp
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalCafe,
                                contentDescription = "Caffeine",
                                tint = Amber400,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Text(
                            "$todayCaffeine / $goalCaffeine mg",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Calories & Macros Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Today's Energy Balance",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressRing(
                            progress = if (goalCalories > 0) todayCalories.toFloat() / goalCalories else 0f,
                            color = Amber500,
                            size = 90.dp
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    todayCalories.toInt().toString(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text("kcal", fontSize = 10.sp, color = Slate400)
                            }
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Protein bar
                            MacroProgressRow("Protein", todayProtein, goalProtein, Emerald500)
                            // Carbs bar
                            MacroProgressRow("Carbs", todayCarbs, goalCarbs, Teal400)
                            // Fat bar
                            MacroProgressRow("Fat", todayFat, goalFat, Amber400)
                        }
                    }
                }
            }
        }

        // Micros List Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Micronutrients Logged Today",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        MicroRow("Fiber", viewModel.getTodayFiberTotal(), "g")
                        MicroRow("Vitamin C", viewModel.getTodayVitaminCTotal(), "mg")
                        MicroRow("Vitamin A", viewModel.getTodayVitaminATotal(), "mcg")
                        MicroRow("Vitamin B", viewModel.getTodayVitaminBTotal(), "mg")
                        MicroRow("Calcium", viewModel.getTodayCalciumTotal(), "mg")
                        MicroRow("Magnesium", viewModel.getTodayMagnesiumTotal(), "mg")
                        MicroRow("Iron", viewModel.getTodayIronTotal(), "mg")
                    }
                }
            }
        }

        // Monthly Fitness Goals Calendar
        item {
            val exerciseLogs by viewModel.exerciseLogs.collectAsStateWithLifecycle()
            FitnessCalendar(
                exerciseLogs = exerciseLogs,
                dailyGoalMin = goalExercise.toDouble(),
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // Quick log actions
        item {
            Text(
                "Quick Shortcuts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.logWater(250)
                        Toast.makeText(context, "+250ml Water Added", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Slate900),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.LocalDrink, contentDescription = null, tint = Teal400)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("+250ml", color = Slate50, fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        viewModel.logCaffeine(80)
                        Toast.makeText(context, "+80mg Caffeine Added", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Slate900),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.LocalCafe, contentDescription = null, tint = Amber400)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("+80mg", color = Slate50, fontSize = 11.sp)
                }

                Button(
                    onClick = { showAddExerciseDialog = true },
                    modifier = Modifier.weight(1.3f).testTag("log_workout_shortcut_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Slate900),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = Indigo400)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Workout", color = Slate50, fontSize = 11.sp)
                }
            }
        }
    }

    if (showAddExerciseDialog) {
        AlertDialog(
            onDismissRequest = { showAddExerciseDialog = false },
            title = { Text("Log Exercise Session", fontWeight = FontWeight.Bold, color = Slate50) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Session Type:", color = Slate400, fontSize = 12.sp)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("GYM", "RUN", "WALK").forEach { type ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (exerciseType == type) Indigo500 else Slate800,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { exerciseType = type }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    type,
                                    color = if (exerciseType == type) Slate950 else Slate50,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        value = gymName,
                        onValueChange = { gymName = it },
                        label = { Text(if (exerciseType == "GYM") "Workout Note / Name" else "Activity Note") },
                        placeholder = { Text("e.g. Bench press / Running around park", color = Slate400) },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                                        putExtra(RecognizerIntent.EXTRA_PROMPT, "Describe your exercise name/note...")
                                    }
                                    try {
                                        exerciseSpeechLauncher.launch(intent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Speech recognition not supported", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.testTag("voice_input_exercise_btn")
                            ) {
                                Icon(Icons.Default.Mic, contentDescription = "Voice Input", tint = Indigo400)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Slate50,
                            unfocusedTextColor = Slate50,
                            focusedBorderColor = Indigo400
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("gym_name_input")
                    )

                    if (exerciseType == "GYM") {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = gymSets,
                                onValueChange = { gymSets = it },
                                label = { Text("Sets") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Slate50,
                                    unfocusedTextColor = Slate50,
                                    focusedBorderColor = Indigo400
                                ),
                                modifier = Modifier.weight(1f).testTag("gym_sets_input")
                            )
                            OutlinedTextField(
                                value = gymReps,
                                onValueChange = { gymReps = it },
                                label = { Text("Reps") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Slate50,
                                    unfocusedTextColor = Slate50,
                                    focusedBorderColor = Indigo400
                                ),
                                modifier = Modifier.weight(1f).testTag("gym_reps_input")
                            )
                        }
                    }

                    if (exerciseType == "RUN" || exerciseType == "WALK") {
                        OutlinedTextField(
                            value = distanceKm,
                            onValueChange = { distanceKm = it },
                            label = { Text("Distance (km)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Slate50,
                                unfocusedTextColor = Slate50,
                                focusedBorderColor = Indigo400
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("distance_input")
                        )
                    }

                    OutlinedTextField(
                        value = durationMins,
                        onValueChange = { durationMins = it },
                        label = { Text("Duration (minutes)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Slate50,
                            unfocusedTextColor = Slate50,
                            focusedBorderColor = Indigo400
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("duration_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val duration = durationMins.toIntOrNull() ?: 30
                        val dist = distanceKm.toDoubleOrNull() ?: 0.0
                        val sets = gymSets.toIntOrNull() ?: 3
                        val reps = gymReps.toIntOrNull() ?: 10

                        viewModel.logExercise(
                            type = exerciseType,
                            exerciseName = gymName.ifEmpty { if (exerciseType == "GYM") "Workout" else exerciseType },
                            sets = if (exerciseType == "GYM") sets else null,
                            reps = if (exerciseType == "GYM") reps else null,
                            durationMinutes = duration,
                            distanceKm = if (exerciseType != "GYM") dist else null
                        )
                        showAddExerciseDialog = false
                        gymName = "" // reset
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo400)
                ) {
                    Text("Add", color = Slate950)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddExerciseDialog = false }) {
                    Text("Cancel", color = Slate400)
                }
            },
            containerColor = Slate900
        )
    }
}

@Composable
fun MacroProgressRow(name: String, current: Double, target: Double, color: Color) {
    val progress = if (target > 0) (current / target).toFloat().coerceIn(0f, 1f) else 0f
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(name, fontSize = 11.sp, color = Slate50, fontWeight = FontWeight.Bold)
            Text("${current.toInt()}g / ${target.toInt()}g", fontSize = 10.sp, color = Slate400)
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = color,
            trackColor = Slate800
        )
    }
}

@Composable
fun MicroRow(name: String, value: Double, unit: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(name, fontSize = 12.sp, color = Slate400)
        Text(
            String.format(Locale.US, "%.1f %s", value, unit),
            fontSize = 12.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun LogsTab(viewModel: HealthViewModel) {
    val context = LocalContext.current

    // Observe logs
    val waterLogs by viewModel.waterLogs.collectAsStateWithLifecycle()
    val caffeineLogs by viewModel.caffeineLogs.collectAsStateWithLifecycle()
    val exerciseLogs by viewModel.exerciseLogs.collectAsStateWithLifecycle()
    val foodLogs by viewModel.foodLogs.collectAsStateWithLifecycle()

    var showAddWaterDialog by remember { mutableStateOf(false) }
    var showAddCaffeineDialog by remember { mutableStateOf(false) }
    var showAddExerciseDialog by remember { mutableStateOf(false) }

    // Dialog form values
    var waterInput by remember { mutableStateOf("250") }
    var caffeineInput by remember { mutableStateOf("80") }

    // Exercise form values
    var exerciseType by remember { mutableStateOf("GYM") } // "GYM", "RUN", "WALK"
    var gymName by remember { mutableStateOf("") }
    var gymSets by remember { mutableStateOf("3") }
    var gymReps by remember { mutableStateOf("10") }
    var durationMins by remember { mutableStateOf("30") }
    var distanceKm by remember { mutableStateOf("5.0") }

    // Combined logs for scrolling
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Logging Shortcuts Title & Buttons
        Text(
            "Log Daily Vitality",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { showAddWaterDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Slate900),
                modifier = Modifier.weight(1f).testTag("log_water_btn")
            ) {
                Icon(Icons.Default.LocalDrink, contentDescription = null, tint = Teal400)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Water", fontSize = 11.sp, color = Slate50)
            }
            Button(
                onClick = { showAddCaffeineDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Slate900),
                modifier = Modifier.weight(1f).testTag("log_caffeine_btn")
            ) {
                Icon(Icons.Default.LocalCafe, contentDescription = null, tint = Amber400)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Caffeine", fontSize = 11.sp, color = Slate50)
            }
            Button(
                onClick = { showAddExerciseDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Slate900),
                modifier = Modifier.weight(1.2f).testTag("log_exercise_btn")
            ) {
                Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = Indigo400)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Exercise", fontSize = 11.sp, color = Slate50)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "Logged Activities",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))

        val allLogsList = remember(waterLogs, caffeineLogs, exerciseLogs, foodLogs) {
            val list = mutableListOf<LogUiItem>()
            waterLogs.forEach {
                list.add(LogUiItem.Water(it))
            }
            caffeineLogs.forEach {
                list.add(LogUiItem.Caffeine(it))
            }
            exerciseLogs.forEach {
                list.add(LogUiItem.Exercise(it))
            }
            foodLogs.forEach {
                list.add(LogUiItem.Food(it))
            }
            list.sortByDescending { it.timestamp }
            list
        }

        if (allLogsList.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Slate400,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "No activities logged yet for today.",
                        color = Slate400,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(allLogsList) { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Slate900),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(item.iconBg, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = null,
                                    tint = item.iconColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    item.title,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                Text(
                                    "${item.details} • ${sdf.format(Date(item.timestamp))}",
                                    fontSize = 12.sp,
                                    color = Slate400
                                )
                            }

                            IconButton(onClick = {
                                when (item) {
                                    is LogUiItem.Water -> viewModel.deleteWaterLog(item.log.id)
                                    is LogUiItem.Caffeine -> viewModel.deleteCaffeineLog(item.log.id)
                                    is LogUiItem.Exercise -> viewModel.deleteExerciseLog(item.log.id)
                                    is LogUiItem.Food -> viewModel.deleteFoodLog(item.log.id)
                                }
                                Toast.makeText(context, "Log Deleted", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Crimson500)
                            }
                        }
                    }
                }
            }
        }
    }

    // Water dialog
    if (showAddWaterDialog) {
        AlertDialog(
            onDismissRequest = { showAddWaterDialog = false },
            title = { Text("Log Water Intake") },
            text = {
                OutlinedTextField(
                    value = waterInput,
                    onValueChange = { waterInput = it },
                    label = { Text("Amount (ml)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Teal400,
                        focusedLabelColor = Teal400
                    ),
                    modifier = Modifier.testTag("water_input")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = waterInput.toIntOrNull() ?: 250
                        viewModel.logWater(amt)
                        showAddWaterDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Teal400)
                ) {
                    Text("Add", color = Slate950)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddWaterDialog = false }) {
                    Text("Cancel", color = Slate400)
                }
            },
            containerColor = Slate900
        )
    }

    // Caffeine dialog
    if (showAddCaffeineDialog) {
        AlertDialog(
            onDismissRequest = { showAddCaffeineDialog = false },
            title = { Text("Log Caffeine Intake") },
            text = {
                OutlinedTextField(
                    value = caffeineInput,
                    onValueChange = { caffeineInput = it },
                    label = { Text("Amount (mg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Amber400,
                        focusedLabelColor = Amber400
                    ),
                    modifier = Modifier.testTag("caffeine_input")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = caffeineInput.toIntOrNull() ?: 80
                        viewModel.logCaffeine(amt)
                        showAddCaffeineDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Amber400)
                ) {
                    Text("Add", color = Slate950)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCaffeineDialog = false }) {
                    Text("Cancel", color = Slate400)
                }
            },
            containerColor = Slate900
        )
    }

    // Exercise dialog
    if (showAddExerciseDialog) {
        AlertDialog(
            onDismissRequest = { showAddExerciseDialog = false },
            title = { Text("Log Exercise Session", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Type selector
                    Text("Session Type:", color = Slate400, fontSize = 12.sp)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("GYM", "RUN", "WALK").forEach { type ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (exerciseType == type) Indigo500 else Slate800,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { exerciseType = type }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    type,
                                    color = if (exerciseType == type) Slate950 else Slate50,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    if (exerciseType == "GYM") {
                        OutlinedTextField(
                            value = gymName,
                            onValueChange = { gymName = it },
                            label = { Text("Exercise Name (e.g. Bench Press)") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Indigo400
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("gym_name_input")
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = gymSets,
                                onValueChange = { gymSets = it },
                                label = { Text("Sets") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Indigo400
                                ),
                                modifier = Modifier.weight(1f).testTag("gym_sets_input")
                            )
                            OutlinedTextField(
                                value = gymReps,
                                onValueChange = { gymReps = it },
                                label = { Text("Reps") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Indigo400
                                ),
                                modifier = Modifier.weight(1f).testTag("gym_reps_input")
                            )
                        }
                    }

                    if (exerciseType == "RUN" || exerciseType == "WALK") {
                        OutlinedTextField(
                            value = distanceKm,
                            onValueChange = { distanceKm = it },
                            label = { Text("Distance (km)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Indigo400
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("distance_input")
                        )
                    }

                    OutlinedTextField(
                        value = durationMins,
                        onValueChange = { durationMins = it },
                        label = { Text("Duration (minutes)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Indigo400
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("duration_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val duration = durationMins.toIntOrNull() ?: 30
                        val dist = distanceKm.toDoubleOrNull() ?: 0.0
                        val sets = gymSets.toIntOrNull() ?: 3
                        val reps = gymReps.toIntOrNull() ?: 10

                        viewModel.logExercise(
                            type = exerciseType,
                            exerciseName = if (exerciseType == "GYM") gymName.ifEmpty { "Workout" } else null,
                            sets = if (exerciseType == "GYM") sets else null,
                            reps = if (exerciseType == "GYM") reps else null,
                            durationMinutes = duration,
                            distanceKm = if (exerciseType != "GYM") dist else null
                        )
                        showAddExerciseDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo400)
                ) {
                    Text("Add", color = Slate950)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddExerciseDialog = false }) {
                    Text("Cancel", color = Slate400)
                }
            },
            containerColor = Slate900
        )
    }
}

sealed class LogUiItem(val timestamp: Long) {
    abstract val icon: androidx.compose.ui.graphics.vector.ImageVector
    abstract val iconColor: Color
    abstract val iconBg: Color
    abstract val title: String
    abstract val details: String

    class Water(val log: WaterLog) : LogUiItem(log.timestamp) {
        override val icon = Icons.Default.LocalDrink
        override val iconColor = Teal400
        override val iconBg = Teal500.copy(alpha = 0.15f)
        override val title = "Drank Water"
        override val details = "${log.amountMl} ml"
    }

    class Caffeine(val log: CaffeineLog) : LogUiItem(log.timestamp) {
        override val icon = Icons.Default.LocalCafe
        override val iconColor = Amber400
        override val iconBg = Amber500.copy(alpha = 0.15f)
        override val title = "Caffeine Boost"
        override val details = "${log.amountMg} mg"
    }

    class Exercise(val log: ExerciseLog) : LogUiItem(log.timestamp) {
        override val icon = when (log.type) {
            "GYM" -> Icons.Default.FitnessCenter
            "RUN" -> Icons.Default.DirectionsRun
            else -> Icons.Default.DirectionsWalk
        }
        override val iconColor = Indigo400
        override val iconBg = Indigo500.copy(alpha = 0.15f)
        override val title = when (log.type) {
            "GYM" -> "Gym: ${log.exerciseName ?: "Workout"}"
            "RUN" -> "Running"
            else -> "Walking"
        }
        override val details = when (log.type) {
            "GYM" -> "${log.sets ?: 3} sets x ${log.reps ?: 10} reps • ${log.durationMinutes} mins"
            else -> "${log.distanceKm ?: 0.0} km • ${log.durationMinutes} mins"
        }
    }

    class Food(val log: FoodLog) : LogUiItem(log.timestamp) {
        override val icon = Icons.Default.Restaurant
        override val iconColor = Amber500
        override val iconBg = Amber500.copy(alpha = 0.15f)
        override val title = log.description
        override val details = "${log.calories.toInt()} kcal • P: ${log.protein.toInt()}g, C: ${log.carbs.toInt()}g, F: ${log.fat.toInt()}g"
    }
}

@Composable
fun NutriSnapTab(viewModel: HealthViewModel) {
    val context = LocalContext.current

    val isAnalyzing by viewModel.isAnalyzingFood.collectAsStateWithLifecycle()
    val result by viewModel.analysisResult.collectAsStateWithLifecycle()

    var foodDescriptionInput by remember { mutableStateOf("") }
    var selectedImagePathOrRes by remember { mutableStateOf<String?>(null) }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val foodSpeechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = results?.getOrNull(0) ?: ""
            if (spokenText.isNotEmpty()) {
                foodDescriptionInput = spokenText
            }
        }
    }

    // Preloaded high-quality generated mock food choice list
    val preloadedFoods = listOf(
        MockFoodItem("Avocado Sourdough Toast", "img_avocado_toast_1782576630562", "Slice of sourdough topped with mashed avocado, tomatoes, radish, and poached egg"),
        MockFoodItem("Grilled Chicken Salad", "img_grilled_chicken_salad_1782576643885", "Fresh salad with chicken breast, mixed greens, avocado, and vinaigrette"),
        MockFoodItem("Berry Protein Smoothie", "img_berry_smoothie_1782576659089", "Smoothie packed with strawberries, blueberries, mint, and whey protein"),
        MockFoodItem("Pan-seared Salmon Plate", "img_salmon_plate_1782576672208", "Salmon fillet served with wild rice, asparagus, and lemon")
    )

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            selectedBitmap = bitmap
            selectedImagePathOrRes = null // Clear resource-based selection
            foodDescriptionInput = "Captured Meal photo"
            Toast.makeText(context, "Photo Captured!", Toast.LENGTH_SHORT).show()
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "NutriSnap AI Tracker",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "Snap or select meals to extract and log nutrients instantly",
                    style = MaterialTheme.typography.labelMedium,
                    color = Slate400,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Show result details if analyzing completed successfully
        if (result != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Slate900),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Slate800)
                            ) {
                                if (selectedBitmap != null) {
                                    Image(
                                        bitmap = selectedBitmap!!.asImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else if (selectedImagePathOrRes != null) {
                                    Image(
                                        painter = rememberAsyncImagePainter(getDrawableId(selectedImagePathOrRes, context)),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.Restaurant,
                                        contentDescription = null,
                                        tint = Teal400,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                Text(
                                    result!!.description,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    "${result!!.calories.toInt()} kcal",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Teal400,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            "Macronutrients Breakdown",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            MacroStatBox("Protein", "${result!!.protein.toInt()}g", Emerald500, Modifier.weight(1f))
                            MacroStatBox("Carbs", "${result!!.carbs.toInt()}g", Teal400, Modifier.weight(1f))
                            MacroStatBox("Fat", "${result!!.fat.toInt()}g", Amber400, Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            "Micronutrients Breakdown",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            MicroRow("Fiber", result!!.fiber, "g")
                            MicroRow("Vitamin C", result!!.vitaminC, "mg")
                            MicroRow("Vitamin A", result!!.vitaminA, "mcg")
                            MicroRow("Vitamin B", result!!.vitaminB, "mg")
                            MicroRow("Calcium", result!!.calcium, "mg")
                            MicroRow("Magnesium", result!!.magnesium, "mg")
                            MicroRow("Iron", result!!.iron, "mg")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Indigo500.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                .border(1.dp, Indigo500.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Indigo400, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("AI Nutritional Insight", fontWeight = FontWeight.Bold, color = Indigo400, fontSize = 12.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    result!!.insights,
                                    color = Slate50,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { viewModel.cancelFoodAnalysis() },
                                colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Discard", color = Slate50)
                            }
                            Button(
                                onClick = {
                                    viewModel.confirmFoodLog(result!!, selectedImagePathOrRes)
                                    selectedBitmap = null
                                    selectedImagePathOrRes = null
                                    foodDescriptionInput = ""
                                    Toast.makeText(context, "Logged Successfully!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Teal400),
                                modifier = Modifier.weight(1.5f).testTag("confirm_save_food_btn")
                            ) {
                                Text("Save to Daily Log", color = Slate950)
                            }
                        }
                    }
                }
            }
        } else {
            // Meal input selection & scanner configuration
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Slate900),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Step 1: Choose or Snap your meal",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )

                        // Chosen visual preview
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Slate800)
                                .clickable { cameraLauncher.launch(null) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedBitmap != null) {
                                Image(
                                    bitmap = selectedBitmap!!.asImageBitmap(),
                                    contentDescription = "Snapped meal",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else if (selectedImagePathOrRes != null) {
                                Image(
                                    painter = rememberAsyncImagePainter(getDrawableId(selectedImagePathOrRes, context)),
                                    contentDescription = "Selected meal",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        Icons.Default.CameraAlt,
                                        contentDescription = "Trigger Camera",
                                        tint = Teal400,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Snap Food with Camera", color = Teal400, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("Simulates camera capture", color = Slate400, fontSize = 10.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            "Or choose from standard balanced meals:",
                            color = Slate400,
                            fontSize = 11.sp
                        )

                        // Preloaded list
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            preloadedFoods.forEach { food ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(64.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selectedImagePathOrRes == food.imageRes) Teal500.copy(alpha = 0.25f) else Slate800)
                                        .border(
                                            1.dp,
                                            if (selectedImagePathOrRes == food.imageRes) Teal400 else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            selectedImagePathOrRes = food.imageRes
                                            selectedBitmap = null
                                            foodDescriptionInput = food.name
                                        }
                                ) {
                                    Image(
                                        painter = rememberAsyncImagePainter(getDrawableId(food.imageRes, context)),
                                        contentDescription = food.name,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.4f))
                                    )
                                    Text(
                                        food.name,
                                        color = Color.White,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 10.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            "Step 2: Describe your food plate",
                            fontWeight = FontWeight.Bold,
                            color = Slate50,
                            fontSize = 14.sp
                        )

                        OutlinedTextField(
                            value = foodDescriptionInput,
                            onValueChange = { foodDescriptionInput = it },
                            placeholder = { Text("e.g., Avocado Sourdough toast with poached egg", color = Slate400) },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                                            putExtra(RecognizerIntent.EXTRA_PROMPT, "Describe what you ate...")
                                        }
                                        try {
                                            foodSpeechLauncher.launch(intent)
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Voice input not supported", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.testTag("voice_input_food_btn")
                                ) {
                                    Icon(Icons.Default.Mic, contentDescription = "Voice input", tint = Teal400)
                                }
                            },
                            modifier = Modifier.fillMaxWidth().testTag("food_desc_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Slate50,
                                unfocusedTextColor = Slate50,
                                focusedBorderColor = Teal400,
                                focusedLabelColor = Teal400
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (isAnalyzing) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(color = Teal400)
                                Text("NutriSnap scanning macros & micros...", color = Teal400, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = {
                                    if (foodDescriptionInput.isEmpty()) {
                                        Toast.makeText(context, "Please enter a description first", Toast.LENGTH_SHORT).show()
                                    } else {
                                        viewModel.analyzeFoodItem(foodDescriptionInput, selectedBitmap, selectedImagePathOrRes)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("scan_food_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = Teal400),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Slate950)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Scan with NutriSnap AI", color = Slate950, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class MockFoodItem(val name: String, val imageRes: String, val desc: String)

@Composable
fun MacroStatBox(name: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Slate800, RoundedCornerShape(10.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(name, color = Slate400, fontSize = 11.sp)
            Text(value, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun InsightsTab(viewModel: HealthViewModel) {
    var selectedMetric by remember { mutableStateOf("CALORIES") } // "CALORIES", "WATER", "CAFFEINE", "EXERCISE"

    val weeklyData = viewModel.getWeeklyChartData()
    val personalizedInsights by viewModel.personalizedInsights.collectAsStateWithLifecycle()
    val isGeneratingInsights by viewModel.isGeneratingInsights.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    "Insights & Progress",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "Track macro consumption and exercise trends over the last 7 days",
                    style = MaterialTheme.typography.labelMedium,
                    color = Slate400
                )
            }
        }

        // Chart Metric Selectors
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    MetricSelectorOption("Fuel", "CALORIES", Amber400),
                    MetricSelectorOption("Hydration", "WATER", Teal400),
                    MetricSelectorOption("Energy", "CAFFEINE", Amber500),
                    MetricSelectorOption("Exercise", "EXERCISE", Indigo400)
                ).forEach { opt ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                if (selectedMetric == opt.key) opt.color.copy(alpha = 0.2f) else Slate900,
                                RoundedCornerShape(10.dp)
                            )
                            .border(
                                1.dp,
                                if (selectedMetric == opt.key) opt.color else Color.Transparent,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedMetric = opt.key }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            opt.label,
                            color = if (selectedMetric == opt.key) opt.color else Slate50,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Render Custom Bar Chart
        item {
            val barColor = when (selectedMetric) {
                "CALORIES" -> Amber400
                "WATER" -> Teal400
                "CAFFEINE" -> Amber500
                else -> Indigo400
            }
            WeeklyBarChart(
                data = weeklyData,
                metricType = selectedMetric,
                barColor = barColor
            )
        }

        // AI Personalized Coach Insights Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Teal400,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Personalized AI Coach Report",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        if (isGeneratingInsights) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Teal400)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (personalizedInsights.isEmpty()) {
                        Text(
                            "Analyzing data metrics. Get daily updates on nutrition tips...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Slate400
                        )
                    } else {
                        Text(
                            personalizedInsights,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Slate50,
                            lineHeight = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.generateWeeklyInsights() },
                        colors = ButtonDefaults.buttonColors(containerColor = Teal400),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("regenerate_report_btn")
                    ) {
                        Text("Regenerate AI Report", color = Slate950, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

data class MetricSelectorOption(val label: String, val key: String, val color: Color)

@Composable
fun GoalsTab(viewModel: HealthViewModel) {
    val goals by viewModel.goals.collectAsStateWithLifecycle()

    val targetWater = viewModel.getGoalValue("water_ml", 2000.0)
    val targetCaffeine = viewModel.getGoalValue("caffeine_mg", 400.0)
    val targetCalories = viewModel.getGoalValue("calories_kcal", 2000.0)
    val targetProtein = viewModel.getGoalValue("protein_g", 120.0)
    val targetFat = viewModel.getGoalValue("fat_g", 70.0)
    val targetCarbs = viewModel.getGoalValue("carbs_g", 250.0)
    val targetExercise = viewModel.getGoalValue("exercise_min", 30.0)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    "Goal Settings",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "Personalize daily targets for both fitness and nutrition logs",
                    style = MaterialTheme.typography.labelMedium,
                    color = Slate400
                )
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Daily Hydration & Energy Targets",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )

                    GoalAdjusterItem(
                        title = "Hydration Goal (ml)",
                        value = targetWater,
                        range = 500.0..5000.0,
                        step = 100.0,
                        onValueChange = { viewModel.updateGoal("water_ml", it) }
                    )

                    GoalAdjusterItem(
                        title = "Caffeine Limit (mg)",
                        value = targetCaffeine,
                        range = 0.0..1000.0,
                        step = 20.0,
                        onValueChange = { viewModel.updateGoal("caffeine_mg", it) }
                    )

                    GoalAdjusterItem(
                        title = "Exercise Target (mins)",
                        value = targetExercise,
                        range = 10.0..180.0,
                        step = 5.0,
                        onValueChange = { viewModel.updateGoal("exercise_min", it) }
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Daily Nutrition Target Goals",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )

                    GoalAdjusterItem(
                        title = "Calories (kcal)",
                        value = targetCalories,
                        range = 1000.0..5000.0,
                        step = 50.0,
                        onValueChange = { viewModel.updateGoal("calories_kcal", it) }
                    )

                    GoalAdjusterItem(
                        title = "Protein Goal (g)",
                        value = targetProtein,
                        range = 30.0..300.0,
                        step = 5.0,
                        onValueChange = { viewModel.updateGoal("protein_g", it) }
                    )

                    GoalAdjusterItem(
                        title = "Carbs Goal (g)",
                        value = targetCarbs,
                        range = 50.0..600.0,
                        step = 10.0,
                        onValueChange = { viewModel.updateGoal("carbs_g", it) }
                    )

                    GoalAdjusterItem(
                        title = "Fat Goal (g)",
                        value = targetFat,
                        range = 20.0..200.0,
                        step = 5.0,
                        onValueChange = { viewModel.updateGoal("fat_g", it) }
                    )
                }
            }
        }
    }
}

@Composable
fun GoalAdjusterItem(
    title: String,
    value: Double,
    range: ClosedRange<Double>,
    step: Double,
    onValueChange: (Double) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, color = Slate50, fontSize = 13.sp)
            Text("${value.toInt()}", color = Teal400, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { onValueChange((value - step).coerceAtLeast(range.start)) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Decrease", tint = Slate400)
            }
            Slider(
                value = value.toFloat(),
                onValueChange = { onValueChange(it.toDouble()) },
                valueRange = range.start.toFloat()..range.endInclusive.toFloat(),
                colors = SliderDefaults.colors(
                    thumbColor = Teal400,
                    activeTrackColor = Teal400,
                    inactiveTrackColor = Slate800
                ),
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { onValueChange((value + step).coerceAtMost(range.endInclusive)) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.AddCircleOutline, contentDescription = "Increase", tint = Slate400)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExplorerCompositeScreen(viewModel: HealthViewModel) {
    var selectedTopTab by remember { mutableStateOf(0) }
    
    Column(modifier = Modifier.fillMaxSize().background(Slate950)) {
        TabRow(
            selectedTabIndex = selectedTopTab,
            containerColor = Slate950,
            contentColor = Teal400,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTopTab]),
                    color = Teal400
                )
            }
        ) {
            Tab(
                selected = selectedTopTab == 0,
                onClick = { selectedTopTab = 0 },
                text = { Text("MuscleWiki", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                unselectedContentColor = Slate400,
                selectedContentColor = Teal400
            )
            Tab(
                selected = selectedTopTab == 1,
                onClick = { selectedTopTab = 1 },
                text = { Text("AI Insights", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                unselectedContentColor = Slate400,
                selectedContentColor = Teal400
            )
            Tab(
                selected = selectedTopTab == 2,
                onClick = { selectedTopTab = 2 },
                text = { Text("Set Goals", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                unselectedContentColor = Slate400,
                selectedContentColor = Teal400
            )
        }
        
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTopTab) {
                0 -> MuscleWikiScreen()
                1 -> InsightsTab(viewModel)
                2 -> GoalsTab(viewModel)
            }
        }
    }
}
