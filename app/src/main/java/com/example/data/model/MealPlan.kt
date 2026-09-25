package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_plans")
data class MealPlan(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dayOfWeek: String, // Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday
    val mealType: String, // Lunch, Dinner, Breakfast, Snack
    val recipeId: Long,
    val recipeTitle: String,
    val calories: Int,
    val proteinG: Int,
    val containerNote: String = "Container #1"
)

object MealDays {
    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    val mealTypes = listOf("Lunch", "Dinner", "Breakfast", "Snack")
}
