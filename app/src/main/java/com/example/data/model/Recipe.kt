package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray
import org.json.JSONObject

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val dietType: String, // High Protein, Low Carb, Keto, Mediterranean, Balanced
    val prepTimeMinutes: Int,
    val cookTimeMinutes: Int,
    val servingsCount: Int = 4, // Typical meal prep batch is 4-5 containers
    val caloriesPerServing: Int,
    val proteinGrams: Int,
    val carbsGrams: Int,
    val fatGrams: Int,
    val fiberGrams: Int,
    val storageFridgeDays: Int = 4,
    val storageFreezerMonths: Int = 2,
    val reheatingNotes: String = "Microwave 2-3 mins with damp paper towel or warm in skillet with 1 tbsp water.",
    val weightLossTip: String = "High protein and fiber preserve lean muscle while keeping you full in a deficit.",
    val ingredientsJson: String = "[]", // JSON array of items {name, amount, unit}
    val instructionsJson: String = "[]", // JSON array of step strings
    val isFavorite: Boolean = false,
    val isAiGenerated: Boolean = false
) {
    fun parseIngredients(): List<RecipeIngredientItem> {
        val list = mutableListOf<RecipeIngredientItem>()
        try {
            val jsonArray = JSONArray(ingredientsJson)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    RecipeIngredientItem(
                        name = obj.optString("name", ""),
                        amount = obj.optString("amount", "1"),
                        unit = obj.optString("unit", ""),
                        isPantryStaple = obj.optBoolean("isPantryStaple", false)
                    )
                )
            }
        } catch (_: Exception) {
            // fallback if plain text lines
            if (ingredientsJson.isNotBlank() && !ingredientsJson.startsWith("[")) {
                ingredientsJson.lines().filter { it.isNotBlank() }.forEach { line ->
                    list.add(RecipeIngredientItem(name = line.trim(), amount = "", unit = ""))
                }
            }
        }
        return list
    }

    fun parseInstructions(): List<String> {
        val list = mutableListOf<String>()
        try {
            val jsonArray = JSONArray(instructionsJson)
            for (i in 0 until jsonArray.length()) {
                list.add(jsonArray.getString(i))
            }
        } catch (_: Exception) {
            if (instructionsJson.isNotBlank() && !instructionsJson.startsWith("[")) {
                instructionsJson.lines().filter { it.isNotBlank() }.forEach { line ->
                    list.add(line.trim())
                }
            }
        }
        return list
    }
}

data class RecipeIngredientItem(
    val name: String,
    val amount: String,
    val unit: String,
    val isPantryStaple: Boolean = false
)

data class RecipeMatchScore(
    val recipe: Recipe,
    val matchPercentage: Int, // 0..100
    val matchedIngredients: List<String>,
    val missingIngredients: List<RecipeIngredientItem>
)

object DietTypes {
    const val ALL = "All"
    const val HIGH_PROTEIN = "High Protein"
    const val LOW_CARB = "Low Carb"
    const val BALANCED = "Balanced Deficit"
    const val MEDITERRANEAN = "Mediterranean"
    const val KETO = "Keto"

    val allList = listOf(ALL, HIGH_PROTEIN, LOW_CARB, BALANCED, MEDITERRANEAN, KETO)
}
