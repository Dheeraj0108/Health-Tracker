package com.example.data.db

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
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
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterDao {
    @Query("SELECT * FROM water_logs ORDER BY timestamp DESC")
    fun getAllWaterLogs(): Flow<List<WaterLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterLog(log: WaterLog)

    @Query("DELETE FROM water_logs WHERE id = :id")
    suspend fun deleteWaterLogById(id: Int)
}


@Dao
interface CaffeineDao {
    @Query("SELECT * FROM caffeine_logs ORDER BY timestamp DESC")
    fun getAllCaffeineLogs(): Flow<List<CaffeineLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCaffeineLog(log: CaffeineLog)

    @Query("DELETE FROM caffeine_logs WHERE id = :id")
    suspend fun deleteCaffeineLogById(id: Int)
}

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercise_logs ORDER BY timestamp DESC")
    fun getAllExerciseLogs(): Flow<List<ExerciseLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExerciseLog(log: ExerciseLog)

    @Query("DELETE FROM exercise_logs WHERE id = :id")
    suspend fun deleteExerciseLogById(id: Int)
}

@Dao
interface FoodDao {
    @Query("SELECT * FROM food_logs ORDER BY timestamp DESC")
    fun getAllFoodLogs(): Flow<List<FoodLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodLog(log: FoodLog)

    @Query("DELETE FROM food_logs WHERE id = :id")
    suspend fun deleteFoodLogById(id: Int)
}

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals ORDER BY timestamp DESC")
    fun getAllGoals(): Flow<List<Goal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: Goal)

    @Query("SELECT * FROM goals WHERE `key` = :key ORDER BY timestamp DESC LIMIT 1")
    suspend fun getGoalByKey(key: String): Goal?
}

@Dao
interface BodyDao {
    @Query("SELECT * FROM body_logs ORDER BY timestamp DESC")
    fun getAllBodyLogs(): Flow<List<BodyLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBodyLog(log: BodyLog)

    @Query("DELETE FROM body_logs WHERE id = :id")
    suspend fun deleteBodyLogById(id: Int)
}

@Dao
interface RoutineDao {
    @Query("SELECT * FROM exercise_routines ORDER BY timestamp DESC")
    fun getAllRoutines(): Flow<List<ExerciseRoutine>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: ExerciseRoutine)

    @Query("DELETE FROM exercise_routines WHERE id = :id")
    suspend fun deleteRoutineById(id: Int)
}

@Dao
interface PlanDao {
    @Query("SELECT * FROM weekly_plans ORDER BY weekNumber ASC, dayOfWeek ASC")
    fun getAllPlans(): Flow<List<WeeklyPlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: WeeklyPlan)

    @Query("UPDATE weekly_plans SET isCompleted = :completed WHERE id = :id")
    suspend fun updatePlanCompletion(id: Int, completed: Boolean)

    @Query("DELETE FROM weekly_plans WHERE id = :id")
    suspend fun deletePlanById(id: Int)

    @Query("DELETE FROM weekly_plans")
    suspend fun clearAllPlans()
}

@Dao
interface WorkoutSessionDao {
    @Query("SELECT * FROM workout_sessions ORDER BY timestamp DESC")
    fun getAllWorkoutSessions(): Flow<List<WorkoutSession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutSession(session: WorkoutSession)

    @Query("DELETE FROM workout_sessions WHERE id = :id")
    suspend fun deleteWorkoutSessionById(id: Int)
}

@Dao
interface SleepDao {
    @Query("SELECT * FROM sleep_logs ORDER BY timestamp DESC")
    fun getAllSleepLogs(): Flow<List<SleepLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleepLog(log: SleepLog)

    @Query("DELETE FROM sleep_logs WHERE id = :id")
    suspend fun deleteSleepLogById(id: Int)
}

@Database(
    entities = [
        WaterLog::class,
        CaffeineLog::class,
        ExerciseLog::class,
        FoodLog::class,
        Goal::class,
        BodyLog::class,
        ExerciseRoutine::class,
        WeeklyPlan::class,
        SleepLog::class,
        WorkoutSession::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun waterDao(): WaterDao
    abstract fun caffeineDao(): CaffeineDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun foodDao(): FoodDao
    abstract fun goalDao(): GoalDao
    abstract fun bodyDao(): BodyDao
    abstract fun routineDao(): RoutineDao
    abstract fun planDao(): PlanDao
    abstract fun sleepDao(): SleepDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
}
