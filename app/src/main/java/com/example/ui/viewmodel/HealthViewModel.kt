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
import com.example.data.model.SleepLog
import com.example.data.model.WorkoutSession
import com.example.data.model.PersonalRecord
import com.example.data.model.CustomExercise
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

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt
import kotlinx.coroutines.flow.asStateFlow

class HealthViewModel(private val repository: HealthRepository, private val context: Context) : ViewModel() {

    private val prefs = context.getSharedPreferences("user_settings", Context.MODE_PRIVATE)

    // Accelerometer Step Counter (Pessimistic)
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val _stepsWalked = MutableStateFlow(prefs.getInt("steps_walked", 0))
    val stepsWalked = _stepsWalked.asStateFlow()

    private var lastStepTime = 0L

    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event == null || event.sensor.type != Sensor.TYPE_ACCELEROMETER) return
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val magnitude = sqrt(x * x + y * y + z * z)
            val currentTime = System.currentTimeMillis()

            // Strict & selective step detection (pessimistic threshold: 14.5 m/s^2)
            if (magnitude > 14.5f && (currentTime - lastStepTime) > 450L) {
                lastStepTime = currentTime
                val newSteps = _stepsWalked.value + 1
                _stepsWalked.value = newSteps
                prefs.edit().putInt("steps_walked", newSteps).apply()
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    fun addMockSteps(count: Int) {
        val newSteps = _stepsWalked.value + count
        _stepsWalked.value = newSteps
        prefs.edit().putInt("steps_walked", newSteps).apply()
    }

    fun resetSteps() {
        _stepsWalked.value = 0
        prefs.edit().putInt("steps_walked", 0).apply()
    }

    private val _userAge = MutableStateFlow(prefs.getInt("user_age", 25))
    val userAge = _userAge.asStateFlow()

    private val _userWeight = MutableStateFlow(prefs.getFloat("user_weight", 70.0f).toDouble())
    val userWeight = _userWeight.asStateFlow()

    private val _userWaist = MutableStateFlow(prefs.getFloat("user_waist", 80.0f).toDouble())
    val userWaist = _userWaist.asStateFlow()

    private val _userChest = MutableStateFlow(prefs.getFloat("user_chest", 95.0f).toDouble())
    val userChest = _userChest.asStateFlow()

    private val _userHips = MutableStateFlow(prefs.getFloat("user_hips", 90.0f).toDouble())
    val userHips = _userHips.asStateFlow()

    private val _userBiceps = MutableStateFlow(prefs.getFloat("user_biceps", 32.0f).toDouble())
    val userBiceps = _userBiceps.asStateFlow()

    private val _userThighs = MutableStateFlow(prefs.getFloat("user_thighs", 50.0f).toDouble())
    val userThighs = _userThighs.asStateFlow()

    private val _weightUnit = MutableStateFlow(prefs.getString("weight_unit", "kg") ?: "kg")
    val weightUnit = _weightUnit.asStateFlow()

    private val _gramsUnit = MutableStateFlow(prefs.getString("grams_unit", "g") ?: "g")
    val gramsUnit = _gramsUnit.asStateFlow()

    private val _volumeUnit = MutableStateFlow(prefs.getString("volume_unit", "ml") ?: "ml")
    val volumeUnit = _volumeUnit.asStateFlow()

    private val _googleUserSignedIn = MutableStateFlow(prefs.getBoolean("google_user_signed_in", false))
    val googleUserSignedIn = _googleUserSignedIn.asStateFlow()

    private val _googleUserName = MutableStateFlow(prefs.getString("google_user_name", "") ?: "")
    val googleUserName = _googleUserName.asStateFlow()

    private val _googleUserEmail = MutableStateFlow(prefs.getString("google_user_email", "") ?: "")
    val googleUserEmail = _googleUserEmail.asStateFlow()

    private val _googleUserPhoto = MutableStateFlow(prefs.getString("google_user_photo", "") ?: "")
    val googleUserPhoto = _googleUserPhoto.asStateFlow()

    fun updatePersonalDetails(
        age: Int,
        weight: Double,
        waist: Double,
        chest: Double,
        hips: Double,
        biceps: Double,
        thighs: Double
    ) {
        prefs.edit().apply {
            putInt("user_age", age)
            putFloat("user_weight", weight.toFloat())
            putFloat("user_waist", waist.toFloat())
            putFloat("user_chest", chest.toFloat())
            putFloat("user_hips", hips.toFloat())
            putFloat("user_biceps", biceps.toFloat())
            putFloat("user_thighs", thighs.toFloat())
            apply()
        }
        _userAge.value = age
        _userWeight.value = weight
        _userWaist.value = waist
        _userChest.value = chest
        _userHips.value = hips
        _userBiceps.value = biceps
        _userThighs.value = thighs
    }

    fun updateWeightUnit(unit: String) {
        prefs.edit().putString("weight_unit", unit).apply()
        _weightUnit.value = unit
    }

    fun updateGramsUnit(unit: String) {
        prefs.edit().putString("grams_unit", unit).apply()
        _gramsUnit.value = unit
    }

    fun updateVolumeUnit(unit: String) {
        prefs.edit().putString("volume_unit", unit).apply()
        _volumeUnit.value = unit
    }

    fun signInWithGoogle(name: String, email: String, photo: String) {
        prefs.edit().apply {
            putBoolean("google_user_signed_in", true)
            putString("google_user_name", name)
            putString("google_user_email", email)
            putString("google_user_photo", photo)
            apply()
        }
        _googleUserSignedIn.value = true
        _googleUserName.value = name
        _googleUserEmail.value = email
        _googleUserPhoto.value = photo
    }

    fun signOutFromGoogle() {
        prefs.edit().apply {
            putBoolean("google_user_signed_in", false)
            putString("google_user_name", "")
            putString("google_user_email", "")
            putString("google_user_photo", "")
            apply()
        }
        _googleUserSignedIn.value = false
        _googleUserName.value = ""
        _googleUserEmail.value = ""
        _googleUserPhoto.value = ""
    }

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

    val sleepLogs: StateFlow<List<SleepLog>> = repository.allSleepLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val workoutSessions: StateFlow<List<WorkoutSession>> = repository.allWorkoutSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val personalRecords: StateFlow<List<PersonalRecord>> = repository.allPersonalRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customExercises: StateFlow<List<CustomExercise>> = repository.allCustomExercises
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
        accelerometer?.let {
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(sensorEventListener)
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

    fun logSleep(durationHours: Double, quality: Int, notes: String? = null) {
        viewModelScope.launch {
            repository.insertSleepLog(SleepLog(durationHours = durationHours, quality = quality, notes = notes))
        }
    }

    fun deleteSleepLog(id: Int) {
        viewModelScope.launch {
            repository.deleteSleepLog(id)
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

    fun getTodaySleepDurationTotal(): Double {
        return sleepLogs.value.filter { isToday(it.timestamp) }.sumOf { it.durationHours }
    }

    fun getTodaySleepQualityAverage(): Double {
        val todaySleeps = sleepLogs.value.filter { isToday(it.timestamp) }
        if (todaySleeps.isEmpty()) return 0.0
        return todaySleeps.map { it.quality }.average()
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
            val sleepHours = sleepLogs.value.filter { isSameDay(it.timestamp, checkCal) }.sumOf { it.durationHours }
            val sleepQualityLogs = sleepLogs.value.filter { isSameDay(it.timestamp, checkCal) }
            val sleepQualityAvg = if (sleepQualityLogs.isEmpty()) 0.0 else sleepQualityLogs.map { it.quality }.average()

            result.add(
                DailySummaryPoint(
                    dayLabel = dayName,
                    waterMl = water,
                    caffeineMg = caffeine,
                    caloriesKcal = calories,
                    proteinG = protein,
                    fatG = fat,
                    carbsG = carbs,
                    exerciseMin = exercise,
                    sleepHours = sleepHours,
                    sleepQuality = sleepQualityAvg
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
        imagePath: String?,
        notes: String? = null
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
                    imagePath = imagePath,
                    notes = notes
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

    fun logWorkoutSession(name: String, durationMinutes: Int, exercisesJson: String, notes: String? = null) {
        viewModelScope.launch {
            repository.insertWorkoutSession(
                WorkoutSession(
                    name = name,
                    durationMinutes = durationMinutes,
                    exercisesJson = exercisesJson,
                    notes = notes
                )
            )
            // Also log to the general exercise_logs for dashboard integration!
            repository.insertExerciseLog(
                ExerciseLog(
                    type = "GYM",
                    exerciseName = name,
                    sets = null,
                    reps = null,
                    durationMinutes = durationMinutes,
                    distanceKm = null
                )
            )
        }
    }

    fun deleteWorkoutSession(id: Int) {
        viewModelScope.launch {
            repository.deleteWorkoutSession(id)
        }
    }

    fun addCustomExercise(
        name: String,
        bodyPart: String,
        equipment: String,
        level: String,
        specificInstruction: String? = null,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (name.isBlank()) {
            onError("Exercise name is mandatory!")
            return
        }
        if (bodyPart.isBlank()) {
            onError("Body part is mandatory!")
            return
        }
        if (equipment.isBlank()) {
            onError("Equipment is mandatory!")
            return
        }
        if (level.isBlank()) {
            onError("Level is mandatory!")
            return
        }
        viewModelScope.launch {
            try {
                repository.insertCustomExercise(
                    CustomExercise(
                        name = name.trim(),
                        bodyPart = bodyPart.trim(),
                        equipment = equipment.trim(),
                        level = level.trim(),
                        specificInstruction = specificInstruction?.trim()?.ifEmpty { null }
                    )
                )
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to save custom exercise")
            }
        }
    }

    fun deleteCustomExercise(id: Int) {
        viewModelScope.launch {
            repository.deleteCustomExercise(id)
        }
    }

    fun checkAndLogPersonalRecord(
        exerciseName: String,
        weight: Double,
        reps: Int,
        onNewRecord: (PersonalRecord) -> Unit
    ) {
        viewModelScope.launch {
            var newPRTriggered = false
            
            // Check weight record
            val bestWeightRecord = repository.getBestPersonalRecord(exerciseName, "weight")
            if (weight > 0.0 && (bestWeightRecord == null || weight > bestWeightRecord.value)) {
                val record = PersonalRecord(
                    exerciseName = exerciseName,
                    metricType = "weight",
                    value = weight,
                    reps = reps
                )
                repository.insertPersonalRecord(record)
                onNewRecord(record)
                newPRTriggered = true
            }
            
            // Check reps record (only if weight is not zero/empty or same as before but more reps)
            val bestRepsRecord = repository.getBestPersonalRecord(exerciseName, "reps")
            if (!newPRTriggered && reps > 0 && (bestRepsRecord == null || reps > bestRepsRecord.value.toInt())) {
                val record = PersonalRecord(
                    exerciseName = exerciseName,
                    metricType = "reps",
                    value = reps.toDouble(),
                    reps = reps
                )
                repository.insertPersonalRecord(record)
                onNewRecord(record)
            }
        }
    }

    fun deletePersonalRecord(id: Int) {
        viewModelScope.launch {
            repository.deletePersonalRecord(id)
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
    val exerciseMin: Double,
    val sleepHours: Double = 0.0,
    val sleepQuality: Double = 0.0
)
