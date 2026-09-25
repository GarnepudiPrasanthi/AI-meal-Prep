package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.DietTypes
import com.example.data.model.GroceryItem
import com.example.data.model.Ingredient
import com.example.data.model.IngredientCategories
import com.example.data.model.MealPlan
import com.example.data.model.Recipe
import com.example.data.model.RecipeIngredientItem
import com.example.data.model.RecipeMatchScore
import com.example.data.model.UserWeightProfile
import com.example.data.model.WeightEntry
import com.example.data.repository.TrimPrepRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TrimPrepUiState(
    val selectedTab: Int = 0, // 0: Dashboard, 1: Ingredients/Pantry, 2: Recipes, 3: Meal Plan, 4: Weight, 5: Grocery
    val selectedRecipeForDetail: Recipe? = null,
    val selectedDietFilter: String = DietTypes.ALL,
    val ingredientCategoryFilter: String = "All",
    val ingredientSearchQuery: String = "",
    val recipeSearchQuery: String = "",
    val isGeneratingAi: Boolean = false,
    val aiStatusMessage: String? = null,
    val snackbarMessage: String? = null,
    val showScheduleDialogForRecipe: Recipe? = null,
    val showAddIngredientDialog: Boolean = false,
    val showLogWeightDialog: Boolean = false,
    val showProfileDialog: Boolean = false
)

class TrimPrepViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TrimPrepRepository

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = TrimPrepRepository(db)
    }

    private val _uiState = MutableStateFlow(TrimPrepUiState())
    val uiState: StateFlow<TrimPrepUiState> = _uiState.asStateFlow()

    val allIngredients: StateFlow<List<Ingredient>> = repository.allIngredients
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val availableIngredients: StateFlow<List<Ingredient>> = repository.availableIngredients
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRecipeMatches: StateFlow<List<RecipeMatchScore>> = repository.recipeMatches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredRecipeMatches: StateFlow<List<RecipeMatchScore>> = combine(
        repository.recipeMatches,
        _uiState
    ) { matches, state ->
        matches.filter { match ->
            val matchesDiet = state.selectedDietFilter == DietTypes.ALL ||
                    match.recipe.dietType.equals(state.selectedDietFilter, ignoreCase = true)
            val matchesQuery = state.recipeSearchQuery.isBlank() ||
                    match.recipe.title.contains(state.recipeSearchQuery, ignoreCase = true) ||
                    match.recipe.description.contains(state.recipeSearchQuery, ignoreCase = true)
            matchesDiet && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMealPlans: StateFlow<List<MealPlan>> = repository.allMealPlans
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserWeightProfile> = repository.userProfile
        .combine(MutableStateFlow(Unit)) { profile, _ -> profile ?: UserWeightProfile() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserWeightProfile())

    val weightHistory: StateFlow<List<WeightEntry>> = repository.weightHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val groceryItems: StateFlow<List<GroceryItem>> = repository.groceryItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = index, selectedRecipeForDetail = null)
    }

    fun openRecipeDetail(recipe: Recipe) {
        _uiState.value = _uiState.value.copy(selectedRecipeForDetail = recipe)
    }

    fun closeRecipeDetail() {
        _uiState.value = _uiState.value.copy(selectedRecipeForDetail = null)
    }

    fun setDietFilter(filter: String) {
        _uiState.value = _uiState.value.copy(selectedDietFilter = filter)
    }

    fun setIngredientCategoryFilter(cat: String) {
        _uiState.value = _uiState.value.copy(ingredientCategoryFilter = cat)
    }

    fun setIngredientSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(ingredientSearchQuery = query)
    }

    fun setRecipeSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(recipeSearchQuery = query)
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    fun showSnackbar(message: String) {
        _uiState.value = _uiState.value.copy(snackbarMessage = message)
    }

    // Ingredient actions
    fun toggleIngredientAvailability(id: Long, current: Boolean) {
        viewModelScope.launch {
            repository.toggleIngredientAvailability(id, current)
        }
    }

    fun addIngredient(name: String, category: String, quantity: Double, unit: String) {
        viewModelScope.launch {
            repository.addIngredient(
                Ingredient(
                    name = name.trim(),
                    category = category,
                    quantity = quantity,
                    unit = unit,
                    isAvailable = true
                )
            )
            _uiState.value = _uiState.value.copy(
                showAddIngredientDialog = false,
                snackbarMessage = "Added $name to your pantry"
            )
        }
    }

    fun deleteIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            repository.deleteIngredient(ingredient)
            showSnackbar("Removed ${ingredient.name}")
        }
    }

    // Recipe actions
    fun toggleFavorite(recipe: Recipe) {
        viewModelScope.launch {
            repository.toggleFavoriteRecipe(recipe.id, recipe.isFavorite)
        }
    }

    fun addMissingToGrocery(recipe: Recipe, missing: List<RecipeIngredientItem>) {
        viewModelScope.launch {
            repository.addMissingIngredientsToGrocery(recipe.title, missing)
            showSnackbar("Added ${missing.size} missing items to Grocery List!")
        }
    }

    // Schedule meal plan
    fun promptScheduleRecipe(recipe: Recipe) {
        _uiState.value = _uiState.value.copy(showScheduleDialogForRecipe = recipe)
    }

    fun dismissScheduleDialog() {
        _uiState.value = _uiState.value.copy(showScheduleDialogForRecipe = null)
    }

    fun scheduleMeal(day: String, mealType: String, recipe: Recipe, containerNum: Int = 1) {
        viewModelScope.launch {
            repository.scheduleMeal(
                dayOfWeek = day,
                mealType = mealType,
                recipe = recipe,
                containerNote = "Batch Container #$containerNum (Ready to heat)"
            )
            _uiState.value = _uiState.value.copy(
                showScheduleDialogForRecipe = null,
                snackbarMessage = "Scheduled ${recipe.title} for $day $mealType"
            )
        }
    }

    fun deleteMealPlan(mealPlan: MealPlan) {
        viewModelScope.launch {
            repository.deleteMealPlan(mealPlan)
            showSnackbar("Removed meal from ${mealPlan.dayOfWeek}")
        }
    }

    fun clearAllMealPlans() {
        viewModelScope.launch {
            repository.clearMealPlans()
            showSnackbar("Cleared meal prep plan")
        }
    }

    // AI Generation
    fun generateAiMealPrep(customPromptAddon: String = "") {
        val currentProfile = userProfile.value
        val targetCalories = currentProfile.dailyCalorieTarget / 3 // Target ~1/3 of daily budget for lunch/dinner prep
        val targetDiet = currentProfile.dietPreference
        val batchServings = currentProfile.prepBatchDays

        _uiState.value = _uiState.value.copy(
            isGeneratingAi = true,
            aiStatusMessage = "Analyzing available ingredients & macro deficit targets..."
        )

        viewModelScope.launch {
            try {
                val result = repository.generateAiMealPrep(
                    targetCalories = targetCalories,
                    targetDiet = targetDiet,
                    batchServings = batchServings
                )
                result.onSuccess { recipe ->
                    _uiState.value = _uiState.value.copy(
                        isGeneratingAi = false,
                        aiStatusMessage = null,
                        selectedRecipeForDetail = recipe,
                        snackbarMessage = "Generated: ${recipe.title}!"
                    )
                }.onFailure { err ->
                    _uiState.value = _uiState.value.copy(
                        isGeneratingAi = false,
                        aiStatusMessage = null,
                        snackbarMessage = "Generation note: ${err.localizedMessage ?: "Synthesized fallback meal prep"}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isGeneratingAi = false,
                    aiStatusMessage = null,
                    snackbarMessage = "Error generating recipe: ${e.message}"
                )
            }
        }
    }

    // Dialog state controllers
    fun setShowAddIngredientDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showAddIngredientDialog = show)
    }

    fun setShowLogWeightDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showLogWeightDialog = show)
    }

    fun setShowProfileDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showProfileDialog = show)
    }

    fun logWeight(weightKg: Double, note: String) {
        viewModelScope.launch {
            repository.logWeight(weightKg, note)
            _uiState.value = _uiState.value.copy(
                showLogWeightDialog = false,
                snackbarMessage = "Logged weight: $weightKg kg"
            )
        }
    }

    fun deleteWeightEntry(entry: WeightEntry) {
        viewModelScope.launch {
            repository.deleteWeightEntry(entry)
        }
    }

    fun updateProfile(
        currentWeightKg: Double,
        targetWeightKg: Double,
        calorieTarget: Int,
        proteinTargetG: Int,
        diet: String,
        batchContainers: Int
    ) {
        viewModelScope.launch {
            val updated = userProfile.value.copy(
                currentWeightKg = currentWeightKg,
                targetWeightKg = targetWeightKg,
                dailyCalorieTarget = calorieTarget,
                dailyProteinTargetG = proteinTargetG,
                dietPreference = diet,
                prepBatchDays = batchContainers
            )
            repository.saveProfile(updated)
            _uiState.value = _uiState.value.copy(
                showProfileDialog = false,
                snackbarMessage = "Weight loss profile updated!"
            )
        }
    }

    // Grocery actions
    fun toggleGroceryItem(id: Long, current: Boolean) {
        viewModelScope.launch {
            repository.toggleGroceryItem(id, current)
        }
    }

    fun addGroceryItem(name: String, quantity: String, category: String) {
        viewModelScope.launch {
            repository.addGroceryItem(name, quantity, category)
            showSnackbar("Added $name to grocery list")
        }
    }

    fun clearCompletedGrocery() {
        viewModelScope.launch {
            repository.clearCompletedGrocery()
            showSnackbar("Removed completed items")
        }
    }

    fun deleteGroceryItem(item: GroceryItem) {
        viewModelScope.launch {
            repository.deleteGroceryItem(item)
        }
    }
}
