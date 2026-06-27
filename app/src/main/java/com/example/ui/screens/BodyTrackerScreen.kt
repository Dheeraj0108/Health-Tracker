package com.example.ui.screens

import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
    var showAddLogDialog by remember { mutableStateOf(false) }

    // Date formatting
    val sdf = remember { SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
            .padding(16.dp)
    ) {
        // Header Row
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Body Tracking",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
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

        // Weight progression card if logs exist
        if (bodyLogs.isNotEmpty()) {
            val latestLog = bodyLogs.first()
            val earliestLog = bodyLogs.last()
            val totalLoss = earliestLog.weightKg - latestLog.weightKg

            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Slate900)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Current Weight", fontSize = 12.sp, color = Slate400)
                        Text("${latestLog.weightKg} kg", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Teal400)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Overall Change", fontSize = 12.sp, color = Slate400)
                        Text(
                            text = when {
                                totalLoss > 0 -> "-${String.format("%.1f", totalLoss)} kg"
                                totalLoss < 0 -> "+${String.format("%.1f", -totalLoss)} kg"
                                else -> "0.0 kg"
                            },
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (totalLoss >= 0) Emerald400 else Crimson400
                        )
                    }
                }
            }
        }

        // Transformation logs timeline
        if (bodyLogs.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Camera,
                    contentDescription = null,
                    tint = Slate700,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Start Your Transformation", fontWeight = FontWeight.Bold, color = Slate300)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Log weight & upload a photo to track your journey.", color = Slate500, fontSize = 12.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(bodyLogs) { log ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Slate900),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column {
                            // Header: Date and delete
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = sdf.format(Date(log.timestamp)),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                                IconButton(onClick = { viewModel.deleteBodyLog(log.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Crimson500, modifier = Modifier.size(18.dp))
                                }
                            }

                            // Weight and measurements grid
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp).padding(bottom = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                BodyMetricChip("Weight", "${log.weightKg} kg", Teal400, Modifier.weight(1f))
                                if (log.waistCm != null) BodyMetricChip("Waist", "${log.waistCm} cm", Indigo400, Modifier.weight(1f))
                                if (log.chestCm != null) BodyMetricChip("Chest", "${log.chestCm} cm", Amber400, Modifier.weight(1f))
                            }

                            if (log.hipsCm != null || log.bicepsCm != null || log.thighsCm != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp).padding(bottom = 14.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (log.bicepsCm != null) BodyMetricChip("Biceps", "${log.bicepsCm} cm", Amber400, Modifier.weight(1f))
                                    if (log.hipsCm != null) BodyMetricChip("Hips", "${log.hipsCm} cm", Indigo400, Modifier.weight(1f))
                                    if (log.thighsCm != null) BodyMetricChip("Thighs", "${log.thighsCm} cm", Teal400, Modifier.weight(1f))
                                }
                            }

                            // Transformation Photo rendering
                            if (log.imagePath != null) {
                                val photoFile = File(log.imagePath)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .background(Slate950)
                                ) {
                                    if (photoFile.exists()) {
                                        Image(
                                            painter = rememberAsyncImagePainter(photoFile),
                                            contentDescription = "Transformation Photo",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        // Mock image or fallback placeholder
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Image, contentDescription = null, tint = Slate600, modifier = Modifier.size(48.dp))
                                            Text("Photo file not found", color = Slate500, fontSize = 11.sp, modifier = Modifier.padding(top = 60.dp))
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

    // Add entry dialog
    if (showAddLogDialog) {
        var weightStr by remember { mutableStateOf("") }
        var chestStr by remember { mutableStateOf("") }
        var waistStr by remember { mutableStateOf("") }
        var hipsStr by remember { mutableStateOf("") }
        var bicepsStr by remember { mutableStateOf("") }
        var thighsStr by remember { mutableStateOf("") }
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
            title = { Text("Log Body Metrics & Photo", color = Color.White) },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        // Snapshot visual
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Slate800)
                                .clickable { cameraLauncher.launch(null) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedBitmap != null) {
                                Image(
                                    bitmap = selectedBitmap!!.asImageBitmap(),
                                    contentDescription = "Snapped Transformation",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Teal400, modifier = Modifier.size(32.dp))
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Snap Transformation Photo", color = Slate300, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = weightStr,
                            onValueChange = { weightStr = it },
                            label = { Text("Weight (kg) *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Teal400,
                                unfocusedBorderColor = Slate600,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = waistStr,
                                onValueChange = { waistStr = it },
                                label = { Text("Waist (cm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Teal400,
                                    unfocusedBorderColor = Slate600,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = chestStr,
                                onValueChange = { chestStr = it },
                                label = { Text("Chest (cm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Teal400,
                                    unfocusedBorderColor = Slate600,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = hipsStr,
                                onValueChange = { hipsStr = it },
                                label = { Text("Hips (cm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Teal400,
                                    unfocusedBorderColor = Slate600,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = bicepsStr,
                                onValueChange = { bicepsStr = it },
                                label = { Text("Biceps (cm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Teal400,
                                    unfocusedBorderColor = Slate600,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = thighsStr,
                            onValueChange = { thighsStr = it },
                            label = { Text("Thighs (cm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Teal400,
                                unfocusedBorderColor = Slate600,
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
                        val weight = weightStr.toDoubleOrNull()
                        if (weight != null) {
                            var savedFilePath: String? = null
                            
                            // Save bitmap to file internally
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

                            viewModel.logBody(
                                weightKg = weight,
                                chestCm = chestStr.toDoubleOrNull(),
                                waistCm = waistStr.toDoubleOrNull(),
                                hipsCm = hipsStr.toDoubleOrNull(),
                                bicepsCm = bicepsStr.toDoubleOrNull(),
                                thighsCm = thighsStr.toDoubleOrNull(),
                                imagePath = savedFilePath
                            )

                            Toast.makeText(context, "Metrics logged!", Toast.LENGTH_SHORT).show()
                            showAddLogDialog = false
                        } else {
                            Toast.makeText(context, "Please enter a valid weight.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Teal400)
                ) {
                    Text("Save", color = Slate950)
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
