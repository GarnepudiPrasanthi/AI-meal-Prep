package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weight_profile")
data class UserWeightProfile(
    @PrimaryKey
    val id: Int = 1,
    val currentWeightKg: Double = 78.0,
    val targetWeightKg: Double = 70.0,
    val heightCm: Double = 175.0,
    val dailyCalorieTarget: Int = 1650,
    val dailyProteinTargetG: Int = 135,
    val dietPreference: String = "High Protein",
    val prepBatchDays: Int = 4, // 4 days prep batch
    val activityLevel: String = "Moderate" // Sedentary, Light, Moderate, Active
)

@Entity(tableName = "weight_entries")
data class WeightEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val weightKg: Double,
    val note: String = ""
)

@Entity(tableName = "grocery_items")
data class GroceryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val quantity: String,
    val category: String,
    val isChecked: Boolean = false,
    val associatedRecipeTitle: String = ""
)
