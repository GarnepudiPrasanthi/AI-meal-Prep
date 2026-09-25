package com.example.data.repository

import com.example.data.ai.GeminiMealPrepService
import com.example.data.local.AppDatabase
import com.example.data.model.GroceryItem
import com.example.data.model.Ingredient
import com.example.data.model.MealPlan
import com.example.data.model.Recipe
import com.example.data.model.RecipeIngredientItem
import com.example.data.model.RecipeMatchScore
import com.example.data.model.UserWeightProfile
import com.example.data.model.WeightEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Locale

class TrimPrepRepository(
    private val database: AppDatabase,
    private val geminiService: GeminiMealPrepService = GeminiMealPrepService()
) {
    private val ingredientDao = database.ingredientDao()
    private val recipeDao = database.recipeDao()
    private val mealPlanDao = database.mealPlanDao()
    private val weightDao = database.weightDao()
    private val groceryDao = database.groceryDao()

    // Ingredients
    val allIngredients: Flow<List<Ingredient>> = ingredientDao.getAllIngredients()
    val availableIngredients: Flow<List<Ingredient>> = ingredientDao.getAvailableIngredients()

    suspend fun toggleIngredientAvailability(id: Long, current: Boolean) {
        ingredientDao.updateAvailability(id, !current)
    }

    suspend fun addIngredient(ingredient: Ingredient): Long {
        return ingredientDao.insertIngredient(ingredient)
    }

    suspend fun updateIngredient(ingredient: Ingredient) {
        ingredientDao.updateIngredient(ingredient)
    }

    suspend fun deleteIngredient(ingredient: Ingredient) {
        ingredientDao.deleteIngredient(ingredient)
    }

    // Recipes & Smart Match
    val allRecipes: Flow<List<Recipe>> = recipeDao.getAllRecipes()
    val favoriteRecipes: Flow<List<Recipe>> = recipeDao.getFavoriteRecipes()

    fun getRecipeById(id: Long): Flow<Recipe?> = recipeDao.getRecipeByIdFlow(id)

    suspend fun toggleFavoriteRecipe(id: Long, current: Boolean) {
        recipeDao.updateFavorite(id, !current)
    }

    suspend fun saveRecipe(recipe: Recipe): Long {
        return recipeDao.insertRecipe(recipe)
    }

    suspend fun deleteRecipe(recipe: Recipe) {
        recipeDao.deleteRecipe(recipe)
    }

    /**
     * Computes the real-time match scores of all recipes based on currently available ingredients.
     */
    val recipeMatches: Flow<List<RecipeMatchScore>> = combine(
        allRecipes,
        availableIngredients
    ) { recipes, availableList ->
        val availableNames = availableList.map { it.name.trim().lowercase(Locale.ROOT) }

        recipes.map { recipe ->
            val ingredients = recipe.parseIngredients()
            if (ingredients.isEmpty()) {
                RecipeMatchScore(
                    recipe = recipe,
                    matchPercentage = 100,
                    matchedIngredients = emptyList(),
                    missingIngredients = emptyList()
                )
            } else {
                val matched = mutableListOf<String>()
                val missing = mutableListOf<RecipeIngredientItem>()

                for (req in ingredients) {
                    val reqName = req.name.trim().lowercase(Locale.ROOT)
                    // Check if any available ingredient matches or is contained in reqName
                    val isPresent = availableNames.any { avail ->
                        avail.contains(reqName) || reqName.contains(avail) ||
                                (reqName.contains("chicken") && avail.contains("chicken")) ||
                                (reqName.contains("egg") && avail.contains("egg")) ||
                                (reqName.contains("tuna") && avail.contains("tuna")) ||
                                (reqName.contains("turkey") && avail.contains("turkey")) ||
                                (reqName.contains("spinach") && avail.contains("spinach")) ||
                                (reqName.contains("broccoli") && avail.contains("broccoli")) ||
                                (reqName.contains("quinoa") && avail.contains("quinoa")) ||
                                (reqName.contains("rice") && avail.contains("rice")) ||
                                (reqName.contains("oil") && avail.contains("oil"))
                    }

                    if (isPresent) {
                        matched.add(req.name)
                    } else {
                        missing.add(req)
                    }
                }

                val score = ((matched.size.toDouble() / ingredients.size.toDouble()) * 100).toInt()
                RecipeMatchScore(
                    recipe = recipe,
                    matchPercentage = score,
                    matchedIngredients = matched,
                    missingIngredients = missing
                )
            }
        }.sortedByDescending { it.matchPercentage }
    }

    // AI Generation
    suspend fun generateAiMealPrep(
        targetCalories: Int,
        targetDiet: String,
        batchServings: Int
    ): Result<Recipe> {
        val available = ingredientDao.getAvailableIngredientsList()
        val result = geminiService.generateMealPrepRecipe(
            availableIngredients = available,
            targetCalories = targetCalories,
            targetDiet = targetDiet,
            batchServings = batchServings
        )
        result.onSuccess { generatedRecipe ->
            recipeDao.insertRecipe(generatedRecipe)
        }
        return result
    }

    // Meal Plan
    val allMealPlans: Flow<List<MealPlan>> = mealPlanDao.getAllMealPlans()

    suspend fun scheduleMeal(
        dayOfWeek: String,
        mealType: String,
        recipe: Recipe,
        containerNote: String = "Container #1"
    ) {
        mealPlanDao.clearMealSlot(dayOfWeek, mealType)
        mealPlanDao.insertMealPlan(
            MealPlan(
                dayOfWeek = dayOfWeek,
                mealType = mealType,
                recipeId = recipe.id,
                recipeTitle = recipe.title,
                calories = recipe.caloriesPerServing,
                proteinG = recipe.proteinGrams,
                containerNote = containerNote
            )
        )
    }

    suspend fun deleteMealPlan(mealPlan: MealPlan) {
        mealPlanDao.deleteMealPlan(mealPlan)
    }

    suspend fun clearMealPlans() {
        mealPlanDao.clearAll()
    }

    // Weight & Goals
    val userProfile: Flow<UserWeightProfile?> = weightDao.getProfile()
    val weightHistory: Flow<List<WeightEntry>> = weightDao.getAllEntries()

    suspend fun saveProfile(profile: UserWeightProfile) {
        weightDao.saveProfile(profile)
    }

    suspend fun logWeight(weightKg: Double, note: String) {
        weightDao.insertEntry(WeightEntry(weightKg = weightKg, note = note))
        val current = weightDao.getProfileSync() ?: UserWeightProfile()
        weightDao.saveProfile(current.copy(currentWeightKg = weightKg))
    }

    suspend fun deleteWeightEntry(entry: WeightEntry) {
        weightDao.deleteEntry(entry)
    }

    // Grocery
    val groceryItems: Flow<List<GroceryItem>> = groceryDao.getAllGroceryItems()

    suspend fun addMissingIngredientsToGrocery(recipeTitle: String, missing: List<RecipeIngredientItem>) {
        val items = missing.map {
            GroceryItem(
                name = it.name,
                quantity = "${it.amount} ${it.unit}".trim(),
                category = "Meal Prep Ingredients",
                isChecked = false,
                associatedRecipeTitle = recipeTitle
            )
        }
        groceryDao.insertAll(items)
    }

    suspend fun addGroceryItem(name: String, quantity: String, category: String) {
        groceryDao.insertItem(
            GroceryItem(
                name = name,
                quantity = quantity,
                category = category
            )
        )
    }

    suspend fun toggleGroceryItem(id: Long, current: Boolean) {
        groceryDao.updateChecked(id, !current)
    }

    suspend fun clearCompletedGrocery() {
        groceryDao.deleteCompleted()
    }

    suspend fun deleteGroceryItem(item: GroceryItem) {
        groceryDao.deleteItem(item)
    }
}
