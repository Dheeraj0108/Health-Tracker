package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_logs")
data class WaterLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amountMl: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "caffeine_logs")
data class CaffeineLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amountMg: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "exercise_logs")
data class ExerciseLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "GYM", "RUN", "WALK"
    val exerciseName: String?, // e.g. "Bench Press" (null for run/walk)
    val sets: Int?,
    val reps: Int?,
    val durationMinutes: Int,
    val distanceKm: Double?,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "food_logs")
data class FoodLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val description: String,
    val imagePathOrRes: String?, // Stores a drawable resource name or file URI
    val calories: Double, // kcal
    val fat: Double, // g
    val carbs: Double, // g
    val protein: Double, // g
    val fiber: Double, // g
    val vitaminC: Double, // mg
    val vitaminA: Double, // mcg or IU
    val vitaminB: Double, // mg
    val calcium: Double, // mg
    val magnesium: Double, // mg
    val iron: Double, // mg
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val key: String, // "water_ml", "caffeine_mg", "calories_kcal", "protein_g", "fat_g", "carbs_g", "exercise_min"
    val value: Double,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "body_logs")
data class BodyLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val weightKg: Double,
    val chestCm: Double?,
    val waistCm: Double?,
    val hipsCm: Double?,
    val bicepsCm: Double?,
    val thighsCm: Double?,
    val imagePath: String?, // File path or content URI for body transformation photo
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "exercise_routines")
data class ExerciseRoutine(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val exercisesListJson: String, // Semi-colon separated or text format of exercises
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "weekly_plans")
data class WeeklyPlan(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val weekNumber: Int, // 1 to 12
    val dayOfWeek: Int, // 1 (Monday) to 7 (Sunday)
    val routineNameOrId: String, // e.g. "Push Day", "Rest", or Routine ID
    val isCompleted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

