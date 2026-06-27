package com.example.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiClient
import com.example.data.api.NutritionResult
import com.example.data.model.CaffeineLog
import com.example.data.model.ExerciseLog
import com.example.data.model.FoodLog
import com.example.data.model.Goal
import com.example.data.model.WaterLog
import com.example.data.model.BodyLog
import com.example.data.model.ExerciseRoutine
import com.example.data.model.WeeklyPlan
import com.example.data.repository.HealthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HealthViewModel(private val repository: HealthRepository) : ViewModel() {

    val waterLogs: StateFlow<List<WaterLog>> = repository.allWaterLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val caffeineLogs: StateFlow<List<CaffeineLog>> = repository.allCaffeineLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val exerciseLogs: StateFlow<List<ExerciseLog>> = repository.allExerciseLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val foodLogs: StateFlow<List<FoodLog>> = repository.allFoodLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val goals: StateFlow<List<Goal>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bodyLogs: StateFlow<List<BodyLog>> = repository.allBodyLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val routines: StateFlow<List<ExerciseRoutine>> = repository.allRoutines
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val plans: StateFlow<List<WeeklyPlan>> = repository.allPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Status states
    private val _isAnalyzingFood = MutableStateFlow(false)
    val isAnalyzingFood = _isAnalyzingFood.asStateFlow()

    private val _analysisResult = MutableStateFlow<NutritionResult?>(null)
    val analysisResult = _analysisResult.asStateFlow()

    private val _isGeneratingInsights = MutableStateFlow(false)
    val isGeneratingInsights = _isGeneratingInsights.asStateFlow()

    private val _personalizedInsights = MutableStateFlow("")
    val personalizedInsights = _personalizedInsights.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureDefaultGoals()
            generateWeeklyInsights()
        }
    }

    // Logging actions
    fun logWater(amountMl: Int) {
        viewModelScope.launch {
            repository.insertWaterLog(WaterLog(amountMl = amountMl))
        }
    }

    fun deleteWaterLog(id: Int) {
        viewModelScope.launch {
            repository.deleteWaterLog(id)
        }
    }

    fun logCaffeine(amountMg: Int) {
        viewModelScope.launch {
            repository.insertCaffeineLog(CaffeineLog(amountMg = amountMg))
        }
    }

    fun deleteCaffeineLog(id: Int) {
        viewModelScope.launch {
            repository.deleteCaffeineLog(id)
        }
    }

    fun logExercise(
        type: String,
        exerciseName: String?,
        sets: Int?,
        reps: Int?,
        durationMinutes: Int,
        distanceKm: Double?
    ) {
        viewModelScope.launch {
            repository.insertExerciseLog(
                ExerciseLog(
                    type = type,
                    exerciseName = exerciseName,
                    sets = sets,
                    reps = reps,
                    durationMinutes = durationMinutes,
                    distanceKm = distanceKm
                )
            )
        }
    }

    fun deleteExerciseLog(id: Int) {
        viewModelScope.launch {
            repository.deleteExerciseLog(id)
        }
    }

    fun updateGoal(key: String, value: Double) {
        viewModelScope.launch {
            val existing = repository.getGoalByKey(key)
            if (existing != null) {
                repository.insertGoal(existing.copy(value = value, timestamp = System.currentTimeMillis()))
            } else {
                repository.insertGoal(Goal(key = key, value = value))
            }
        }
    }

    // Analyze snap food item
    fun analyzeFoodItem(description: String, bitmap: Bitmap?, imageResName: String?) {
        viewModelScope.launch {
            _isAnalyzingFood.value = true
            _analysisResult.value = null
            try {
                val result = GeminiClient.analyzeFood(description, bitmap)
                _analysisResult.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isAnalyzingFood.value = false
            }
        }
    }

    fun confirmFoodLog(result: NutritionResult, imagePathOrResName: String?) {
        viewModelScope.launch {
            repository.insertFoodLog(
                FoodLog(
                    description = result.description,
                    imagePathOrRes = imagePathOrResName,
                    calories = result.calories,
                    fat = result.fat,
                    carbs = result.carbs,
                    protein = result.protein,
                    fiber = result.fiber,
                    vitaminC = result.vitaminC,
                    vitaminA = result.vitaminA,
                    vitaminB = result.vitaminB,
                    calcium = result.calcium,
                    magnesium = result.magnesium,
                    iron = result.iron
                )
            )
            _analysisResult.value = null // reset after confirming
            generateWeeklyInsights() // Refresh insights
        }
    }

    fun cancelFoodAnalysis() {
        _analysisResult.value = null
    }

    fun deleteFoodLog(id: Int) {
        viewModelScope.launch {
            repository.deleteFoodLog(id)
            generateWeeklyInsights()
        }
    }

    // Generate weekly reports & insights
    fun generateWeeklyInsights() {
        viewModelScope.launch {
            _isGeneratingInsights.value = true
            try {
                val today = System.currentTimeMillis()
                val totalWater = getTodayWaterTotal()
                val totalCaffeine = getTodayCaffeineTotal()
                val totalCalories = getTodayCaloriesTotal()
                val exerciseCount = exerciseLogs.value.filter { isToday(it.timestamp) }.size
                val foodCount = foodLogs.value.filter { isToday(it.timestamp) }.size

                val summary = """
                    Daily metrics logged:
                    - Water logged: $totalWater ml
                    - Caffeine logged: $totalCaffeine mg
                    - Calories logged: $totalCalories kcal
                    - Food logs count: $foodCount items
                    - Exercise sessions completed: $exerciseCount sessions
                    Weekly progress shows:
                    - Total exercise logs in last 7 days: ${exerciseLogs.value.size} sessions
                    - Foods logged: ${foodLogs.value.size} times
                """.trimIndent()

                val insights = GeminiClient.generatePersonalizedInsights(summary)
                _personalizedInsights.value = insights
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isGeneratingInsights.value = false
            }
        }
    }

    // Helper functions for daily calculations
    private fun isToday(timestamp: Long): Boolean {
        val calLog = Calendar.getInstance().apply { timeInMillis = timestamp }
        val calNow = Calendar.getInstance()
        return calLog.get(Calendar.YEAR) == calNow.get(Calendar.YEAR) &&
                calLog.get(Calendar.DAY_OF_YEAR) == calNow.get(Calendar.DAY_OF_YEAR)
    }

    fun getTodayWaterTotal(): Int {
        return waterLogs.value.filter { isToday(it.timestamp) }.sumOf { it.amountMl }
    }

    fun getTodayCaffeineTotal(): Int {
        return caffeineLogs.value.filter { isToday(it.timestamp) }.sumOf { it.amountMg }
    }

    fun getTodayCaloriesTotal(): Double {
        return foodLogs.value.filter { isToday(it.timestamp) }.sumOf { it.calories }
    }

    fun getTodayProteinTotal(): Double {
        return foodLogs.value.filter { isToday(it.timestamp) }.sumOf { it.protein }
    }

    fun getTodayFatTotal(): Double {
        return foodLogs.value.filter { isToday(it.timestamp) }.sumOf { it.fat }
    }

    fun getTodayCarbsTotal(): Double {
        return foodLogs.value.filter { isToday(it.timestamp) }.sumOf { it.carbs }
    }

    fun getTodayFiberTotal(): Double {
        return foodLogs.value.filter { isToday(it.timestamp) }.sumOf { it.fiber }
    }

    fun getTodayVitaminCTotal(): Double {
        return foodLogs.value.filter { isToday(it.timestamp) }.sumOf { it.vitaminC }
    }

    fun getTodayVitaminATotal(): Double {
        return foodLogs.value.filter { isToday(it.timestamp) }.sumOf { it.vitaminA }
    }

    fun getTodayVitaminBTotal(): Double {
        return foodLogs.value.filter { isToday(it.timestamp) }.sumOf { it.vitaminB }
    }

    fun getTodayCalciumTotal(): Double {
        return foodLogs.value.filter { isToday(it.timestamp) }.sumOf { it.calcium }
    }

    fun getTodayMagnesiumTotal(): Double {
        return foodLogs.value.filter { isToday(it.timestamp) }.sumOf { it.magnesium }
    }

    fun getTodayIronTotal(): Double {
        return foodLogs.value.filter { isToday(it.timestamp) }.sumOf { it.iron }
    }

    fun getTodayExerciseMinutesTotal(): Int {
        return exerciseLogs.value.filter { isToday(it.timestamp) }.sumOf { it.durationMinutes }
    }

    fun getGoalValue(key: String, defaultValue: Double): Double {
        return goals.value.find { it.key == key }?.value ?: defaultValue
    }

    // Historical chart data grouping for last 7 days
    fun getWeeklyChartData(): List<DailySummaryPoint> {
        val result = mutableListOf<DailySummaryPoint>()
        val sdf = SimpleDateFormat("EEE", Locale.getDefault())
        val cal = Calendar.getInstance()

        // Generate points for last 7 days
        for (i in 6 downTo 0) {
            val checkCal = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -i)
            }
            val dayName = sdf.format(checkCal.time)
            
            val water = waterLogs.value.filter { isSameDay(it.timestamp, checkCal) }.sumOf { it.amountMl }.toDouble()
            val caffeine = caffeineLogs.value.filter { isSameDay(it.timestamp, checkCal) }.sumOf { it.amountMg }.toDouble()
            val calories = foodLogs.value.filter { isSameDay(it.timestamp, checkCal) }.sumOf { it.calories }
            val protein = foodLogs.value.filter { isSameDay(it.timestamp, checkCal) }.sumOf { it.protein }
            val fat = foodLogs.value.filter { isSameDay(it.timestamp, checkCal) }.sumOf { it.fat }
            val carbs = foodLogs.value.filter { isSameDay(it.timestamp, checkCal) }.sumOf { it.carbs }
            val exercise = exerciseLogs.value.filter { isSameDay(it.timestamp, checkCal) }.sumOf { it.durationMinutes }.toDouble()

            result.add(
                DailySummaryPoint(
                    dayLabel = dayName,
                    waterMl = water,
                    caffeineMg = caffeine,
                    caloriesKcal = calories,
                    proteinG = protein,
                    fatG = fat,
                    carbsG = carbs,
                    exerciseMin = exercise
                )
            )
        }
        return result
    }

    private fun isSameDay(timestamp: Long, checkCal: Calendar): Boolean {
        val calLog = Calendar.getInstance().apply { timeInMillis = timestamp }
        return calLog.get(Calendar.YEAR) == checkCal.get(Calendar.YEAR) &&
                calLog.get(Calendar.DAY_OF_YEAR) == checkCal.get(Calendar.DAY_OF_YEAR)
    }

    fun logBody(
        weightKg: Double,
        chestCm: Double?,
        waistCm: Double?,
        hipsCm: Double?,
        bicepsCm: Double?,
        thighsCm: Double?,
        imagePath: String?
    ) {
        viewModelScope.launch {
            repository.insertBodyLog(
                BodyLog(
                    weightKg = weightKg,
                    chestCm = chestCm,
                    waistCm = waistCm,
                    hipsCm = hipsCm,
                    bicepsCm = bicepsCm,
                    thighsCm = thighsCm,
                    imagePath = imagePath
                )
            )
        }
    }

    fun deleteBodyLog(id: Int) {
        viewModelScope.launch {
            repository.deleteBodyLog(id)
        }
    }

    fun addRoutine(name: String, description: String, exercisesListJson: String) {
        viewModelScope.launch {
            repository.insertRoutine(
                ExerciseRoutine(
                    name = name,
                    description = description,
                    exercisesListJson = exercisesListJson
                )
            )
        }
    }

    fun deleteRoutine(id: Int) {
        viewModelScope.launch {
            repository.deleteRoutine(id)
        }
    }

    fun addPlan(weekNumber: Int, dayOfWeek: Int, routineNameOrId: String) {
        viewModelScope.launch {
            repository.insertPlan(
                WeeklyPlan(
                    weekNumber = weekNumber,
                    dayOfWeek = dayOfWeek,
                    routineNameOrId = routineNameOrId
                )
            )
        }
    }

    fun updatePlanCompletion(id: Int, completed: Boolean) {
        viewModelScope.launch {
            repository.updatePlanCompletion(id, completed)
        }
    }

    fun deletePlan(id: Int) {
        viewModelScope.launch {
            repository.deletePlan(id)
        }
    }

    fun clearAllPlans() {
        viewModelScope.launch {
            repository.clearAllPlans()
        }
    }

    fun generateDefault12WeekPlan() {
        viewModelScope.launch {
            repository.clearAllPlans()
            val days = listOf(
                "Push Day (Chest, Shoulders, Triceps)",
                "Pull Day (Back & Biceps)",
                "Rest Day",
                "Leg Day (Quads, Hamstrings, Calves)",
                "Active Recovery & Cardio",
                "Rest Day",
                "Full Body Stretch"
            )
            for (week in 1..12) {
                for (day in 1..7) {
                    val planName = days[day - 1]
                    repository.insertPlan(
                        WeeklyPlan(
                            weekNumber = week,
                            dayOfWeek = day,
                            routineNameOrId = planName,
                            isCompleted = false
                        )
                    )
                }
            }
        }
    }
}

data class DailySummaryPoint(
    val dayLabel: String,
    val waterMl: Double,
    val caffeineMg: Double,
    val caloriesKcal: Double,
    val proteinG: Double,
    val fatG: Double,
    val carbsG: Double,
    val exerciseMin: Double
)
