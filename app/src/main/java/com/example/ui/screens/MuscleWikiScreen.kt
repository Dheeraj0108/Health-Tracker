package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CustomExercise
import com.example.ui.theme.*
import com.example.ui.viewmodel.HealthViewModel

// Data structure representing an exercise from the database
data class WikiExercise(
    val name: String,
    val muscleGroup: String,
    val difficulty: String, // "Beginner", "Intermediate", "Advanced"
    val equipment: String, // "Barbell", "Dumbbell", "Cables", "Bodyweight", "Machine", "Smith Machine"
    val description: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuscleWikiScreen(viewModel: HealthViewModel) {
    val context = LocalContext.current
    var selectedMuscleGroup by remember { mutableStateOf("Chest") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedExerciseForDialog by remember { mutableStateOf<WikiExercise?>(null) }
    var showAddCustomDialog by remember { mutableStateOf(false) }

    val customExercises by viewModel.customExercises.collectAsStateWithLifecycle()
    val customExercisesMapped = remember(customExercises) {
        customExercises.map {
            WikiExercise(
                name = it.name,
                muscleGroup = it.bodyPart,
                difficulty = it.level,
                equipment = it.equipment,
                description = it.specificInstruction ?: "No specific instructions provided."
            )
        }
    }

    val muscleGroups = listOf(
        "Chest", "Back", "Shoulders", "Biceps", "Triceps", "Quads", "Hamstrings", "Abs"
    )

    // Comprehensive exercise database mapped to categories with a good bunch of exercises
    val exerciseDatabase = remember {
        listOf(
            // CHEST
            WikiExercise(
                name = "Barbell Bench Press",
                muscleGroup = "Chest",
                difficulty = "Beginner",
                equipment = "Barbell"
            ),
            WikiExercise(
                name = "Incline Dumbbell Press",
                muscleGroup = "Chest",
                difficulty = "Intermediate",
                equipment = "Dumbbell"
            ),
            WikiExercise(
                name = "Dumbbell Flyes",
                muscleGroup = "Chest",
                difficulty = "Beginner",
                equipment = "Dumbbell"
            ),
            WikiExercise(
                name = "Chest Fly (Pec Deck)",
                muscleGroup = "Chest",
                difficulty = "Beginner",
                equipment = "Machine"
            ),
            WikiExercise(
                name = "Push-ups",
                muscleGroup = "Chest",
                difficulty = "Beginner",
                equipment = "Bodyweight"
            ),
            WikiExercise(
                name = "Cable Crossover",
                muscleGroup = "Chest",
                difficulty = "Intermediate",
                equipment = "Cables"
            ),
            WikiExercise(
                name = "Decline Bench Press",
                muscleGroup = "Chest",
                difficulty = "Intermediate",
                equipment = "Barbell"
            ),
            WikiExercise(
                name = "Chest Dips",
                muscleGroup = "Chest",
                difficulty = "Advanced",
                equipment = "Bodyweight"
            ),
            WikiExercise(
                name = "Smith Machine Chest Press",
                muscleGroup = "Chest",
                difficulty = "Beginner",
                equipment = "Smith Machine"
            ),
            WikiExercise(
                name = "Decline Dumbbell Press",
                muscleGroup = "Chest",
                difficulty = "Intermediate",
                equipment = "Dumbbell"
            ),
            WikiExercise(
                name = "Incline Barbell Bench Press",
                muscleGroup = "Chest",
                difficulty = "Intermediate",
                equipment = "Barbell"
            ),
            WikiExercise(
                name = "Single-Arm Cable Fly",
                muscleGroup = "Chest",
                difficulty = "Intermediate",
                equipment = "Cables"
            ),
            WikiExercise(
                name = "Dumbbell Pullover",
                muscleGroup = "Chest",
                difficulty = "Beginner",
                equipment = "Dumbbell"
            ),
            WikiExercise(
                name = "Incline Cable Fly",
                muscleGroup = "Chest",
                difficulty = "Intermediate",
                equipment = "Cables"
            ),
            WikiExercise(
                name = "Machine Chest Press",
                muscleGroup = "Chest",
                difficulty = "Beginner",
                equipment = "Machine"
            ),

            // BACK
            WikiExercise(
                name = "Barbell Bent Over Row",
                muscleGroup = "Back",
                difficulty = "Intermediate",
                equipment = "Barbell"
            ),
            WikiExercise(
                name = "Lat Pulldown",
                muscleGroup = "Back",
                difficulty = "Beginner",
                equipment = "Cables"
            ),
            WikiExercise(
                name = "Pull-Ups",
                muscleGroup = "Back",
                difficulty = "Advanced",
                equipment = "Bodyweight"
            ),
            WikiExercise(
                name = "T-bar Row",
                muscleGroup = "Back",
                difficulty = "Intermediate",
                equipment = "Barbell"
            ),
            WikiExercise(
                name = "One-Arm Dumbbell Row",
                muscleGroup = "Back",
                difficulty = "Beginner",
                equipment = "Dumbbell"
            ),
            WikiExercise(
                name = "Deadlift",
                muscleGroup = "Back",
                difficulty = "Advanced",
                equipment = "Barbell"
            ),
            WikiExercise(
                name = "Seated Cable Row",
                muscleGroup = "Back",
                difficulty = "Beginner",
                equipment = "Cables"
            ),
            WikiExercise(
                name = "Smith Machine Row",
                muscleGroup = "Back",
                difficulty = "Intermediate",
                equipment = "Smith Machine"
            ),
            WikiExercise(
                name = "Hyperextensions (Back Extension)",
                muscleGroup = "Back",
                difficulty = "Beginner",
                equipment = "Bodyweight"
            ),
            WikiExercise(
                name = "Chin-Ups",
                muscleGroup = "Back",
                difficulty = "Intermediate",
                equipment = "Bodyweight"
            ),
            WikiExercise(
                name = "Straight Arm Lat Pulldown",
                muscleGroup = "Back",
                difficulty = "Beginner",
                equipment = "Cables"
            ),
            WikiExercise(
                name = "Rack Pulls",
                muscleGroup = "Back",
                difficulty = "Intermediate",
                equipment = "Barbell"
            ),
            WikiExercise(
                name = "Chest-Supported Dumbbell Row",
                muscleGroup = "Back",
                difficulty = "Beginner",
                equipment = "Dumbbell"
            ),
            WikiExercise(
                name = "Reverse Grip Lat Pulldown",
                muscleGroup = "Back",
                difficulty = "Beginner",
                equipment = "Cables"
            ),
            WikiExercise(
                name = "Dumbbell Shrugs",
                muscleGroup = "Back",
                difficulty = "Beginner",
                equipment = "Dumbbell"
            ),

            // SHOULDERS
            WikiExercise(
                name = "Overhead Barbell Press",
                muscleGroup = "Shoulders",
                difficulty = "Intermediate",
                equipment = "Barbell"
            ),
            WikiExercise(
                name = "Dumbbell Lateral Raise",
                muscleGroup = "Shoulders",
                difficulty = "Beginner",
                equipment = "Dumbbell"
            ),
            WikiExercise(
                name = "Arnold Press",
                muscleGroup = "Shoulders",
                difficulty = "Intermediate",
                equipment = "Dumbbell"
            ),
            WikiExercise(
                name = "Front Dumbbell Raise",
                muscleGroup = "Shoulders",
                difficulty = "Beginner",
                equipment = "Dumbbell"
            ),
            WikiExercise(
                name = "Rear Delt Fly (Reverse Pec Deck)",
                muscleGroup = "Shoulders",
                difficulty = "Beginner",
                equipment = "Machine"
            ),
            WikiExercise(
                name = "Face Pulls",
                muscleGroup = "Shoulders",
                difficulty = "Beginner",
                equipment = "Cables"
            ),
            WikiExercise(
                name = "Smith Machine Shoulder Press",
                muscleGroup = "Shoulders",
                difficulty = "Intermediate",
                equipment = "Smith Machine"
            ),
            WikiExercise(
                name = "Seated Dumbbell Press",
                muscleGroup = "Shoulders",
                difficulty = "Intermediate",
                equipment = "Dumbbell"
            ),
            WikiExercise(
                name = "Cable Lateral Raise",
                muscleGroup = "Shoulders",
                difficulty = "Intermediate",
                equipment = "Cables"
            ),
            WikiExercise(
                name = "Reverse Dumbbell Flyes",
                muscleGroup = "Shoulders",
                difficulty = "Beginner",
                equipment = "Dumbbell"
            ),
            WikiExercise(
                name = "Upright Row",
                muscleGroup = "Shoulders",
                difficulty = "Intermediate",
                equipment = "Barbell"
            ),
            WikiExercise(
                name = "Behind the Neck Press",
                muscleGroup = "Shoulders",
                difficulty = "Advanced",
                equipment = "Barbell"
            ),
            WikiExercise(
                name = "Barbell Front Raise",
                muscleGroup = "Shoulders",
                difficulty = "Beginner",
                equipment = "Barbell"
            ),
            WikiExercise(
                name = "Cable Rear Delt Row",
                muscleGroup = "Shoulders",
                difficulty = "Intermediate",
                equipment = "Cables"
            ),
            WikiExercise(
                name = "Machine Shoulder Press",
                muscleGroup = "Shoulders",
                difficulty = "Beginner",
                equipment = "Machine"
            ),

            // BICEPS
            WikiExercise(
                name = "Barbell Bicep Curl",
                muscleGroup = "Biceps",
                difficulty = "Beginner",
                equipment = "Barbell"
            ),
            WikiExercise(
                name = "Hammer Curl",
                muscleGroup = "Biceps",
                difficulty = "Beginner",
                equipment = "Dumbbell"
            ),
            WikiExercise(
                name = "Concentration Curl",
                muscleGroup = "Biceps",
                difficulty = "Beginner",
                equipment = "Dumbbell"
            ),
            WikiExercise(
                name = "Preacher Curl",
                muscleGroup = "Biceps",
                difficulty = "Intermediate",
                equipment = "Barbell"
            ),
            WikiExercise(
                name = "Cable Bicep Curl",
                muscleGroup = "Biceps",
                difficulty = "Beginner",
                equipment = "Cables"
            ),
            WikiExercise(
                name = "Incline Dumbbell Curl",
                muscleGroup = "Biceps",
                difficulty = "Intermediate",
                equipment = "Dumbbell"
            ),
            WikiExercise(
                name = "Spider Curl",
                muscleGroup = "Biceps",
                difficulty = "Intermediate",
                equipment = "Barbell"
            ),
            WikiExercise(
                name = "Cable Hammer Curl",
                muscleGroup = "Biceps",
                difficulty = "Beginner",
                equipment = "Cables"
            ),
            WikiExercise(
                name = "Zottman Curl",
                muscleGroup = "Biceps",
                difficulty = "Intermediate",
                equipment = "Dumbbell"
            ),
            WikiExercise(
                name = "Concentration Cable Curl",
                muscleGroup = "Biceps",
                difficulty = "Beginner",
                equipment = "Cables"
            ),
            WikiExercise(
                name = "EZ Bar Curl",
                muscleGroup = "Biceps",
                difficulty = "Beginner",
                equipment = "Barbell"
            ),
            WikiExercise(
                name = "Close-Grip Chin-up",
                muscleGroup = "Biceps",
                difficulty = "Intermediate",
                equipment = "Bodyweight"
            ),
            WikiExercise(
                name = "Machine Bicep Curl",
                muscleGroup = "Biceps",
                difficulty = "Beginner",
                equipment = "Machine"
            ),
            WikiExercise(
                name = "Behind-the-Back Cable Curl",
                muscleGroup = "Biceps",
                difficulty = "Intermediate",
                equipment = "Cables"
            ),
            WikiExercise(
                name = "Cross-Body Hammer Curl",
                muscleGroup = "Biceps",
                difficulty = "Beginner",
                equipment = "Dumbbell"
            ),

            // TRICEPS
            WikiExercise(
                name = "Tricep Overhead Extension",
                muscleGroup = "Triceps",
                difficulty = "Beginner",
                equipment = "Dumbbell"
            ),
            WikiExercise(
                name = "Triceps Rope Pushdown",
                muscleGroup = "Triceps",
                difficulty = "Beginner",
                equipment = "Cables"
            ),
            WikiExercise(
                name = "Close-Grip Bench Press",
                muscleGroup = "Triceps",
                difficulty = "Intermediate",
                equipment = "Barbell"
            ),
            WikiExercise(
                name = "Skull Crushers (Lying Triceps Extension)",
                muscleGroup = "Triceps",
                difficulty = "Intermediate",
                equipment = "Barbell"
            ),
            WikiExercise(
                name = "Bench Dips",
                muscleGroup = "Triceps",
                difficulty = "Beginner",
                equipment = "Bodyweight"
            ),
            WikiExercise(
                name = "Cable Overhead Extension",
                muscleGroup = "Triceps",
                difficulty = "Beginner",
                equipment = "Cables"
            ),
            WikiExercise(
                name = "Cable V-Bar Pushdown",
                muscleGroup = "Triceps",
                difficulty = "Beginner",
                equipment = "Cables"
            ),
            WikiExercise(
                name = "Dumbbell Kickbacks",
                muscleGroup = "Triceps",
                difficulty = "Beginner",
                equipment = "Dumbbell"
            ),
            WikiExercise(
                name = "Diamond Push-ups",
                muscleGroup = "Triceps",
                difficulty = "Intermediate",
                equipment = "Bodyweight"
            ),
            WikiExercise(
                name = "Weighted Dips",
                muscleGroup = "Triceps",
                difficulty = "Advanced",
                equipment = "Bodyweight"
            ),
            WikiExercise(
                name = "Single-Arm Cable Pushdown",
                muscleGroup = "Triceps",
                difficulty = "Intermediate",
                equipment = "Cables"
            ),
            WikiExercise(
                name = "Overhead Cable Pull",
                muscleGroup = "Triceps",
                difficulty = "Intermediate",
                equipment = "Cables"
            ),
            WikiExercise(
                name = "Tate Press",
                muscleGroup = "Triceps",
                difficulty = "Advanced",
                equipment = "Dumbbell"
            ),
            WikiExercise(
                name = "Machine Triceps Dip",
                muscleGroup = "Triceps",
                difficulty = "Beginner",
                equipment = "Machine"
            ),
            WikiExercise(
                name = "Dumbbell JM Press",
                muscleGroup = "Triceps",
                difficulty = "Intermediate",
                equipment = "Dumbbell"
            ),

            // QUADS
            WikiExercise(
                name = "Barbell Back Squat",
                muscleGroup = "Quads",
                difficulty = "Intermediate",
                equipment = "Barbell"
            ),
            WikiExercise(
                name = "Leg Press",
                muscleGroup = "Quads",
                difficulty = "Beginner",
                equipment = "Machine"
            ),
            WikiExercise(
                name = "Leg Extension",
                muscleGroup = "Quads",
                difficulty = "Beginner",
                equipment = "Machine"
            ),
            WikiExercise(
                name = "Goblet Squat",
                muscleGroup = "Quads",
                difficulty = "Beginner",
                equipment = "Dumbbell"
            ),
            WikiExercise(
                name = "Lunges",
                muscleGroup = "Quads",
                difficulty = "Beginner",
                equipment = "Dumbbell"
            ),
            WikiExercise(
                name = "Bulgarian Split Squat",
                muscleGroup = "Quads",
                difficulty = "Advanced",
                equipment = "Dumbbell"
            ),
            WikiExercise(
                name = "Front Squat",
                muscleGroup = "Quads",
                difficulty = "Advanced",
                equipment = "Barbell"
            ),
            WikiExercise(
                name = "Hack Squat",
                muscleGroup = "Quads",
                difficulty = "Intermediate",
                equipment = "Machine"
            ),
            WikiExercise(
                name = "Smith Machine Squats",
                muscleGroup = "Quads",
                difficulty = "Beginner",
                equipment = "Smith Machine"
            ),
            WikiExercise(
                name = "Sissy Squat",
                muscleGroup = "Quads",
                difficulty = "Advanced",
                equipment = "Bodyweight"
            ),
            WikiExercise(
                name = "Step-Ups",
                muscleGroup = "Quads",
                difficulty = "Beginner",
                equipment = "Dumbbell"
            ),
            WikiExercise(
                name = "Box Squat",
                muscleGroup = "Quads",
                difficulty = "Intermediate",
                equipment = "Barbell"
            ),
            WikiExercise(
                name = "Goblet Bulgarian Split Squat",
                muscleGroup = "Quads",
                difficulty = "Advanced",
                equipment = "Dumbbell"
            ),
            WikiExercise(
                name = "Bodyweight Air Squats",
                muscleGroup = "Quads",
                difficulty = "Beginner",
                equipment = "Bodyweight"
            ),
            WikiExercise(
                name = "Walking Lunges",
                muscleGroup = "Quads",
                difficulty = "Beginner",
                equipment = "Dumbbell"
            ),

            // HAMSTRINGS
            WikiExercise(
                name = "Romanian Deadlift",
                muscleGroup = "Hamstrings",
                difficulty = "Intermediate",
                equipment = "Barbell"
            ),
            WikiExercise(
                name = "Lying Leg Curl",
                muscleGroup = "Hamstrings",
                difficulty = "Beginner",
                equipment = "Machine"
            ),
            WikiExercise(
                name = "Romanian Dumbbell Deadlift",
                muscleGroup = "Hamstrings",
                difficulty = "Beginner",
                equipment = "Dumbbell"
            ),
            WikiExercise(
                name = "Glute Ham Raise",
                muscleGroup = "Hamstrings",
                difficulty = "Advanced",
                equipment = "Bodyweight"
            ),
            WikiExercise(
                name = "Good Mornings",
                muscleGroup = "Hamstrings",
                difficulty = "Advanced",
                equipment = "Barbell"
            ),
            WikiExercise(
                name = "Seated Leg Curl",
                muscleGroup = "Hamstrings",
                difficulty = "Beginner",
                equipment = "Machine"
            ),
            WikiExercise(
                name = "Single-Leg Romanian Deadlift",
                muscleGroup = "Hamstrings",
                difficulty = "Intermediate",
                equipment = "Dumbbell"
            ),
            WikiExercise(
                name = "Kettlebell Swings",
                muscleGroup = "Hamstrings",
                difficulty = "Beginner",
                equipment = "Dumbbell"
            ),
            WikiExercise(
                name = "Sumo Deadlift",
                muscleGroup = "Hamstrings",
                difficulty = "Advanced",
                equipment = "Barbell"
            ),
            WikiExercise(
                name = "Nordic Hamstring Curl",
                muscleGroup = "Hamstrings",
                difficulty = "Advanced",
                equipment = "Bodyweight"
            ),
            WikiExercise(
                name = "Stability Ball Leg Curl",
                muscleGroup = "Hamstrings",
                difficulty = "Beginner",
                equipment = "Bodyweight"
            ),
            WikiExercise(
                name = "Deficit Deadlift",
                muscleGroup = "Hamstrings",
                difficulty = "Advanced",
                equipment = "Barbell"
            ),
            WikiExercise(
                name = "Cable Pull-Throughs",
                muscleGroup = "Hamstrings",
                difficulty = "Intermediate",
                equipment = "Cables"
            ),
            WikiExercise(
                name = "Slider Hamstring Curls",
                muscleGroup = "Hamstrings",
                difficulty = "Beginner",
                equipment = "Bodyweight"
            ),
            WikiExercise(
                name = "Single-Leg Lying Leg Curl",
                muscleGroup = "Hamstrings",
                difficulty = "Intermediate",
                equipment = "Machine"
            ),

            // ABS
            WikiExercise(
                name = "Hanging Knee Raise",
                muscleGroup = "Abs",
                difficulty = "Beginner",
                equipment = "Bodyweight"
            ),
            WikiExercise(
                name = "Plank",
                muscleGroup = "Abs",
                difficulty = "Beginner",
                equipment = "Bodyweight"
            ),
            WikiExercise(
                name = "Ab Wheel Rollout",
                muscleGroup = "Abs",
                difficulty = "Advanced",
                equipment = "Bodyweight"
            ),
            WikiExercise(
                name = "Russian Twists",
                muscleGroup = "Abs",
                difficulty = "Beginner",
                equipment = "Dumbbell"
            ),
            WikiExercise(
                name = "Crunches",
                muscleGroup = "Abs",
                difficulty = "Beginner",
                equipment = "Bodyweight"
            ),
            WikiExercise(
                name = "Cable Woodchoppers",
                muscleGroup = "Abs",
                difficulty = "Intermediate",
                equipment = "Cables"
            ),
            WikiExercise(
                name = "Hanging Leg Raise",
                muscleGroup = "Abs",
                difficulty = "Advanced",
                equipment = "Bodyweight"
            ),
            WikiExercise(
                name = "Decline Sit-ups",
                muscleGroup = "Abs",
                difficulty = "Beginner",
                equipment = "Bodyweight"
            ),
            WikiExercise(
                name = "Cable Crunch",
                muscleGroup = "Abs",
                difficulty = "Intermediate",
                equipment = "Cables"
            ),
            WikiExercise(
                name = "Bicycle Crunches",
                muscleGroup = "Abs",
                difficulty = "Beginner",
                equipment = "Bodyweight"
            ),
            WikiExercise(
                name = "Lying Leg Raise",
                muscleGroup = "Abs",
                difficulty = "Beginner",
                equipment = "Bodyweight"
            ),
            WikiExercise(
                name = "Dragon Flag",
                muscleGroup = "Abs",
                difficulty = "Advanced",
                equipment = "Bodyweight"
            ),
            WikiExercise(
                name = "Deadbug",
                muscleGroup = "Abs",
                difficulty = "Beginner",
                equipment = "Bodyweight"
            ),
            WikiExercise(
                name = "Side Plank",
                muscleGroup = "Abs",
                difficulty = "Beginner",
                equipment = "Bodyweight"
            ),
            WikiExercise(
                name = "Flutter Kicks",
                muscleGroup = "Abs",
                difficulty = "Beginner",
                equipment = "Bodyweight"
            )
        )
    }

    // Filter exercises based on selected muscle group and search query
    val filteredExercises = remember(selectedMuscleGroup, searchQuery, customExercisesMapped) {
        val combinedDatabase = exerciseDatabase + customExercisesMapped
        combinedDatabase.filter { exercise ->
            if (searchQuery.isBlank()) {
                exercise.muscleGroup == selectedMuscleGroup
            } else {
                // Global search across all muscle groups if query is provided
                exercise.name.contains(searchQuery, ignoreCase = true) ||
                exercise.muscleGroup.contains(searchQuery, ignoreCase = true) ||
                exercise.equipment.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Slate950)
                .padding(16.dp)
        ) {
        // Search & Deep Link Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "MuscleWiki Explorer",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Interactive exercise database & instructions",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate400
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Darebee button
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://darebee.com/"))
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Teal500),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.OpenInBrowser, contentDescription = null, tint = Slate950, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Darebee", color = Slate950, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // MuscleWiki button
                Button(
                    onClick = {
                        val formattedMuscle = selectedMuscleGroup.lowercase()
                        val url = "https://musclewiki.com/$formattedMuscle"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo500),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.OpenInBrowser, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("MuscleWiki", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search exercises (e.g. T-bar, Chest fly...)", color = Slate400) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Slate400) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Teal400,
                unfocusedBorderColor = Slate800,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        // Muscle groups selector slider (only show/emphasize active tab if search is empty)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            items(muscleGroups) { muscle ->
                val isSelected = muscle == selectedMuscleGroup && searchQuery.isBlank()
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Teal400 else Slate900)
                        .clickable { 
                            selectedMuscleGroup = muscle
                            searchQuery = "" // Clear search to focus on this category
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = muscle,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Slate950 else Slate300,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Exercise Results List
        if (filteredExercises.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No exercises found matching your search.",
                    color = Slate500,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredExercises) { exercise ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedExerciseForDialog = exercise },
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
                                    Text(
                                        text = exercise.name,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Badge(containerColor = Indigo500.copy(alpha = 0.2f), contentColor = Indigo400) {
                                            Text(exercise.muscleGroup, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontWeight = FontWeight.Bold)
                                        }
                                        Badge(containerColor = Slate800, contentColor = Slate300) {
                                            Text(exercise.equipment, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                        Badge(containerColor = Slate800, contentColor = when(exercise.difficulty) {
                                            "Beginner" -> Emerald400
                                            "Intermediate" -> Amber400
                                            else -> Crimson400
                                        }) {
                                            Text(exercise.difficulty, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                                val isCustom = customExercises.any { it.name.equals(exercise.name, ignoreCase = true) }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (isCustom) {
                                        val matchedCustom = customExercises.firstOrNull { it.name.equals(exercise.name, ignoreCase = true) }
                                        matchedCustom?.let { cEx ->
                                            IconButton(
                                                onClick = {
                                                    viewModel.deleteCustomExercise(cEx.id)
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete Custom Exercise",
                                                    tint = Crimson500,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                    Icon(
                                        imageVector = Icons.Default.Launch,
                                        contentDescription = "Search",
                                        tint = Teal400,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } // Closes else block
    } // Closes Column

        // Floating Action Button for Adding Custom Strength Exercises
        FloatingActionButton(
            onClick = { showAddCustomDialog = true },
            containerColor = Teal400,
            contentColor = Slate950,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Custom Exercise", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    } // Closes Box

    if (showAddCustomDialog) {
        var exName by remember { mutableStateOf("") }
        var selectedBodyPart by remember { mutableStateOf("Chest") }
        var selectedEquipment by remember { mutableStateOf("Barbell") }
        var selectedLevel by remember { mutableStateOf("Beginner") }
        var specInstruction by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf("") }

        val bodyPartOptions = listOf("Chest", "Back", "Shoulders", "Biceps", "Triceps", "Quads", "Hamstrings", "Abs")
        val equipmentOptions = listOf("Barbell", "Dumbbell", "Cables", "Bodyweight", "Machine", "Smith Machine")
        val levelOptions = listOf("Beginner", "Intermediate", "Advanced")

        var bodyPartExpanded by remember { mutableStateOf(false) }
        var equipmentExpanded by remember { mutableStateOf(false) }
        var levelExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddCustomDialog = false },
            title = {
                Text(
                    "Add Custom Strength Exercise",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (errorMessage.isNotEmpty()) {
                        Text(errorMessage, color = Crimson500, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Column {
                        Text("Exercise Name *", color = Slate400, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = exName,
                            onValueChange = { exName = it },
                            placeholder = { Text("e.g. Hammer Strength Incline Press", color = Slate500, fontSize = 13.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Teal400,
                                unfocusedBorderColor = Slate700,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Column {
                        Text("Body Part *", color = Slate400, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { bodyPartExpanded = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = BorderStroke(1.dp, Slate700),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(selectedBodyPart, fontSize = 14.sp)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            }
                            DropdownMenu(
                                expanded = bodyPartExpanded,
                                onDismissRequest = { bodyPartExpanded = false },
                                modifier = Modifier.background(Slate800)
                            ) {
                                bodyPartOptions.forEach { part ->
                                    DropdownMenuItem(
                                        text = { Text(part, color = Color.White) },
                                        onClick = {
                                            selectedBodyPart = part
                                            bodyPartExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Column {
                        Text("Equipment *", color = Slate400, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { equipmentExpanded = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = BorderStroke(1.dp, Slate700),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(selectedEquipment, fontSize = 14.sp)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            }
                            DropdownMenu(
                                expanded = equipmentExpanded,
                                onDismissRequest = { equipmentExpanded = false },
                                modifier = Modifier.background(Slate800)
                            ) {
                                equipmentOptions.forEach { eq ->
                                    DropdownMenuItem(
                                        text = { Text(eq, color = Color.White) },
                                        onClick = {
                                            selectedEquipment = eq
                                            equipmentExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Column {
                        Text("Difficulty Level *", color = Slate400, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { levelExpanded = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = BorderStroke(1.dp, Slate700),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(selectedLevel, fontSize = 14.sp)
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            }
                            DropdownMenu(
                                expanded = levelExpanded,
                                onDismissRequest = { levelExpanded = false },
                                modifier = Modifier.background(Slate800)
                            ) {
                                levelOptions.forEach { lvl ->
                                    DropdownMenuItem(
                                        text = { Text(lvl, color = Color.White) },
                                        onClick = {
                                            selectedLevel = lvl
                                            levelExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Column {
                        Text("Specific Instructions (Optional)", color = Slate400, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = specInstruction,
                            onValueChange = { specInstruction = it },
                            placeholder = { Text("e.g. Keep chest up, adjust seat so handles align with mid-chest.", color = Slate500, fontSize = 13.sp) },
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Teal400,
                                unfocusedBorderColor = Slate700,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (exName.isBlank()) {
                            errorMessage = "Exercise name is mandatory!"
                            return@Button
                        }
                        viewModel.addCustomExercise(
                            name = exName,
                            bodyPart = selectedBodyPart,
                            equipment = selectedEquipment,
                            level = selectedLevel,
                            specificInstruction = specInstruction,
                            onSuccess = {
                                showAddCustomDialog = false
                            },
                            onError = {
                                errorMessage = it
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Teal400)
                ) {
                    Text("Save Exercise", color = Slate950, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCustomDialog = false }) {
                    Text("Cancel", color = Slate400)
                }
            },
            containerColor = Slate900,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Popup Dialog when clicking an exercise
    selectedExerciseForDialog?.let { exercise ->
        AlertDialog(
            onDismissRequest = { selectedExerciseForDialog = null },
            title = {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Quick info list
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Badge(containerColor = Indigo500.copy(alpha = 0.2f), contentColor = Indigo400) {
                            Text(exercise.muscleGroup, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), fontWeight = FontWeight.Bold)
                        }
                        Badge(containerColor = Slate800, contentColor = Slate300) {
                            Text(exercise.equipment, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                        Badge(containerColor = Slate800, contentColor = when(exercise.difficulty) {
                            "Beginner" -> Emerald400
                            "Intermediate" -> Amber400
                            else -> Crimson400
                        }) {
                            Text(exercise.difficulty, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "To keep the database lightweight, instructions are hosted online. Learn how to perform this exercise, check worksheets, and watch interactive video guides directly on MuscleWiki or Darebee:",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate300,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Buttons to launch external searches/links
                    Button(
                        onClick = {
                            val encodedQuery = Uri.encode(exercise.name)
                            val url = "https://www.google.com/search?q=site:darebee.com+${encodedQuery}"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Teal500),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Launch, contentDescription = null, tint = Slate950, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Search Darebee Worksheets", color = Slate950, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val formattedName = exercise.name.lowercase().replace(" ", "-").replace("(", "").replace(")", "")
                            val url = "https://musclewiki.com/directory/exercises/$formattedName"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Indigo500),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Launch, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Search on MuscleWiki", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { selectedExerciseForDialog = null }
                ) {
                    Text("Close", color = Slate400, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Slate900,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
