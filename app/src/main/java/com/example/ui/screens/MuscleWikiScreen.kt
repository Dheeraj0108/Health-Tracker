package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
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

// Data structure representing an exercise from MuscleWiki database
data class WikiExercise(
    val name: String,
    val muscleGroup: String,
    val difficulty: String, // "Beginner", "Intermediate", "Advanced"
    val equipment: String, // "Barbell", "Dumbbell", "Cables", "Bodyweight"
    val description: String,
    val steps: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuscleWikiScreen() {
    val context = LocalContext.current
    var selectedMuscleGroup by remember { mutableStateOf("Chest") }
    var searchQuery by remember { mutableStateOf("") }

    val muscleGroups = listOf(
        "Chest", "Back", "Shoulders", "Biceps", "Triceps", "Quads", "Hamstrings", "Abs"
    )

    // Comprehensive exercise database mapped to MuscleWiki categories
    val exerciseDatabase = remember {
        listOf(
            WikiExercise(
                name = "Barbell Bench Press",
                muscleGroup = "Chest",
                difficulty = "Beginner",
                equipment = "Barbell",
                description = "The classic compound press for developing pectorals, front deltoids, and triceps.",
                steps = listOf(
                    "Lie flat on your back on a bench.",
                    "Grip the bar with hands slightly wider than shoulder-width.",
                    "Unrack the bar and lower it slowly to your mid-chest.",
                    "Push the bar back up powerfully until arms are fully extended."
                )
            ),
            WikiExercise(
                name = "Incline Dumbbell Press",
                muscleGroup = "Chest",
                difficulty = "Intermediate",
                equipment = "Dumbbell",
                description = "Builds strength in the upper portion of the pectoral muscle (clavicular head).",
                steps = listOf(
                    "Set an incline bench to 30-45 degrees.",
                    "Hold a dumbbell in each hand, resting them on your thighs.",
                    "Lie back, raise the weights to shoulder height, palms facing forward.",
                    "Press the weights up over your chest, then lower under control."
                )
            ),
            WikiExercise(
                name = "Dumbbell Flyes",
                muscleGroup = "Chest",
                difficulty = "Beginner",
                equipment = "Dumbbell",
                description = "Excellent isolation exercise for stretching and widening the chest muscles.",
                steps = listOf(
                    "Lie flat on your back with dumbbells held above your chest, palms facing each other.",
                    "Lower your arms out to the sides in a wide arc, keeping a slight bend in your elbows.",
                    "Once you feel a comfortable stretch in your chest, reverse the movement to return to the top."
                )
            ),
            WikiExercise(
                name = "Barbell Bent Over Row",
                muscleGroup = "Back",
                difficulty = "Intermediate",
                equipment = "Barbell",
                description = "High-impact compound exercise for thickness across the latissimus dorsi and rhomboids.",
                steps = listOf(
                    "Stand with feet shoulder-width, knees slightly bent, holding a barbell.",
                    "Hinge at your hips to lean forward, keeping your spine straight and flat.",
                    "Pull the bar up toward your lower ribcage, squeezing your shoulder blades at the top.",
                    "Lower the bar back to the starting position in a controlled manner."
                )
            ),
            WikiExercise(
                name = "Lat Pulldown",
                muscleGroup = "Back",
                difficulty = "Beginner",
                equipment = "Cables",
                description = "Builds back width and shoulder posture, pulling downwards to target the lats.",
                steps = listOf(
                    "Sit at a lat pulldown station and adjust the knee pad.",
                    "Grip the bar wider than shoulder-width, palms facing away from you.",
                    "Pull the bar down toward your collarbone, keeping your chest up and pulling with your elbows.",
                    "Slowly let the bar return to the top position under tension."
                )
            ),
            WikiExercise(
                name = "Pull-Ups",
                muscleGroup = "Back",
                difficulty = "Advanced",
                equipment = "Bodyweight",
                description = "A gold-standard bodyweight pull to build functional upper body pulling strength.",
                steps = listOf(
                    "Hang from a pull-up bar with hands wider than shoulders, palms facing forward.",
                    "Pull your body upwards by pulling your elbows down until your chin clears the bar.",
                    "Lower yourself slowly back down to a full dead hang before repeating."
                )
            ),
            WikiExercise(
                name = "Overhead Barbell Press",
                muscleGroup = "Shoulders",
                difficulty = "Intermediate",
                equipment = "Barbell",
                description = "Builds strong shoulders (anterior deltoid) and overall core stability.",
                steps = listOf(
                    "Set the bar at chest height on a rack. Grip it slightly wider than shoulders.",
                    "Unrack the bar, resting it on your upper chest/collarbone.",
                    "Squeeze your glutes and core, then press the bar straight overhead.",
                    "Bring the bar back down to your chest safely."
                )
            ),
            WikiExercise(
                name = "Dumbbell Lateral Raise",
                muscleGroup = "Shoulders",
                difficulty = "Beginner",
                equipment = "Dumbbell",
                description = "Isolates the lateral head of the deltoid for broader, wider shoulders.",
                steps = listOf(
                    "Stand straight with dumbbells at your sides, palms facing inwards.",
                    "With a slight bend in your elbows, raise your arms out to the sides until horizontal.",
                    "Pause at the top, then slowly lower the weights back down."
                )
            ),
            WikiExercise(
                name = "Barbell Bicep Curl",
                muscleGroup = "Biceps",
                difficulty = "Beginner",
                equipment = "Barbell",
                description = "The classic mass builder for biceps brachii development.",
                steps = listOf(
                    "Stand straight holding a barbell with an underhand grip, shoulder-width apart.",
                    "Keeping elbows tucked close to your torso, curl the bar up toward your shoulders.",
                    "Squeeze your biceps at the peak, then lower the bar slowly under control."
                )
            ),
            WikiExercise(
                name = "Hammer Curl",
                muscleGroup = "Biceps",
                difficulty = "Beginner",
                equipment = "Dumbbell",
                description = "Targets the brachialis and brachioradialis for forearm and thick arm growth.",
                steps = listOf(
                    "Hold dumbbells with a neutral grip (palms facing each other) at your sides.",
                    "Curl the weights up without rotating your wrists, keeping the palms facing each other.",
                    "Lower the weights slowly under tension."
                )
            ),
            WikiExercise(
                name = "Tricep Overhead Extension",
                muscleGroup = "Triceps",
                difficulty = "Beginner",
                equipment = "Dumbbell",
                description = "Excellent isolation for the long head of the triceps brachii.",
                steps = listOf(
                    "Sit or stand, holding a single dumbbell with both hands overhead.",
                    "Lower the dumbbell slowly behind your head by bending only your elbows.",
                    "Extend your elbows to press the weight back straight up above your head."
                )
            ),
            WikiExercise(
                name = "Triceps Rope Pushdown",
                muscleGroup = "Triceps",
                difficulty = "Beginner",
                equipment = "Cables",
                description = "Cable pushdown focusing on the lateral and medial heads of the triceps.",
                steps = listOf(
                    "Attach a rope handle to a high cable pulley. Grip it with palms facing each other.",
                    "Start with elbows bent at 90 degrees, tucked against your ribs.",
                    "Push the rope downwards, extending your elbows fully and spreading the rope tips apart at the bottom.",
                    "Let the rope return slowly back to the 90-degree start."
                )
            ),
            WikiExercise(
                name = "Barbell Back Squat",
                muscleGroup = "Quads",
                difficulty = "Intermediate",
                equipment = "Barbell",
                description = "The king of leg movements, building massive quadriceps, glutes, and core stability.",
                steps = listOf(
                    "Rest the barbell on your upper back / traps. Stand with feet slightly wider than hips.",
                    "Initiate the squat by breaking at your hips, bending knees, sitting backward.",
                    "Squat down until thighs are parallel to the floor (or lower).",
                    "Drive through your mid-foot to stand back up to starting position."
                )
            ),
            WikiExercise(
                name = "Romanian Deadlift",
                muscleGroup = "Hamstrings",
                difficulty = "Intermediate",
                equipment = "Barbell",
                description = "High efficiency posterior chain development targeting hamstrings and glutes.",
                steps = listOf(
                    "Stand straight holding a barbell at hip height with an overhand grip.",
                    "Push your hips back and hinge forward, keeping a flat back and knees only slightly bent.",
                    "Lower the bar down your shins until you feel a deep stretch in your hamstrings.",
                    "Drive hips forward to return to standing, squeezing glutes."
                )
            ),
            WikiExercise(
                name = "Hanging Knee Raise",
                muscleGroup = "Abs",
                difficulty = "Beginner",
                equipment = "Bodyweight",
                description = "Excellent core builder targeting lower abs and hip flexors.",
                steps = listOf(
                    "Hang from a pull-up bar with arms straight.",
                    "Keep your legs together and slowly raise your knees up toward your chest.",
                    "Squeeze your abs at the top of the lift, then lower your knees slowly back down."
                )
            )
        )
    }

    // Filter exercises based on selected muscle group and search query
    val filteredExercises = remember(selectedMuscleGroup, searchQuery) {
        exerciseDatabase.filter { exercise ->
            val matchesMuscle = exercise.muscleGroup == selectedMuscleGroup
            val matchesQuery = exercise.name.contains(searchQuery, ignoreCase = true) ||
                    exercise.description.contains(searchQuery, ignoreCase = true)
            matchesMuscle && (searchQuery.isBlank() || matchesQuery)
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

            // Launch Browser button
            Button(
                onClick = {
                    val formattedMuscle = selectedMuscleGroup.lowercase()
                    // MuscleWiki URL e.g. https://musclewiki.com/chest/
                    val url = "https://musclewiki.com/$formattedMuscle"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Indigo500),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.OpenInBrowser, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("MuscleWiki", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search exercises...", color = Slate400) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Slate400) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Teal400,
                unfocusedBorderColor = Slate800,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
        )

        // Muscle groups selector slider
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            items(muscleGroups) { muscle ->
                val isSelected = muscle == selectedMuscleGroup
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Teal400 else Slate900)
                        .clickable { selectedMuscleGroup = muscle }
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
                    text = "No exercises found for \"$selectedMuscleGroup\" matching your search.",
                    color = Slate500,
                    fontSize = 13.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredExercises) { exercise ->
                    var isExpanded by remember { mutableStateOf(false) }

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
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = exercise.name,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 15.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
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
                                }
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.Launch else Icons.Default.Info,
                                    contentDescription = "Details",
                                    tint = Teal400,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = exercise.description,
                                color = Slate400,
                                fontSize = 12.sp
                            )

                            AnimatedVisibility(visible = isExpanded) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Slate950)
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = "How to perform:",
                                        fontWeight = FontWeight.Bold,
                                        color = Teal400,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                    exercise.steps.forEachIndexed { idx, step ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 3.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text(
                                                text = "${idx + 1}.",
                                                fontWeight = FontWeight.Bold,
                                                color = Teal400,
                                                fontSize = 12.sp,
                                                modifier = Modifier.width(18.dp)
                                            )
                                            Text(
                                                text = step,
                                                color = Slate200,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                    // Deep link button
                                    Button(
                                        onClick = {
                                            val formattedName = exercise.name.lowercase().replace(" ", "-")
                                            val url = "https://musclewiki.com/directory/exercises/$formattedName"
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                            context.startActivity(intent)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Launch, contentDescription = null, tint = Teal400, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("View steps & videos on MuscleWiki", color = Teal400, fontSize = 11.sp)
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
