package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.MealPlan
import kotlinx.coroutines.flow.Flow

@Dao
interface MealPlanDao {
    @Query("SELECT * FROM meal_plans ORDER BY id ASC")
    fun getAllMealPlans(): Flow<List<MealPlan>>

    @Query("SELECT * FROM meal_plans WHERE dayOfWeek = :day")
    fun getMealPlansForDay(day: String): Flow<List<MealPlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealPlan(mealPlan: MealPlan): Long

    @Delete
    suspend fun deleteMealPlan(mealPlan: MealPlan)

    @Query("DELETE FROM meal_plans WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM meal_plans WHERE dayOfWeek = :day AND mealType = :mealType")
    suspend fun clearMealSlot(day: String, mealType: String)

    @Query("DELETE FROM meal_plans")
    suspend fun clearAll()
}
