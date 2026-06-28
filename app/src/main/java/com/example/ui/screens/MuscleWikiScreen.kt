package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.ui.theme.*

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
fun MuscleWikiScreen() {
    val context = LocalContext.current
    var selectedMuscleGroup by remember { mutableStateOf("Chest") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedExerciseForDialog by remember { mutableStateOf<WikiExercise?>(null) }

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
            )
        )
    }

    // Filter exercises based on selected muscle group and search query
    val filteredExercises = remember(selectedMuscleGroup, searchQuery) {
        exerciseDatabase.filter { exercise ->
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
