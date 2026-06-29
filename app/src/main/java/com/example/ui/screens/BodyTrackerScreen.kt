package com.example.ui.screens

import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.data.model.BodyLog
import com.example.ui.theme.*
import com.example.ui.viewmodel.HealthViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BodyTrackerScreen(viewModel: HealthViewModel) {
    val context = LocalContext.current
    val bodyLogs by viewModel.bodyLogs.collectAsStateWithLifecycle()
    val weightUnit by viewModel.weightUnit.collectAsStateWithLifecycle()
    
    var showAddLogDialog by remember { mutableStateOf(false) }
    var expandedLogForSummary by remember { mutableStateOf<BodyLog?>(null) }
    var activeGenderProfile by remember { mutableStateOf("Male") } // "Male" or "Female"

    // Date formatting
    val sdf = remember { SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()) }

    // Helper function to format weight
    val formatWeight = remember(weightUnit) {
        { weightInKg: Double ->
            if (weightUnit == "lb") {
                "${String.format(Locale.US, "%.1f", weightInKg * 2.20462)} lb"
            } else {
                "${String.format(Locale.US, "%.1f", weightInKg)} kg"
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
            .padding(16.dp)
    ) {
        // Header Row
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Body Tracking",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ThemeWhite
                )
                Text(
                    text = "Weight, measurements & photos",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate400
                )
            }
            Button(
                onClick = { showAddLogDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = Teal400),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Slate950, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Log Entry", color = Slate950, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        if (bodyLogs.isEmpty()) {
            // Default Landing State: clean blank state placeholder
            Column(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Camera,
                    contentDescription = null,
                    tint = Slate700,
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Start Your Transformation", fontWeight = FontWeight.Bold, color = Slate300)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Log physical measurements & photos to generate dynamic 3D mapping.", color = Slate500, fontSize = 12.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 3D Anatomical Visualization Section
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "3D Anatomical Mapping",
                                style = MaterialTheme.typography.titleSmall,
                                color = Teal400,
                                fontWeight = FontWeight.Bold
                            )
                            // Gender Selector Toggle
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Slate800)
                                    .padding(2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (activeGenderProfile == "Male") Teal400 else Color.Transparent)
                                        .clickable { activeGenderProfile = "Male" }
                                        .padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Male", color = if (activeGenderProfile == "Male") Slate950 else Slate300, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (activeGenderProfile == "Female") Teal400 else Color.Transparent)
                                        .clickable { activeGenderProfile = "Female" }
                                        .padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Female", color = if (activeGenderProfile == "Female") Slate950 else Slate300, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Anatomical Human Body Visualizer
                        AnatomicalHumanBody(
                            gender = activeGenderProfile,
                            metrics = bodyLogs.first()
                        )
                    }
                }

                // Historical Cascade Header
                item {
                    Text(
                        text = "Transformation History Logs",
                        style = MaterialTheme.typography.titleSmall,
                        color = ThemeWhite,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Chronological items with latest on top
                items(bodyLogs) { log ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Slate900),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Slate800)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    // Chronological Cascade format: Date: June 29, 2026 | Height | Weight
                                    val heightDisplay = if (log.heightCm != null) "${log.heightCm} cm" else "No Height"
                                    Text(
                                        text = "Date: ${sdf.format(Date(log.timestamp))} | H: $heightDisplay | W: ${formatWeight(log.weightKg)}",
                                        fontWeight = FontWeight.Bold,
                                        color = ThemeWhite,
                                        fontSize = 13.sp
                                    )
                                }
                                IconButton(onClick = { viewModel.deleteBodyLog(log.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Log", tint = Crimson500, modifier = Modifier.size(18.dp))
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (log.notes.isNullOrEmpty()) "No additional text notes logged." else log.notes,
                                    fontSize = 11.sp,
                                    color = Slate400,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Button(
                                    onClick = { expandedLogForSummary = log },
                                    colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Expand Log", color = Teal400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- EXPANDED LOG DETAILED SUMMARY POPUP WINDOW ---
    expandedLogForSummary?.let { log ->
        AlertDialog(
            onDismissRequest = { expandedLogForSummary = null },
            title = {
                Text(
                    text = "Summary: ${sdf.format(Date(log.timestamp))}",
                    color = ThemeWhite,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (log.imagePath != null) {
                        item {
                            val photoFile = File(log.imagePath)
                            if (photoFile.exists()) {
                                Image(
                                    painter = rememberAsyncImagePainter(photoFile),
                                    contentDescription = "Transformation Photo",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }

                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Logged Physical Matrix", fontSize = 12.sp, color = Teal400, fontWeight = FontWeight.Bold)
                            val loggedMetricsList = listOfNotNull(
                                "Weight" to formatWeight(log.weightKg),
                                log.heightCm?.let { "Height" to "$it cm" },
                                log.neckCm?.let { "Neck" to "$it cm" },
                                log.shoulderCm?.let { "Shoulders" to "$it cm" },
                                log.chestCm?.let { "Chest" to "$it cm" },
                                log.leftBicepCm?.let { "Left Bicep" to "$it cm" },
                                log.rightBicepCm?.let { "Right Bicep" to "$it cm" },
                                log.leftForearmCm?.let { "Left Forearm" to "$it cm" },
                                log.rightForearmCm?.let { "Right Forearm" to "$it cm" },
                                log.waistCm?.let { "Waist" to "$it cm" },
                                log.upperAbsCm?.let { "Upper Abs" to "$it cm" },
                                log.lowerAbsCm?.let { "Lower Abs" to "$it cm" },
                                log.hipsCm?.let { "Hips" to "$it cm" },
                                log.leftThighCm?.let { "Left Thigh" to "$it cm" },
                                log.rightThighCm?.let { "Right Thigh" to "$it cm" },
                                log.leftCalfCm?.let { "Left Calf" to "$it cm" },
                                log.rightCalfCm?.let { "Right Calf" to "$it cm" }
                            )

                            if (loggedMetricsList.isEmpty()) {
                                Text("No measurements logged.", fontSize = 12.sp, color = Slate400)
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Slate800)
                                        .padding(10.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        loggedMetricsList.chunked(2).forEach { rowPair ->
                                            Row(modifier = Modifier.fillMaxWidth()) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(rowPair[0].first, color = Slate400, fontSize = 10.sp)
                                                    Text(rowPair[0].second, color = ThemeWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                }
                                                if (rowPair.size > 1) {
                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(rowPair[1].first, color = Slate400, fontSize = 10.sp)
                                                        Text(rowPair[1].second, color = ThemeWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (!log.notes.isNullOrEmpty()) {
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Workout & Progress Notes", fontSize = 12.sp, color = Teal400, fontWeight = FontWeight.Bold)
                                Text(log.notes, color = ThemeWhite, fontSize = 12.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { expandedLogForSummary = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Teal400)
                ) {
                    Text("Close", color = Slate950, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Slate900
        )
    }

    // --- LOG ENTRY MODAL POPUP INTERFACE ---
    if (showAddLogDialog) {
        var weightStr by remember { mutableStateOf("") }
        var heightStr by remember { mutableStateOf("") }
        var neckStr by remember { mutableStateOf("") }
        var shoulderStr by remember { mutableStateOf("") }
        var chestStr by remember { mutableStateOf("") }
        var leftBicepStr by remember { mutableStateOf("") }
        var rightBicepStr by remember { mutableStateOf("") }
        var leftForearmStr by remember { mutableStateOf("") }
        var rightForearmStr by remember { mutableStateOf("") }
        var waistStr by remember { mutableStateOf("") }
        var upperAbsStr by remember { mutableStateOf("") }
        var lowerAbsStr by remember { mutableStateOf("") }
        var hipsStr by remember { mutableStateOf("") }
        var leftThighStr by remember { mutableStateOf("") }
        var rightThighStr by remember { mutableStateOf("") }
        var leftCalfStr by remember { mutableStateOf("") }
        var rightCalfStr by remember { mutableStateOf("") }
        var progressNotes by remember { mutableStateOf("") }
        var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }

        val cameraLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicturePreview()
        ) { bitmap ->
            if (bitmap != null) {
                selectedBitmap = bitmap
            }
        }

        AlertDialog(
            onDismissRequest = { showAddLogDialog = false },
            title = {
                Text(
                    "Log Daily Body Entry",
                    color = ThemeWhite,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Action Row at the top: Camera/Photo capture row
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("Transformation Snapshot", fontSize = 11.sp, color = Teal400, fontWeight = FontWeight.Bold)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Slate800)
                                    .border(1.dp, Slate700, RoundedCornerShape(12.dp))
                                    .clickable { cameraLauncher.launch(null) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (selectedBitmap != null) {
                                    Image(
                                        bitmap = selectedBitmap!!.asImageBitmap(),
                                        contentDescription = "Transformation Photo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Teal400, modifier = Modifier.size(28.dp))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Capture Transformation Image", color = Slate300, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Numeric input matrix fields
                    item {
                        Text("Sequential Metric Matrix", fontSize = 11.sp, color = Teal400, fontWeight = FontWeight.Bold)
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = weightStr,
                                onValueChange = { weightStr = it },
                                label = { Text("Weight ($weightUnit) *") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Teal400, unfocusedBorderColor = Slate600,
                                    focusedTextColor = Color.White, unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = heightStr,
                                onValueChange = { heightStr = it },
                                label = { Text("Height (cm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Teal400, unfocusedBorderColor = Slate600,
                                    focusedTextColor = Color.White, unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = neckStr,
                                onValueChange = { neckStr = it },
                                label = { Text("Neck (cm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Teal400, unfocusedBorderColor = Slate600,
                                    focusedTextColor = Color.White, unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = shoulderStr,
                                onValueChange = { shoulderStr = it },
                                label = { Text("Shoulder (cm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Teal400, unfocusedBorderColor = Slate600,
                                    focusedTextColor = Color.White, unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = chestStr,
                            onValueChange = { chestStr = it },
                            label = { Text("Chest (cm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Teal400, unfocusedBorderColor = Slate600,
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = leftBicepStr,
                                onValueChange = { leftBicepStr = it },
                                label = { Text("L Bicep (cm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Teal400, unfocusedBorderColor = Slate600,
                                    focusedTextColor = Color.White, unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = rightBicepStr,
                                onValueChange = { rightBicepStr = it },
                                label = { Text("R Bicep (cm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Teal400, unfocusedBorderColor = Slate600,
                                    focusedTextColor = Color.White, unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = leftForearmStr,
                                onValueChange = { leftForearmStr = it },
                                label = { Text("L Forearm (cm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Teal400, unfocusedBorderColor = Slate600,
                                    focusedTextColor = Color.White, unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = rightForearmStr,
                                onValueChange = { rightForearmStr = it },
                                label = { Text("R Forearm (cm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Teal400, unfocusedBorderColor = Slate600,
                                    focusedTextColor = Color.White, unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = waistStr,
                                onValueChange = { waistStr = it },
                                label = { Text("Waist (cm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Teal400, unfocusedBorderColor = Slate600,
                                    focusedTextColor = Color.White, unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = hipsStr,
                                onValueChange = { hipsStr = it },
                                label = { Text("Hips (cm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Teal400, unfocusedBorderColor = Slate600,
                                    focusedTextColor = Color.White, unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = upperAbsStr,
                                onValueChange = { upperAbsStr = it },
                                label = { Text("Upper Abs (cm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Teal400, unfocusedBorderColor = Slate600,
                                    focusedTextColor = Color.White, unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = lowerAbsStr,
                                onValueChange = { lowerAbsStr = it },
                                label = { Text("Lower Abs (cm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Teal400, unfocusedBorderColor = Slate600,
                                    focusedTextColor = Color.White, unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = leftThighStr,
                                onValueChange = { leftThighStr = it },
                                label = { Text("L Thigh (cm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Teal400, unfocusedBorderColor = Slate600,
                                    focusedTextColor = Color.White, unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = rightThighStr,
                                onValueChange = { rightThighStr = it },
                                label = { Text("R Thigh (cm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Teal400, unfocusedBorderColor = Slate600,
                                    focusedTextColor = Color.White, unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = leftCalfStr,
                                onValueChange = { leftCalfStr = it },
                                label = { Text("L Calf (cm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Teal400, unfocusedBorderColor = Slate600,
                                    focusedTextColor = Color.White, unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = rightCalfStr,
                                onValueChange = { rightCalfStr = it },
                                label = { Text("R Calf (cm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Teal400, unfocusedBorderColor = Slate600,
                                    focusedTextColor = Color.White, unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = progressNotes,
                            onValueChange = { progressNotes = it },
                            label = { Text("Text notes/feelings") },
                            placeholder = { Text("Felt leaner, high vascularity today.") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Teal400, unfocusedBorderColor = Slate600,
                                focusedTextColor = Color.White, unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        var weight = weightStr.toDoubleOrNull()
                        if (weight != null) {
                            if (weightUnit == "lb") {
                                weight /= 2.20462 // Convert to kg internally
                            }

                            var savedFilePath: String? = null
                            if (selectedBitmap != null) {
                                try {
                                    val fileName = "transform_${System.currentTimeMillis()}.png"
                                    val file = File(context.filesDir, fileName)
                                    val out = FileOutputStream(file)
                                    selectedBitmap!!.compress(Bitmap.CompressFormat.PNG, 90, out)
                                    out.flush()
                                    out.close()
                                    savedFilePath = file.absolutePath
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }

                            // Use the custom logBody with all 16 variables passed safely!
                            viewModel.logBody(
                                weightKg = weight,
                                chestCm = chestStr.toDoubleOrNull(),
                                waistCm = waistStr.toDoubleOrNull(),
                                hipsCm = hipsStr.toDoubleOrNull(),
                                bicepsCm = leftBicepStr.toDoubleOrNull() ?: rightBicepStr.toDoubleOrNull(), // Support fallback
                                thighsCm = leftThighStr.toDoubleOrNull() ?: rightThighStr.toDoubleOrNull(), // Support fallback
                                imagePath = savedFilePath,
                                notes = progressNotes.ifEmpty { null },
                                heightCm = heightStr.toDoubleOrNull(),
                                neckCm = neckStr.toDoubleOrNull(),
                                shoulderCm = shoulderStr.toDoubleOrNull(),
                                leftBicepCm = leftBicepStr.toDoubleOrNull(),
                                rightBicepCm = rightBicepStr.toDoubleOrNull(),
                                leftForearmCm = leftForearmStr.toDoubleOrNull(),
                                rightForearmCm = rightForearmStr.toDoubleOrNull(),
                                lowerAbsCm = lowerAbsStr.toDoubleOrNull(),
                                upperAbsCm = upperAbsStr.toDoubleOrNull(),
                                leftThighCm = leftThighStr.toDoubleOrNull(),
                                rightThighCm = rightThighStr.toDoubleOrNull(),
                                leftCalfCm = leftCalfStr.toDoubleOrNull(),
                                rightCalfCm = rightCalfStr.toDoubleOrNull()
                            )

                            Toast.makeText(context, "All physical metrics logged! 🚀", Toast.LENGTH_SHORT).show()
                            showAddLogDialog = false
                        } else {
                            Toast.makeText(context, "Please enter a valid weight metric to save.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Teal400)
                ) {
                    Text("Save", color = Slate950, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddLogDialog = false }) {
                    Text("Cancel", color = Slate400)
                }
            },
            containerColor = Slate900
        )
    }
}

@Composable
fun AnatomicalHumanBody(
    gender: String, // "Male" or "Female"
    metrics: BodyLog,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val breatheScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe_scale"
    )
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring_rotation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(290.dp)
            .background(Slate900, RoundedCornerShape(16.dp))
            .border(1.dp, Slate800, RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h / 2f

            val scale = breatheScale

            // Draw dynamic 3D scanning orbits
            val ringW = 110.dp.toPx()
            val ringH = 24.dp.toPx()
            rotate(degrees = ringRotation, pivot = androidx.compose.ui.geometry.Offset(cx, cy - 25.dp.toPx())) {
                drawOval(
                    color = Teal400.copy(alpha = 0.35f),
                    topLeft = androidx.compose.ui.geometry.Offset(cx - ringW / 2, cy - 25.dp.toPx() - ringH / 2),
                    size = androidx.compose.ui.geometry.Size(ringW, ringH),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx())
                )
            }

            rotate(degrees = -ringRotation - 40f, pivot = androidx.compose.ui.geometry.Offset(cx, cy + 35.dp.toPx())) {
                drawOval(
                    color = Amber400.copy(alpha = 0.25f),
                    topLeft = androidx.compose.ui.geometry.Offset(cx - (ringW * 1.15f) / 2, cy + 35.dp.toPx() - (ringH * 0.9f) / 2),
                    size = androidx.compose.ui.geometry.Size(ringW * 1.15f, ringH * 0.9f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx())
                )
            }

            // Drawing the beautiful anatomical human silhouette
            val isMale = gender == "Male"
            val bodyColor = Slate500.copy(alpha = 0.5f)

            // Muscle highlights based on user-entered bodyLog metrics!
            val chestHighlight = if (metrics.chestCm != null) Teal400 else bodyColor
            val waistHighlight = if (metrics.waistCm != null) Amber400 else bodyColor
            val shoulderHighlight = if (metrics.shoulderCm != null) Teal400 else bodyColor
            val neckHighlight = if (metrics.neckCm != null) Amber400 else bodyColor
            val bicepHighlight = if (metrics.leftBicepCm != null || metrics.rightBicepCm != null || metrics.bicepsCm != null) Teal400 else bodyColor
            val forearmHighlight = if (metrics.leftForearmCm != null || metrics.rightForearmCm != null) Amber400 else bodyColor
            val thighHighlight = if (metrics.leftThighCm != null || metrics.rightThighCm != null || metrics.thighsCm != null) Teal400 else bodyColor
            val calfHighlight = if (metrics.leftCalfCm != null || metrics.rightCalfCm != null) Amber400 else bodyColor
            val hipsHighlight = if (metrics.hipsCm != null) Teal400 else bodyColor

            // Draw Head
            drawCircle(
                color = bodyColor,
                radius = 15.dp.toPx() * scale,
                center = androidx.compose.ui.geometry.Offset(cx, cy - 80.dp.toPx())
            )

            // Draw Neck
            drawRect(
                color = neckHighlight,
                topLeft = androidx.compose.ui.geometry.Offset(cx - 3.5.dp.toPx(), cy - 65.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(7.dp.toPx(), 11.dp.toPx())
            )

            // Shoulders/Torso geometry based on gender profile
            val shoulderWidth = if (isMale) 34.dp.toPx() else 25.dp.toPx()
            val waistWidth = if (isMale) 18.dp.toPx() else 16.dp.toPx()
            val hipsWidth = if (isMale) 20.dp.toPx() else 26.dp.toPx()

            // Draw Shoulders (bar)
            drawRoundRect(
                color = shoulderHighlight,
                topLeft = androidx.compose.ui.geometry.Offset(cx - shoulderWidth / 2f, cy - 54.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(shoulderWidth, 11.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.5.dp.toPx())
            )

            // Draw Chest
            drawRect(
                color = chestHighlight,
                topLeft = androidx.compose.ui.geometry.Offset(cx - shoulderWidth * 0.44f, cy - 43.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(shoulderWidth * 0.88f, 15.dp.toPx())
            )

            // Draw Waist/Abs
            drawRect(
                color = waistHighlight,
                topLeft = androidx.compose.ui.geometry.Offset(cx - waistWidth / 2f, cy - 28.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(waistWidth, 15.dp.toPx())
            )

            // Draw Hips
            drawRoundRect(
                color = hipsHighlight,
                topLeft = androidx.compose.ui.geometry.Offset(cx - hipsWidth / 2f, cy - 13.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(hipsWidth, 13.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
            )

            // Left Arm Bicep & Forearm
            drawCircle(
                color = bicepHighlight,
                radius = if (isMale) 6.5.dp.toPx() else 4.5.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(cx - (shoulderWidth / 2f + 5.5.dp.toPx()), cy - 36.dp.toPx())
            )
            drawOval(
                color = forearmHighlight,
                topLeft = androidx.compose.ui.geometry.Offset(cx - (shoulderWidth / 2f + 10.dp.toPx()), cy - 27.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(9.dp.toPx(), 20.dp.toPx())
            )

            // Right Arm Bicep & Forearm
            drawCircle(
                color = bicepHighlight,
                radius = if (isMale) 6.5.dp.toPx() else 4.5.dp.toPx(),
                center = androidx.compose.ui.geometry.Offset(cx + (shoulderWidth / 2f + 5.5.dp.toPx()), cy - 36.dp.toPx())
            )
            drawOval(
                color = forearmHighlight,
                topLeft = androidx.compose.ui.geometry.Offset(cx + (shoulderWidth / 2f + 1.dp.toPx()), cy - 27.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(9.dp.toPx(), 20.dp.toPx())
            )

            // Left Leg Thigh & Calf
            drawOval(
                color = thighHighlight,
                topLeft = androidx.compose.ui.geometry.Offset(cx - hipsWidth * 0.45f, cy + 3.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(hipsWidth * 0.4f, 34.dp.toPx())
            )
            drawOval(
                color = calfHighlight,
                topLeft = androidx.compose.ui.geometry.Offset(cx - hipsWidth * 0.39f, cy + 40.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(hipsWidth * 0.28f, 30.dp.toPx())
            )

            // Right Leg Thigh & Calf
            drawOval(
                color = thighHighlight,
                topLeft = androidx.compose.ui.geometry.Offset(cx + hipsWidth * 0.05f, cy + 3.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(hipsWidth * 0.4f, 34.dp.toPx())
            )
            drawOval(
                color = calfHighlight,
                topLeft = androidx.compose.ui.geometry.Offset(cx + hipsWidth * 0.11f, cy + 40.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(hipsWidth * 0.28f, 30.dp.toPx())
            )
        }

        // Overlay vertical scanning laser bar
        val laserTransition = rememberInfiniteTransition(label = "laser")
        val laserY by laserTransition.animateFloat(
            initialValue = 0.15f,
            targetValue = 0.85f,
            animationSpec = infiniteRepeatable(
                animation = tween(2800, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "laser_y"
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.012f)
                .align(Alignment.TopCenter)
                .offset(y = 290.dp * laserY)
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Teal400.copy(alpha = 0.1f),
                            Teal400,
                            Teal400.copy(alpha = 0.1f)
                        )
                    )
                )
        )
    }
}

@Composable
fun BodyMetricChip(
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Slate800)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Column {
            Text(label, fontSize = 9.sp, color = Slate400, fontWeight = FontWeight.Bold)
            Text(value, fontSize = 12.sp, color = accentColor, fontWeight = FontWeight.Bold)
        }
    }
}
