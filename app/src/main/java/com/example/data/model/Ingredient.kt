package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingredients")
data class Ingredient(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String, // Protein, Vegetables, Grains & Carbs, Healthy Fats, Dairy, Condiments/Spices
    val quantity: Double = 1.0,
    val unit: String = "serving", // g, kg, pcs, cups, oz
    val isAvailable: Boolean = true,
    val isExpiringSoon: Boolean = false,
    val notes: String = ""
)

object IngredientCategories {
    const val PROTEIN = "Lean Protein"
    const val VEGETABLES = "Vegetables & Greens"
    const val GRAINS_CARBS = "Smart Carbs & Grains"
    const val HEALTHY_FATS = "Healthy Fats & Nuts"
    const val DAIRY = "Dairy & Alternatives"
    const val CONDIMENTS = "Seasonings & Sauces"

    val all = listOf(
        PROTEIN,
        VEGETABLES,
        GRAINS_CARBS,
        HEALTHY_FATS,
        DAIRY,
        CONDIMENTS
    )
}
