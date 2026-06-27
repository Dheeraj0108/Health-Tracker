package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.model.CaffeineLog
import com.example.data.model.ExerciseLog
import com.example.data.model.FoodLog
import com.example.data.model.Goal
import com.example.data.model.WaterLog
import com.example.data.model.BodyLog
import com.example.data.model.ExerciseRoutine
import com.example.data.model.WeeklyPlan
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class HealthRepository(private val db: AppDatabase) {

    val allWaterLogs: Flow<List<WaterLog>> = db.waterDao().getAllWaterLogs()
    val allCaffeineLogs: Flow<List<CaffeineLog>> = db.caffeineDao().getAllCaffeineLogs()
    val allExerciseLogs: Flow<List<ExerciseLog>> = db.exerciseDao().getAllExerciseLogs()
    val allFoodLogs: Flow<List<FoodLog>> = db.foodDao().getAllFoodLogs()
    val allGoals: Flow<List<Goal>> = db.goalDao().getAllGoals()
    val allBodyLogs: Flow<List<BodyLog>> = db.bodyDao().getAllBodyLogs()
    val allRoutines: Flow<List<ExerciseRoutine>> = db.routineDao().getAllRoutines()
    val allPlans: Flow<List<WeeklyPlan>> = db.planDao().getAllPlans()

    suspend fun insertWaterLog(log: WaterLog) = db.waterDao().insertWaterLog(log)
    suspend fun deleteWaterLog(id: Int) = db.waterDao().deleteWaterLogById(id)

    suspend fun insertCaffeineLog(log: CaffeineLog) = db.caffeineDao().insertCaffeineLog(log)
    suspend fun deleteCaffeineLog(id: Int) = db.caffeineDao().deleteCaffeineLogById(id)

    suspend fun insertExerciseLog(log: ExerciseLog) = db.exerciseDao().insertExerciseLog(log)
    suspend fun deleteExerciseLog(id: Int) = db.exerciseDao().deleteExerciseLogById(id)

    suspend fun insertFoodLog(log: FoodLog) = db.foodDao().insertFoodLog(log)
    suspend fun deleteFoodLog(id: Int) = db.foodDao().deleteFoodLogById(id)

    suspend fun insertGoal(goal: Goal) = db.goalDao().insertGoal(goal)
    suspend fun getGoalByKey(key: String): Goal? = db.goalDao().getGoalByKey(key)

    suspend fun insertBodyLog(log: BodyLog) = db.bodyDao().insertBodyLog(log)
    suspend fun deleteBodyLog(id: Int) = db.bodyDao().deleteBodyLogById(id)

    suspend fun insertRoutine(routine: ExerciseRoutine) = db.routineDao().insertRoutine(routine)
    suspend fun deleteRoutine(id: Int) = db.routineDao().deleteRoutineById(id)

    suspend fun insertPlan(plan: WeeklyPlan) = db.planDao().insertPlan(plan)
    suspend fun updatePlanCompletion(id: Int, completed: Boolean) = db.planDao().updatePlanCompletion(id, completed)
    suspend fun deletePlan(id: Int) = db.planDao().deletePlanById(id)
    suspend fun clearAllPlans() = db.planDao().clearAllPlans()

    suspend fun ensureDefaultGoals() {
        val defaults = mapOf(
            "water_ml" to 2000.0,
            "caffeine_mg" to 400.0,
            "calories_kcal" to 2000.0,
            "protein_g" to 120.0,
            "fat_g" to 70.0,
            "carbs_g" to 250.0,
            "exercise_min" to 30.0
        )
        for ((key, value) in defaults) {
            val existing = db.goalDao().getGoalByKey(key)
            if (existing == null) {
                db.goalDao().insertGoal(Goal(key = key, value = value))
            }
        }

        // Add some default routines to help the user get started
        val routinesList = db.routineDao().getAllRoutines().firstOrNull()
        if (routinesList.isNullOrEmpty()) {
            val defaultRoutines = listOf(
                ExerciseRoutine(
                    name = "Push Day (Chest, Shoulders, Triceps)",
                    description = "Focus on pressing movements for upper body push muscles.",
                    exercisesListJson = "Bench Press (4x8); Overhead Dumbbell Press (3x10); Incline Dumbbell Flyes (3x12); Triceps Rope Pushdown (3x15); Lateral Raises (3x15)"
                ),
                ExerciseRoutine(
                    name = "Pull Day (Back & Biceps)",
                    description = "Focus on pulling movements to build a strong back and arms.",
                    exercisesListJson = "Barbell Row (4x8); Lat Pulldown (3x10); Seated Cable Row (3x12); Barbell Bicep Curl (3x10); Hammer Curls (3x12)"
                ),
                ExerciseRoutine(
                    name = "Leg Day (Quads, Hamstrings, Calves)",
                    description = "Full lower body development workout.",
                    exercisesListJson = "Barbell Squats (4x8); Romanian Deadlifts (4x10); Leg Press (3x12); Lying Leg Curls (3x15); Standing Calf Raises (4x15)"
                ),
                ExerciseRoutine(
                    name = "Active Recovery & Cardio",
                    description = "Low intensity steady state cardio and core work.",
                    exercisesListJson = "Incline Treadmill Walk (30 mins); Plank (3x60 secs); Hanging Knee Raises (3x15)"
                )
            )
            for (r in defaultRoutines) {
                db.routineDao().insertRoutine(r)
            }
        }
    }
}
