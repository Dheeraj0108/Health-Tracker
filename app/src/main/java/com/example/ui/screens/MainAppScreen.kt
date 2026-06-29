package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import android.speech.RecognizerIntent
import android.content.Intent
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.shadow
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
import com.example.ui.components.*
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

    val googleUserSignedIn by viewModel.googleUserSignedIn.collectAsStateWithLifecycle()
    val googleUserName by viewModel.googleUserName.collectAsStateWithLifecycle()
    val weightUnit by viewModel.weightUnit.collectAsStateWithLifecycle()
    val gramsUnit by viewModel.gramsUnit.collectAsStateWithLifecycle()
    val volumeUnit by viewModel.volumeUnit.collectAsStateWithLifecycle()

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
                    var showSettingsDialog by remember { mutableStateOf(false) }

                    IconButton(
                        onClick = { showSettingsDialog = true },
                        modifier = Modifier.testTag("settings_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Teal400
                        )
                    }

                    if (showSettingsDialog) {
                        SettingsDialog(
                            viewModel = viewModel,
                            onDismiss = { showSettingsDialog = false }
                        )
                    }

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
                                leadingIcon = { Icon(Icons.Default.DarkMode, contentDescription = null, tint = Color(0xFFFFD700)) },
                                onClick = {
                                    AppThemeState.themeMode = "dark_gold"
                                    showThemeMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("White Theme", color = Slate50, fontWeight = FontWeight.SemiBold) },
                                leadingIcon = { Icon(Icons.Default.LightMode, contentDescription = null, tint = Color(0xFF0284C7)) },
                                onClick = {
                                    AppThemeState.themeMode = "white"
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
                .background(if (AppThemeState.themeMode == "white") Color(0xFFF1F5F9) else Slate950)
                .padding(innerPadding)
        ) {
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
                    targetState = selectedTab,
                    transitionSpec = {
                        (scaleIn(animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMediumLow)) + fadeIn(animationSpec = tween(220)))
                            .togetherWith(scaleOut(animationSpec = spring(dampingRatio = 0.85f)) + fadeOut(animationSpec = tween(180)))
                    },
                    modifier = Modifier.fillMaxSize()
                ) { targetTab ->
                    when (targetTab) {
                        0 -> DashboardTab(viewModel)
                        1 -> FitnessPlannerScreen(viewModel)
                        2 -> NutriSnapTab(viewModel)
                        3 -> BodyTrackerScreen(viewModel)
                        4 -> ExplorerCompositeScreen(viewModel)
                    }
                }
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

    val googleUserSignedIn by viewModel.googleUserSignedIn.collectAsStateWithLifecycle()
    val googleUserName by viewModel.googleUserName.collectAsStateWithLifecycle()
    val weightUnit by viewModel.weightUnit.collectAsStateWithLifecycle()
    val gramsUnit by viewModel.gramsUnit.collectAsStateWithLifecycle()
    val volumeUnit by viewModel.volumeUnit.collectAsStateWithLifecycle()

    var showAddExerciseDialog by remember { mutableStateOf(false) }
    var exerciseType by remember { mutableStateOf("GYM") } // "GYM", "RUN", "WALK"
    var gymName by remember { mutableStateOf("") }
    var gymSets by remember { mutableStateOf("3") }
    var gymReps by remember { mutableStateOf("10") }
    var durationMins by remember { mutableStateOf("30") }
    var distanceKm by remember { mutableStateOf("5.0") }

    var showLogSleepDialog by remember { mutableStateOf(false) }
    var sleepDurationStr by remember { mutableStateOf("8.0") }
    var sleepQuality by remember { mutableStateOf(4) }
    var sleepNotes by remember { mutableStateOf("") }

    var showWaterPopup by remember { mutableStateOf(false) }
    var showExercisePopup by remember { mutableStateOf(false) }
    var showCoffeePopup by remember { mutableStateOf(false) }
    var showFloatingShortcutBubble by remember { mutableStateOf(false) }
    var showQuickWorkoutConfetti by remember { mutableStateOf(false) }
    var showEditTargetsDialog by remember { mutableStateOf(false) }
    var quickWeightInput by remember { mutableStateOf("") }
    var quickNoteInput by remember { mutableStateOf("") }

    var quickWorkoutToggle by remember { mutableStateOf(false) }
    var quickActivityArchetype by remember { mutableStateOf("Gym") } // "Gym", "Walk", "Run"
    var quickWorkoutName by remember { mutableStateOf("") }
    var quickWorkoutWeight by remember { mutableStateOf("") }
    var quickWorkoutSets by remember { mutableStateOf(3) }
    var quickWorkoutReps by remember { mutableStateOf(10) }
    var quickWorkoutDuration by remember { mutableStateOf(30) }
    var quickWorkoutDistance by remember { mutableStateOf(2.5) }

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
    val todaySleep = viewModel.getTodaySleepDurationTotal()

    // Goals
    val goalWater = viewModel.getGoalValue("water_ml", 2000.0).toInt()
    val goalCaffeine = viewModel.getGoalValue("caffeine_mg", 400.0).toInt()
    val goalCalories = viewModel.getGoalValue("calories_kcal", 2000.0).toInt()
    val goalExercise = viewModel.getGoalValue("exercise_min", 30.0).toInt()
    val goalSleep = viewModel.getGoalValue("sleep_hours", 8.0)

    // Macros
    val todayProtein = viewModel.getTodayProteinTotal()
    val todayCarbs = viewModel.getTodayCarbsTotal()
    val todayFat = viewModel.getTodayFatTotal()

    val goalProtein = viewModel.getGoalValue("protein_g", 120.0)
    val goalCarbs = viewModel.getGoalValue("carbs_g", 250.0)
    val goalFat = viewModel.getGoalValue("fat_g", 70.0)

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp), // add bottom padding so FAB doesn't cover last item
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
                            text = if (googleUserSignedIn) "Hello, $googleUserName!" else "Good morning!",
                            style = MaterialTheme.typography.titleMedium,
                            color = Slate400
                        )
                        Text(
                            text = "Track Your Vitals",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .clickable { showEditTargetsDialog = true }
                                .padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Targets",
                                tint = Teal400,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Adjust Daily Targets",
                                style = MaterialTheme.typography.labelMedium,
                                color = Teal400,
                                fontWeight = FontWeight.Bold
                            )
                        }
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

        // All 4 progress circles in 1 Row with Fluid Donut Animation
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Water
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showWaterPopup = true },
                    colors = CardDefaults.cardColors(containerColor = Slate900),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Water", style = MaterialTheme.typography.labelSmall, color = Slate400, maxLines = 1)
                        com.example.ui.components.FluidDonutProgress(
                            progress = if (goalWater > 0) todayWater.toFloat() / goalWater else 0f,
                            color = Color(0xFF2196F3),
                            size = 56.dp
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalDrink,
                                contentDescription = "Water",
                                tint = Color(0xFF2196F3),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        val waterText = if (volumeUnit == "L") {
                            "${String.format(Locale.US, "%.1f", todayWater / 1000.0)}/${String.format(Locale.US, "%.1f", goalWater / 1000.0)}L"
                        } else {
                            "$todayWater/$goalWater ml"
                        }
                        Text(
                            waterText,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1
                        )
                    }
                }

                // Exercise
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showExercisePopup = true },
                    colors = CardDefaults.cardColors(containerColor = Slate900),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Exercise", style = MaterialTheme.typography.labelSmall, color = Slate400, maxLines = 1)
                        com.example.ui.components.FluidDonutProgress(
                            progress = if (goalExercise > 0) todayExercise.toFloat() / goalExercise else 0f,
                            color = Color(0xFFB7410E),
                            size = 56.dp
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsRun,
                                contentDescription = "Exercise",
                                tint = Color(0xFFB7410E),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            "$todayExercise/$goalExercise m",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1
                        )
                    }
                }

                // Caffeine
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showCoffeePopup = true },
                    colors = CardDefaults.cardColors(containerColor = Slate900),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Caffeine", style = MaterialTheme.typography.labelSmall, color = Slate400, maxLines = 1)
                        com.example.ui.components.FluidDonutProgress(
                            progress = if (goalCaffeine > 0) todayCaffeine.toFloat() / goalCaffeine else 0f,
                            color = Color(0xFF6F4E37),
                            size = 56.dp
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalCafe,
                                contentDescription = "Caffeine",
                                tint = Color(0xFF6F4E37),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            "$todayCaffeine/$goalCaffeine mg",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1
                        )
                    }
                }

                // Sleep
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showLogSleepDialog = true },
                    colors = CardDefaults.cardColors(containerColor = Slate900),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Sleep", style = MaterialTheme.typography.labelSmall, color = Slate400, maxLines = 1)
                        com.example.ui.components.FluidDonutProgress(
                            progress = if (goalSleep > 0) (todaySleep / goalSleep).toFloat() else 0f,
                            color = Color(0xFF9575CD),
                            size = 56.dp
                        ) {
                            Icon(
                                imageVector = Icons.Default.NightsStay,
                                contentDescription = "Sleep",
                                tint = Color(0xFF9575CD),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            "${String.format(Locale.US, "%.1f", todaySleep)}/${String.format(Locale.US, "%.1f", goalSleep)}h",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1
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
                            MacroProgressRow("Protein", todayProtein, goalProtein, Emerald500, gramsUnit)
                            // Carbs bar
                            MacroProgressRow("Carbs", todayCarbs, goalCarbs, Teal400, gramsUnit)
                            // Fat bar
                            MacroProgressRow("Fat", todayFat, goalFat, Amber400, gramsUnit)
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

        // 7-day trend line for water intake, caffeine consumption, and exercise duration
        item {
            val weeklyChartData = viewModel.getWeeklyChartData()
            com.example.ui.components.WeeklyTrendChart(
                data = weeklyChartData,
                goalWater = goalWater.toDouble(),
                goalCaffeine = goalCaffeine.toDouble(),
                goalExercise = goalExercise.toDouble(),
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }

    // Custom floating action bubble inside DashboardTab
    FloatingActionButton(
        onClick = { showFloatingShortcutBubble = true },
        containerColor = Teal500,
        contentColor = Slate950,
        shape = CircleShape,
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(16.dp)
            .testTag("floating_shortcut_bubble")
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Quick Shortcut",
            modifier = Modifier.size(28.dp)
        )
    }
}

// ---------------- NEW DIALOGS & POPUPS ----------------

if (showWaterPopup) {
    AlertDialog(
        onDismissRequest = { showWaterPopup = false },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.LocalDrink, contentDescription = null, tint = Teal400)
                Text("Hydration Window Pane", fontWeight = FontWeight.Bold, color = Slate50, fontSize = 18.sp)
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Tap a glass to fill or empty it. Each glass is 500 ml.",
                    color = Slate400,
                    fontSize = 13.sp
                )

                // 2x4 Glass Grid (Window pane style)
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (row in 0 until 2) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            for (col in 0 until 4) {
                                val index = row * 4 + col
                                val isGlassFilled = index < (todayWater / 500.0)

                                AnimatedGlass(
                                    isFilled = isGlassFilled,
                                    onClick = {
                                        val change = if (isGlassFilled) -500 else 500
                                        if (todayWater + change >= 0) {
                                            viewModel.logWater(change)
                                        } else {
                                            Toast.makeText(context, "Cannot reduce below 0 ml", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Text("Today's Total:", color = Slate300, fontSize = 14.sp)
                    Text(
                        "$todayWater / $goalWater ml",
                        color = Teal400,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { showWaterPopup = false },
                colors = ButtonDefaults.buttonColors(containerColor = Teal500)
            ) {
                Text("Close", color = Slate950, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Slate900
    )
}

if (showExercisePopup) {
    val stepsWalked by viewModel.stepsWalked.collectAsStateWithLifecycle()
    AlertDialog(
        onDismissRequest = { showExercisePopup = false },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.DirectionsRun, contentDescription = null, tint = Indigo400)
                Text("Exercise & Activity", fontWeight = FontWeight.Bold, color = Slate50, fontSize = 18.sp)
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Slate800),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Total Hours Logged", color = Slate400, fontSize = 11.sp)
                        Text(
                            String.format(Locale.US, "%.2f hours", todayExercise.toDouble() / 60.0),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            "Equivalent to $todayExercise minutes",
                            color = Slate300,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Slate800),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("WHO Recommended Baseline", color = Slate400, fontSize = 11.sp)
                            Text("90 mins", color = Indigo400, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        val baselineProgress = (todayExercise.toFloat() / 90f).coerceIn(0f, 1f)
                        LinearProgressIndicator(
                            progress = { baselineProgress },
                            color = Indigo400,
                            trackColor = Slate700,
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (todayExercise >= 90) "Daily activity baseline achieved! 🎉" else "You are ${90 - todayExercise} mins away from daily baseline.",
                            color = if (todayExercise >= 90) Emerald400 else Slate300,
                            fontSize = 11.sp
                        )
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Slate800),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.DirectionsWalk, contentDescription = null, tint = Emerald400, modifier = Modifier.size(16.dp))
                            Text("Pessimistic Step Counter", color = Slate400, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "$stepsWalked steps",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                        Text(
                            "Measurement derived directly from device accelerometer using conservative motion filters.",
                            color = Slate400,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.addMockSteps(50) },
                                colors = ButtonDefaults.buttonColors(containerColor = Slate900),
                                modifier = Modifier.weight(1f).height(32.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Simulate 50 Steps", fontSize = 10.sp, color = Color.White)
                            }
                            Button(
                                onClick = { viewModel.resetSteps() },
                                colors = ButtonDefaults.buttonColors(containerColor = Slate900),
                                modifier = Modifier.weight(0.7f).height(32.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Reset", fontSize = 10.sp, color = Crimson400)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { showExercisePopup = false },
                colors = ButtonDefaults.buttonColors(containerColor = Indigo400)
            ) {
                Text("Close", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Slate900
    )
}

if (showCoffeePopup) {
    AlertDialog(
        onDismissRequest = { showCoffeePopup = false },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.LocalCafe, contentDescription = null, tint = Amber400)
                Text("Caffeine Dashboard", fontWeight = FontWeight.Bold, color = Slate50, fontSize = 18.sp)
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Slate800),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Estimated Cups Consumed", color = Slate400, fontSize = 11.sp)
                        val cupsCount = todayCaffeine.toDouble() / 80.0
                        Text(
                            String.format(Locale.US, "%.1f Cups", cupsCount),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                        Text(
                            "Calculated using 80mg caffeine per standard 200ml cup.",
                            color = Slate300,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Slate800),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Caffeine Intake Status", color = Slate400, fontSize = 11.sp)
                            Text("WHO Limit: 400 mg", color = Amber400, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        val caffeineProgress = (todayCaffeine.toFloat() / 400f).coerceIn(0f, 1f)
                        LinearProgressIndicator(
                            progress = { caffeineProgress },
                            color = if (todayCaffeine > 400) Crimson500 else Amber400,
                            trackColor = Slate700,
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (todayCaffeine > 400) {
                                "Exceeded WHO limit of 400mg! Reduce intake. ⚠️"
                            } else {
                                "You have consumed $todayCaffeine mg. Safely within WHO limit."
                            },
                            color = if (todayCaffeine > 400) Crimson400 else Emerald400,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = Slate800),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Estimated Coffee Volume", color = Slate400, fontSize = 11.sp)
                        val estimatedVolumeMl = (todayCaffeine.toDouble() / 80.0) * 200.0
                        Text(
                            String.format(Locale.US, "%.0f ml", estimatedVolumeMl),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            "Total fluid volume associated with caffeine logs.",
                            color = Slate400,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { showCoffeePopup = false },
                colors = ButtonDefaults.buttonColors(containerColor = Amber400)
            ) {
                Text("Close", color = Slate950, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Slate900
    )
}

if (showFloatingShortcutBubble) {
    AlertDialog(
        onDismissRequest = { showFloatingShortcutBubble = false },
        title = {
            Text("Quick Logging Bubble", fontWeight = FontWeight.Bold, color = Slate50, fontSize = 18.sp)
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    Text("Quickly record your health metrics for today:", color = Slate400, fontSize = 13.sp)
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.logWater(250)
                                Toast.makeText(context, "+250ml Water Added", Toast.LENGTH_SHORT).show()
                                showFloatingShortcutBubble = false
                            },
                        colors = CardDefaults.cardColors(containerColor = Slate800)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(40.dp).background(Teal400.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.LocalDrink, contentDescription = null, tint = Teal400)
                            }
                            Column {
                                Text("+250ml Water", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                Text("Quickly add standard hydration glass", color = Slate400, fontSize = 11.sp)
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.logCaffeine(80)
                                Toast.makeText(context, "+80mg Caffeine Added", Toast.LENGTH_SHORT).show()
                                showFloatingShortcutBubble = false
                            },
                        colors = CardDefaults.cardColors(containerColor = Slate800)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(40.dp).background(Amber400.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.LocalCafe, contentDescription = null, tint = Amber400)
                            }
                            Column {
                                Text("+80mg Caffeine", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                Text("Quickly add cup of coffee", color = Slate400, fontSize = 11.sp)
                            }
                        }
                    }
                }

                // Upgraded Log Workout Toggle Section
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Slate800),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.size(40.dp).background(Indigo400.copy(alpha = 0.2f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = Indigo400)
                                    }
                                    Column {
                                        Text("Log Workout", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                        Text("Record real-time workout stats", color = Slate400, fontSize = 11.sp)
                                    }
                                }
                                Switch(
                                    checked = quickWorkoutToggle,
                                    onCheckedChange = { quickWorkoutToggle = it },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Teal400,
                                        checkedTrackColor = Teal400.copy(alpha = 0.4f),
                                        uncheckedThumbColor = Slate400,
                                        uncheckedTrackColor = Slate700
                                    ),
                                    modifier = Modifier.testTag("log_workout_toggle")
                                )
                            }

                            if (quickWorkoutToggle) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Choose your workout archetype to instantly track your metrics:",
                                    color = Teal400,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                // Interactive Menu to choose archetype: Gym, Walk, Run
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("Gym", "Walk", "Run").forEach { type ->
                                        val isSelected = quickActivityArchetype == type
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSelected) Teal400 else Slate900)
                                                .clickable { quickActivityArchetype = type }
                                                .padding(vertical = 8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = type,
                                                color = if (isSelected) Slate950 else Slate300,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Configuration Panels based on choice
                                if (quickActivityArchetype == "Gym") {
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        // Workout Name
                                        OutlinedTextField(
                                            value = quickWorkoutName,
                                            onValueChange = { quickWorkoutName = it },
                                            label = { Text("Workout Name") },
                                            placeholder = { Text("e.g. Bench Press") },
                                            textStyle = LocalTextStyle.current.copy(color = Color.White),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Teal400,
                                                unfocusedBorderColor = Slate700
                                            ),
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        // Weight Input
                                        OutlinedTextField(
                                            value = quickWorkoutWeight,
                                            onValueChange = { quickWorkoutWeight = it },
                                            label = { Text("Weight ($weightUnit)") },
                                            placeholder = { Text("e.g. 80.0") },
                                            textStyle = LocalTextStyle.current.copy(color = Color.White),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Teal400,
                                                unfocusedBorderColor = Slate700
                                            ),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        // Sets and Reps counters
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            // Sets Counter
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("Sets", color = Slate300, fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(Slate900, RoundedCornerShape(8.dp))
                                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    IconButton(
                                                        onClick = { if (quickWorkoutSets > 1) quickWorkoutSets-- },
                                                        modifier = Modifier.size(32.dp)
                                                    ) {
                                                        Icon(Icons.Default.Remove, contentDescription = "Decrease Sets", tint = Teal400)
                                                    }
                                                    Text(text = quickWorkoutSets.toString(), color = Color.White, fontWeight = FontWeight.Bold)
                                                    IconButton(
                                                        onClick = { quickWorkoutSets++ },
                                                        modifier = Modifier.size(32.dp)
                                                    ) {
                                                        Icon(Icons.Default.Add, contentDescription = "Increase Sets", tint = Teal400)
                                                    }
                                                }
                                            }

                                            // Reps Counter
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("Reps", color = Slate300, fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(Slate900, RoundedCornerShape(8.dp))
                                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    IconButton(
                                                        onClick = { if (quickWorkoutReps > 1) quickWorkoutReps-- },
                                                        modifier = Modifier.size(32.dp)
                                                    ) {
                                                        Icon(Icons.Default.Remove, contentDescription = "Decrease Reps", tint = Teal400)
                                                    }
                                                    Text(text = quickWorkoutReps.toString(), color = Color.White, fontWeight = FontWeight.Bold)
                                                    IconButton(
                                                        onClick = { quickWorkoutReps++ },
                                                        modifier = Modifier.size(32.dp)
                                                    ) {
                                                        Icon(Icons.Default.Add, contentDescription = "Increase Reps", tint = Teal400)
                                                    }
                                                }
                                            }
                                        }

                                        // Duration slider
                                        Column {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text("Duration", color = Slate300, fontSize = 12.sp)
                                                Text("$quickWorkoutDuration mins", color = Teal400, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }
                                            Slider(
                                                value = quickWorkoutDuration.toFloat(),
                                                onValueChange = { quickWorkoutDuration = it.toInt() },
                                                valueRange = 5f..180f,
                                                steps = 34,
                                                colors = SliderDefaults.colors(
                                                    thumbColor = Teal400,
                                                    activeTrackColor = Teal400
                                                )
                                            )
                                        }
                                    }
                                } else {
                                    // Walk or Run Configuration Panel
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        // Distance entry
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Distance", color = Slate300, fontSize = 12.sp)
                                            Text(String.format(Locale.US, "%.1f km", quickWorkoutDistance), color = Teal400, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                        Slider(
                                            value = quickWorkoutDistance.toFloat(),
                                            onValueChange = { quickWorkoutDistance = Math.round(it * 10) / 10.0 },
                                            valueRange = 0.5f..25f,
                                            colors = SliderDefaults.colors(
                                                thumbColor = Teal400,
                                                activeTrackColor = Teal400
                                            )
                                        )

                                        // Duration Entry
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Duration", color = Slate300, fontSize = 12.sp)
                                            Text("$quickWorkoutDuration mins", color = Teal400, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                        Slider(
                                            value = quickWorkoutDuration.toFloat(),
                                            onValueChange = { quickWorkoutDuration = it.toInt() },
                                            valueRange = 5f..120f,
                                            steps = 22,
                                            colors = SliderDefaults.colors(
                                                thumbColor = Teal400,
                                                activeTrackColor = Teal400
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Save Activity button
                                Button(
                                    onClick = {
                                        if (quickActivityArchetype == "Gym") {
                                            if (quickWorkoutName.isBlank()) {
                                                Toast.makeText(context, "Please enter a workout name!", Toast.LENGTH_SHORT).show()
                                                return@Button
                                            }
                                            val wt = quickWorkoutWeight.toDoubleOrNull() ?: 0.0
                                            viewModel.logExercise(
                                                type = "Gym",
                                                exerciseName = quickWorkoutName,
                                                sets = quickWorkoutSets,
                                                reps = quickWorkoutReps,
                                                durationMinutes = quickWorkoutDuration,
                                                distanceKm = null
                                            )
                                            
                                            // Handle personal record check if weight is entered
                                            if (wt > 0.0) {
                                                viewModel.checkAndLogPersonalRecord(
                                                    exerciseName = quickWorkoutName,
                                                    weight = wt,
                                                    reps = quickWorkoutReps,
                                                    onNewRecord = {
                                                        showQuickWorkoutConfetti = true
                                                    }
                                                )
                                            }
                                            
                                            showQuickWorkoutConfetti = true // Give congratulations confetti feedback
                                            Toast.makeText(context, "Workout logged successfully! Congratulations! 🎉", Toast.LENGTH_LONG).show()
                                        } else {
                                            viewModel.logExercise(
                                                type = quickActivityArchetype,
                                                exerciseName = null,
                                                sets = null,
                                                reps = null,
                                                durationMinutes = quickWorkoutDuration,
                                                distanceKm = quickWorkoutDistance
                                            )
                                            showQuickWorkoutConfetti = true // Give congratulations confetti feedback
                                            Toast.makeText(context, "$quickActivityArchetype logged successfully! Great job! 🎉", Toast.LENGTH_LONG).show()
                                        }
                                        
                                        // Reset
                                        quickWorkoutName = ""
                                        quickWorkoutWeight = ""
                                        quickWorkoutToggle = false
                                        showFloatingShortcutBubble = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Teal400),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("save_quick_workout_btn")
                                ) {
                                    Text("Save Workout Session", color = Slate950, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showFloatingShortcutBubble = false
                                showLogSleepDialog = true
                            },
                        colors = CardDefaults.cardColors(containerColor = Slate800)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(40.dp).background(Emerald400.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.NightsStay, contentDescription = null, tint = Emerald400)
                            }
                            Column {
                                Text("Log Sleep", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                                Text("Record hours slept, quality and notes", color = Slate400, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { showFloatingShortcutBubble = false }) {
                Text("Dismiss", color = Slate400, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Slate900
    )
}

ConfettiOverlay(
    isTriggered = showQuickWorkoutConfetti,
    onFinished = { showQuickWorkoutConfetti = false }
)

    if (showLogSleepDialog) {
        AlertDialog(
            onDismissRequest = { showLogSleepDialog = false },
            title = { Text("Log Daily Sleep", fontWeight = FontWeight.Bold, color = Slate50) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sleep Duration (hours):", color = Slate400, fontSize = 12.sp)
                    OutlinedTextField(
                        value = sleepDurationStr,
                        onValueChange = { sleepDurationStr = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Emerald400,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    // Quick duration presets
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("6.0", "7.0", "8.0", "9.0").forEach { h ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (sleepDurationStr == h) Emerald500 else Slate800,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { sleepDurationStr = h }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "$h h",
                                    color = if (sleepDurationStr == h) Slate950 else Slate50,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text("Sleep Quality Rating:", color = Slate400, fontSize = 12.sp)
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        (1..5).forEach { starIndex ->
                            IconButton(
                                onClick = { sleepQuality = starIndex },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (starIndex <= sleepQuality) Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = "$starIndex Star",
                                    tint = if (starIndex <= sleepQuality) Amber400 else Slate600,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    val qualityLabel = when (sleepQuality) {
                        1 -> "Poor"
                        2 -> "Fair"
                        3 -> "Good"
                        4 -> "Very Good"
                        5 -> "Excellent"
                        else -> "Good"
                    }
                    Text(
                        text = "Quality: $qualityLabel",
                        color = Emerald400,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        value = sleepNotes,
                        onValueChange = { sleepNotes = it },
                        label = { Text("Sleep Notes (optional)") },
                        placeholder = { Text("e.g. Slept deep, woke up refreshed", color = Slate400) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Emerald400,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val duration = sleepDurationStr.toDoubleOrNull()
                        if (duration != null && duration > 0) {
                            viewModel.logSleep(duration, sleepQuality, sleepNotes.takeIf { it.isNotEmpty() })
                            Toast.makeText(context, "Sleep logged successfully!", Toast.LENGTH_SHORT).show()
                            showLogSleepDialog = false
                        } else {
                            Toast.makeText(context, "Please enter a valid sleep duration", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald500)
                ) {
                    Text("Save", color = Slate950, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogSleepDialog = false }) {
                    Text("Cancel", color = Slate400)
                }
            },
            containerColor = Slate900
        )
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

    if (showEditTargetsDialog) {
        var tempWater by remember { mutableStateOf(goalWater.toDouble()) }
        var tempCaffeine by remember { mutableStateOf(goalCaffeine.toDouble()) }
        var tempExercise by remember { mutableStateOf(goalExercise.toDouble()) }
        var tempSleep by remember { mutableStateOf(goalSleep) }

        AlertDialog(
            onDismissRequest = { showEditTargetsDialog = false },
            title = {
                Text(
                    text = "Adjust Daily Targets",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    GoalAdjusterItem(
                        title = "Hydration Goal (ml)",
                        value = tempWater,
                        range = 500.0..5000.0,
                        step = 100.0,
                        onValueChange = { tempWater = it }
                    )

                    GoalAdjusterItem(
                        title = "Caffeine Limit (mg)",
                        value = tempCaffeine,
                        range = 0.0..1000.0,
                        step = 20.0,
                        onValueChange = { tempCaffeine = it }
                    )

                    GoalAdjusterItem(
                        title = "Exercise Target (mins)",
                        value = tempExercise,
                        range = 10.0..180.0,
                        step = 5.0,
                        onValueChange = { tempExercise = it }
                    )

                    GoalAdjusterItem(
                        title = "Sleep Target (hours)",
                        value = tempSleep,
                        range = 4.0..12.0,
                        step = 0.5,
                        onValueChange = { tempSleep = it }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateGoal("water_ml", tempWater)
                        viewModel.updateGoal("caffeine_mg", tempCaffeine)
                        viewModel.updateGoal("exercise_min", tempExercise)
                        viewModel.updateGoal("sleep_hours", tempSleep)
                        android.widget.Toast.makeText(context, "Targets updated successfully!", android.widget.Toast.LENGTH_SHORT).show()
                        showEditTargetsDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Teal500)
                ) {
                    Text("Save & Close", color = Slate950, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Slate900,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun MacroProgressRow(name: String, current: Double, target: Double, color: Color, gramsUnit: String = "g") {
    val progress = if (target > 0) (current / target).toFloat().coerceIn(0f, 1f) else 0f
    
    val currentDisplay = if (gramsUnit == "kg") current / 1000.0 else current
    val targetDisplay = if (gramsUnit == "kg") target / 1000.0 else target
    val unitStr = if (gramsUnit == "kg") "kg" else "g"
    val formatStr = if (gramsUnit == "kg") "%.3f" else "%.0f"
    val textVal = "${String.format(Locale.US, formatStr, currentDisplay)}$unitStr / ${String.format(Locale.US, formatStr, targetDisplay)}$unitStr"

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(name, fontSize = 11.sp, color = Slate50, fontWeight = FontWeight.Bold)
            Text(textVal, fontSize = 10.sp, color = Slate400)
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
    var foodItemsList by remember { mutableStateOf(listOf("")) }
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
                foodItemsList = listOf(spokenText)
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
            foodItemsList = listOf("Captured Meal photo")
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
                                onClick = {
                                    viewModel.cancelFoodAnalysis()
                                    foodItemsList = listOf("")
                                    foodDescriptionInput = ""
                                },
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
                                    foodItemsList = listOf("")
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

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            foodItemsList.forEachIndexed { index, itemText ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = itemText,
                                        onValueChange = { newValue ->
                                            val newList = foodItemsList.toMutableList()
                                            newList[index] = newValue
                                            foodItemsList = newList
                                            foodDescriptionInput = newList.filter { it.isNotBlank() }.joinToString(", ")
                                        },
                                        placeholder = { Text("e.g., Item ${index + 1} (e.g., Avocado Toast)", color = Slate400, fontSize = 12.sp) },
                                        modifier = Modifier.weight(1f).testTag("food_desc_input_$index"),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = Slate50,
                                            unfocusedTextColor = Slate50,
                                            focusedBorderColor = Teal400,
                                            focusedLabelColor = Teal400
                                        ),
                                        trailingIcon = {
                                            if (index == 0) {
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
                                            }
                                        }
                                    )

                                    if (foodItemsList.size > 1) {
                                        IconButton(
                                            onClick = {
                                                val newList = foodItemsList.toMutableList()
                                                newList.removeAt(index)
                                                foodItemsList = newList
                                                foodDescriptionInput = newList.filter { it.isNotBlank() }.joinToString(", ")
                                            },
                                            modifier = Modifier.size(36.dp).testTag("remove_food_row_btn_$index")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.RemoveCircleOutline,
                                                contentDescription = "Remove item",
                                                tint = Crimson400
                                            )
                                        }
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = {
                                        foodItemsList = foodItemsList + ""
                                    },
                                    modifier = Modifier.testTag("add_food_row_btn")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = Teal400, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Add Food Item", color = Teal400, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

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
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(listOf(
                    MetricSelectorOption("Fuel", "CALORIES", Amber400),
                    MetricSelectorOption("Hydration", "WATER", Teal400),
                    MetricSelectorOption("Energy", "CAFFEINE", Amber500),
                    MetricSelectorOption("Exercise", "EXERCISE", Indigo400),
                    MetricSelectorOption("Sleep", "SLEEP", Emerald400),
                    MetricSelectorOption("Sleep Q", "SLEEP_QUALITY", Crimson400)
                )) { opt ->
                    Box(
                        modifier = Modifier
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
                            .padding(horizontal = 12.dp, vertical = 8.dp),
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
                "SLEEP" -> Emerald400
                "SLEEP_QUALITY" -> Crimson400
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
    val context = LocalContext.current

    var tempWater by remember(goals) { mutableStateOf(viewModel.getGoalValue("water_ml", 2000.0)) }
    var tempCaffeine by remember(goals) { mutableStateOf(viewModel.getGoalValue("caffeine_mg", 400.0)) }
    var tempCalories by remember(goals) { mutableStateOf(viewModel.getGoalValue("calories_kcal", 2000.0)) }
    var tempProtein by remember(goals) { mutableStateOf(viewModel.getGoalValue("protein_g", 120.0)) }
    var tempFat by remember(goals) { mutableStateOf(viewModel.getGoalValue("fat_g", 70.0)) }
    var tempCarbs by remember(goals) { mutableStateOf(viewModel.getGoalValue("carbs_g", 250.0)) }
    var tempExercise by remember(goals) { mutableStateOf(viewModel.getGoalValue("exercise_min", 30.0)) }
    var tempSleep by remember(goals) { mutableStateOf(viewModel.getGoalValue("sleep_hours", 8.0)) }

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
                        value = tempWater,
                        range = 500.0..5000.0,
                        step = 100.0,
                        onValueChange = { tempWater = it }
                    )

                    GoalAdjusterItem(
                        title = "Caffeine Limit (mg)",
                        value = tempCaffeine,
                        range = 0.0..1000.0,
                        step = 20.0,
                        onValueChange = { tempCaffeine = it }
                    )

                    GoalAdjusterItem(
                        title = "Exercise Target (mins)",
                        value = tempExercise,
                        range = 10.0..180.0,
                        step = 5.0,
                        onValueChange = { tempExercise = it }
                    )

                    GoalAdjusterItem(
                        title = "Sleep Target (hours)",
                        value = tempSleep,
                        range = 4.0..12.0,
                        step = 0.5,
                        onValueChange = { tempSleep = it }
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
                        value = tempCalories,
                        range = 1000.0..5000.0,
                        step = 50.0,
                        onValueChange = { tempCalories = it }
                    )

                    GoalAdjusterItem(
                        title = "Protein Goal (g)",
                        value = tempProtein,
                        range = 30.0..300.0,
                        step = 5.0,
                        onValueChange = { tempProtein = it }
                    )

                    GoalAdjusterItem(
                        title = "Carbs Goal (g)",
                        value = tempCarbs,
                        range = 50.0..600.0,
                        step = 10.0,
                        onValueChange = { tempCarbs = it }
                    )

                    GoalAdjusterItem(
                        title = "Fat Goal (g)",
                        value = tempFat,
                        range = 20.0..200.0,
                        step = 5.0,
                        onValueChange = { tempFat = it }
                    )
                }
            }
        }

        item {
            Button(
                onClick = {
                    viewModel.updateGoal("water_ml", tempWater)
                    viewModel.updateGoal("caffeine_mg", tempCaffeine)
                    viewModel.updateGoal("calories_kcal", tempCalories)
                    viewModel.updateGoal("protein_g", tempProtein)
                    viewModel.updateGoal("fat_g", tempFat)
                    viewModel.updateGoal("carbs_g", tempCarbs)
                    viewModel.updateGoal("exercise_min", tempExercise)
                    viewModel.updateGoal("sleep_hours", tempSleep)
                    android.widget.Toast.makeText(context, "All goals saved successfully!", android.widget.Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Teal400),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text("Save Target Changes", color = Slate950, fontWeight = FontWeight.Bold)
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
                text = { Text("Exercise Explorer", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                unselectedContentColor = Slate400,
                selectedContentColor = Teal400
            )
            Tab(
                selected = selectedTopTab == 1,
                onClick = { selectedTopTab = 1 },
                text = { Text("AI Insights", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                unselectedContentColor = Slate400,
                selectedContentColor = Teal400
            )
            Tab(
                selected = selectedTopTab == 2,
                onClick = { selectedTopTab = 2 },
                text = { Text("Logs & Charts", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                unselectedContentColor = Slate400,
                selectedContentColor = Teal400
            )
        }
        
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .shadow(8.dp, shape = RoundedCornerShape(16.dp), clip = true),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (AppThemeState.themeMode == "white") Color.White else Slate900
            ),
            border = BorderStroke(1.dp, if (AppThemeState.themeMode == "white") Color(0xFFE2E8F0) else Slate800)
        ) {
            AnimatedContent(
                targetState = selectedTopTab,
                transitionSpec = {
                    (scaleIn(animationSpec = spring(dampingRatio = 0.65f, stiffness = Spring.StiffnessMediumLow)) + fadeIn(animationSpec = tween(220)))
                        .togetherWith(scaleOut(animationSpec = spring(dampingRatio = 0.85f)) + fadeOut(animationSpec = tween(180)))
                },
                modifier = Modifier.fillMaxSize()
            ) { targetSubTab ->
                when (targetSubTab) {
                    0 -> MuscleWikiScreen(viewModel)
                    1 -> InsightsTab(viewModel)
                    2 -> LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            WorkoutProgressCharts(viewModel)
                        }
                        item {
                            Text(
                                text = "Activity History",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Slate50,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        item {
                            Box(modifier = Modifier.fillMaxWidth().heightIn(max = 600.dp)) {
                                LogsTab(viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun <T> SegmentedControl(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    labelProvider: (T) -> String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Slate800)
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        items.forEach { item ->
            val selected = item == selectedItem
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selected) Teal400 else Color.Transparent)
                    .clickable { onItemSelected(item) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = labelProvider(item),
                    color = if (selected) Slate950 else Slate300,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun GoogleAccountSelectorDialog(
    onDismiss: () -> Unit,
    onSelect: (name: String, email: String, photo: String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "G",
                    color = Teal400,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Sign in with Google", fontWeight = FontWeight.Bold, color = Slate50, fontSize = 18.sp)
                Text("Choose an account to continue to Health Tracker", color = Slate400, fontSize = 12.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelect("Dheeraj Redhut", "dheeraj.redhut@gmail.com", "img_avatar_dheeraj")
                        },
                    colors = CardDefaults.cardColors(containerColor = Slate800)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Teal500, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("D", color = Slate950, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Column {
                            Text("Dheeraj Redhut", color = Slate50, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("dheeraj.redhut@gmail.com", color = Slate400, fontSize = 12.sp)
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSelect("AI Studio Builder", "developer@aistudio.com", "img_avatar_developer")
                        },
                    colors = CardDefaults.cardColors(containerColor = Slate800)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Indigo400, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("A", color = Slate950, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Column {
                            Text("AI Studio Builder", color = Slate50, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("developer@aistudio.com", color = Slate400, fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Slate400)
            }
        },
        containerColor = Slate900
    )
}

@Composable
fun SettingsDialog(
    viewModel: HealthViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val currentAge by viewModel.userAge.collectAsStateWithLifecycle()
    val currentWeight by viewModel.userWeight.collectAsStateWithLifecycle()
    val currentWaist by viewModel.userWaist.collectAsStateWithLifecycle()
    val currentChest by viewModel.userChest.collectAsStateWithLifecycle()
    val currentHips by viewModel.userHips.collectAsStateWithLifecycle()
    val currentBiceps by viewModel.userBiceps.collectAsStateWithLifecycle()
    val currentThighs by viewModel.userThighs.collectAsStateWithLifecycle()

    val weightUnit by viewModel.weightUnit.collectAsStateWithLifecycle()
    val gramsUnit by viewModel.gramsUnit.collectAsStateWithLifecycle()
    val volumeUnit by viewModel.volumeUnit.collectAsStateWithLifecycle()

    val googleUserSignedIn by viewModel.googleUserSignedIn.collectAsStateWithLifecycle()
    val googleUserName by viewModel.googleUserName.collectAsStateWithLifecycle()
    val googleUserEmail by viewModel.googleUserEmail.collectAsStateWithLifecycle()

    val goals by viewModel.goals.collectAsStateWithLifecycle()

    var tempWater by remember(goals) { mutableStateOf(viewModel.getGoalValue("water_ml", 2000.0)) }
    var tempCaffeine by remember(goals) { mutableStateOf(viewModel.getGoalValue("caffeine_mg", 400.0)) }
    var tempCalories by remember(goals) { mutableStateOf(viewModel.getGoalValue("calories_kcal", 2000.0)) }
    var tempProtein by remember(goals) { mutableStateOf(viewModel.getGoalValue("protein_g", 120.0)) }
    var tempFat by remember(goals) { mutableStateOf(viewModel.getGoalValue("fat_g", 70.0)) }
    var tempCarbs by remember(goals) { mutableStateOf(viewModel.getGoalValue("carbs_g", 250.0)) }
    var tempExercise by remember(goals) { mutableStateOf(viewModel.getGoalValue("exercise_min", 30.0)) }
    var tempSleep by remember(goals) { mutableStateOf(viewModel.getGoalValue("sleep_hours", 8.0)) }

    var ageStr by remember(currentAge) { mutableStateOf(currentAge.toString()) }
    var weightStr by remember(currentWeight) { mutableStateOf(String.format(Locale.US, "%.1f", currentWeight)) }
    var waistStr by remember(currentWaist) { mutableStateOf(String.format(Locale.US, "%.1f", currentWaist)) }
    var chestStr by remember(currentChest) { mutableStateOf(String.format(Locale.US, "%.1f", currentChest)) }
    var hipsStr by remember(currentHips) { mutableStateOf(String.format(Locale.US, "%.1f", currentHips)) }
    var bicepsStr by remember(currentBiceps) { mutableStateOf(String.format(Locale.US, "%.1f", currentBiceps)) }
    var thighsStr by remember(currentThighs) { mutableStateOf(String.format(Locale.US, "%.1f", currentThighs)) }

    var showGoogleAccountSelector by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Settings & Profile", fontWeight = FontWeight.Bold, color = Slate50)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate400)
                }
            }
        },
        text = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Google Authentication", style = MaterialTheme.typography.titleSmall, color = Teal400, fontWeight = FontWeight.Bold)
                        if (googleUserSignedIn) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Slate800),
                                border = BorderStroke(1.dp, Teal400.copy(alpha = 0.3f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .background(Teal500, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(googleUserName.firstOrNull()?.toString() ?: "U", color = Slate950, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                        }
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(googleUserName, color = Slate50, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text(googleUserEmail, color = Slate400, fontSize = 11.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = {
                                            viewModel.signOutFromGoogle()
                                            Toast.makeText(context, "Signed out successfully", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Crimson500),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth().height(36.dp).testTag("sign_out_google_btn")
                                    ) {
                                        Text("Sign Out", color = Slate50, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        } else {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showGoogleAccountSelector = true }
                                    .testTag("sign_in_google_btn"),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Text(
                                            "G",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 18.sp,
                                            color = Color(0xFF4285F4)
                                        )
                                        Text(
                                            "Sign in with Google",
                                            color = Color(0xFF1F1F1F),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Personal Vitals", style = MaterialTheme.typography.titleSmall, color = Teal400, fontWeight = FontWeight.Bold)
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = ageStr,
                                onValueChange = { ageStr = it },
                                label = { Text("Age") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Teal400,
                                    focusedTextColor = Slate50,
                                    unfocusedTextColor = Slate50
                                ),
                                modifier = Modifier.weight(1f).testTag("settings_age_input")
                            )
                            
                            OutlinedTextField(
                                value = weightStr,
                                onValueChange = { weightStr = it },
                                label = { Text("Weight ($weightUnit)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Teal400,
                                    focusedTextColor = Slate50,
                                    unfocusedTextColor = Slate50
                                ),
                                modifier = Modifier.weight(1f).testTag("settings_weight_input")
                            )
                        }
                    }
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Body Measurements (cm)", style = MaterialTheme.typography.titleSmall, color = Teal400, fontWeight = FontWeight.Bold)
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = chestStr,
                                onValueChange = { chestStr = it },
                                label = { Text("Chest") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Teal400,
                                    focusedTextColor = Slate50,
                                    unfocusedTextColor = Slate50
                                ),
                                modifier = Modifier.weight(1f).testTag("settings_chest_input")
                            )
                            OutlinedTextField(
                                value = waistStr,
                                onValueChange = { waistStr = it },
                                label = { Text("Waist") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Teal400,
                                    focusedTextColor = Slate50,
                                    unfocusedTextColor = Slate50
                                ),
                                modifier = Modifier.weight(1f).testTag("settings_waist_input")
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = hipsStr,
                                onValueChange = { hipsStr = it },
                                label = { Text("Hips") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Teal400,
                                    focusedTextColor = Slate50,
                                    unfocusedTextColor = Slate50
                                ),
                                modifier = Modifier.weight(1f).testTag("settings_hips_input")
                            )
                            OutlinedTextField(
                                value = bicepsStr,
                                onValueChange = { bicepsStr = it },
                                label = { Text("Biceps") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Teal400,
                                    focusedTextColor = Slate50,
                                    unfocusedTextColor = Slate50
                                ),
                                modifier = Modifier.weight(1f).testTag("settings_biceps_input")
                            )
                        }

                        OutlinedTextField(
                            value = thighsStr,
                            onValueChange = { thighsStr = it },
                            label = { Text("Thighs") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Teal400,
                                focusedTextColor = Slate50,
                                unfocusedTextColor = Slate50
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("settings_thighs_input")
                        )
                    }
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Unit Preferences", style = MaterialTheme.typography.titleSmall, color = Teal400, fontWeight = FontWeight.Bold)
                        
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Column {
                                Text("Weight Unit", color = Slate400, fontSize = 11.sp, modifier = Modifier.padding(bottom = 4.dp))
                                SegmentedControl(
                                    items = listOf("kg", "lb"),
                                    selectedItem = weightUnit,
                                    onItemSelected = { viewModel.updateWeightUnit(it) },
                                    labelProvider = { it.uppercase() }
                                )
                            }

                            Text(
                                "Toggling converts displayed body weights between Kilograms and Pounds dynamically.",
                                color = Slate500,
                                fontSize = 10.sp
                            )

                            Column {
                                Text("Macros Unit", color = Slate400, fontSize = 11.sp, modifier = Modifier.padding(bottom = 4.dp))
                                SegmentedControl(
                                    items = listOf("g", "kg"),
                                    selectedItem = gramsUnit,
                                    onItemSelected = { viewModel.updateGramsUnit(it) },
                                    labelProvider = { if (it == "g") "Grams (g)" else "Kilograms (kg)" }
                                )
                            }

                            Column {
                                Text("Liquid Volume Unit", color = Slate400, fontSize = 11.sp, modifier = Modifier.padding(bottom = 4.dp))
                                SegmentedControl(
                                    items = listOf("ml", "L"),
                                    selectedItem = volumeUnit,
                                    onItemSelected = { viewModel.updateVolumeUnit(it) },
                                    labelProvider = { if (it == "ml") "Milliliters (ml)" else "Liters (L)" }
                                )
                            }
                        }
                    }
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Set Goals (Nutrition & Energy)", style = MaterialTheme.typography.titleSmall, color = Teal400, fontWeight = FontWeight.Bold)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Slate800),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                GoalAdjusterItem(
                                    title = "Calories (kcal)",
                                    value = tempCalories,
                                    range = 1000.0..5000.0,
                                    step = 50.0,
                                    onValueChange = { tempCalories = it }
                                )
                                GoalAdjusterItem(
                                    title = "Protein Goal (g)",
                                    value = tempProtein,
                                    range = 30.0..300.0,
                                    step = 5.0,
                                    onValueChange = { tempProtein = it }
                                )
                                GoalAdjusterItem(
                                    title = "Carbs Goal (g)",
                                    value = tempCarbs,
                                    range = 50.0..600.0,
                                    step = 10.0,
                                    onValueChange = { tempCarbs = it }
                                )
                                GoalAdjusterItem(
                                    title = "Fat Goal (g)",
                                    value = tempFat,
                                    range = 20.0..200.0,
                                    step = 5.0,
                                    onValueChange = { tempFat = it }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val age = ageStr.toIntOrNull() ?: currentAge
                    val weight = weightStr.toDoubleOrNull() ?: currentWeight
                    val waist = waistStr.toDoubleOrNull() ?: currentWaist
                    val chest = chestStr.toDoubleOrNull() ?: currentChest
                    val hips = hipsStr.toDoubleOrNull() ?: currentHips
                    val biceps = bicepsStr.toDoubleOrNull() ?: currentBiceps
                    val thighs = thighsStr.toDoubleOrNull() ?: currentThighs

                    viewModel.updatePersonalDetails(
                        age = age,
                        weight = weight,
                        waist = waist,
                        chest = chest,
                        hips = hips,
                        biceps = biceps,
                        thighs = thighs
                    )

                    // Save daily target changes and Set Goals parameters
                    viewModel.updateGoal("water_ml", tempWater)
                    viewModel.updateGoal("caffeine_mg", tempCaffeine)
                    viewModel.updateGoal("calories_kcal", tempCalories)
                    viewModel.updateGoal("protein_g", tempProtein)
                    viewModel.updateGoal("fat_g", tempFat)
                    viewModel.updateGoal("carbs_g", tempCarbs)
                    viewModel.updateGoal("exercise_min", tempExercise)
                    viewModel.updateGoal("sleep_hours", tempSleep)

                    Toast.makeText(context, "Settings & Goals updated successfully!", Toast.LENGTH_SHORT).show()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Teal400),
                modifier = Modifier.testTag("save_settings_btn")
            ) {
                Text("Save Changes", color = Slate950)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Slate400)
            }
        },
        containerColor = Slate900
    )

    if (showGoogleAccountSelector) {
        GoogleAccountSelectorDialog(
            onDismiss = { showGoogleAccountSelector = false },
            onSelect = { name, email, photo ->
                viewModel.signInWithGoogle(name, email, photo)
                showGoogleAccountSelector = false
                Toast.makeText(context, "Signed in as $name", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
