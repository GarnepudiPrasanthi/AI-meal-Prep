package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.MonitorWeight
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.dialogs.AddIngredientDialog
import com.example.ui.dialogs.EditProfileDialog
import com.example.ui.dialogs.LogWeightDialog
import com.example.ui.dialogs.ScheduleMealDialog
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.GroceryScreen
import com.example.ui.screens.IngredientsScreen
import com.example.ui.screens.MealPlannerScreen
import com.example.ui.screens.RecipeDetailScreen
import com.example.ui.screens.RecipesScreen
import com.example.ui.screens.WeightGoalScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.TrimPrepViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: TrimPrepViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                TrimPrepApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrimPrepApp(viewModel: TrimPrepViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allIngredients by viewModel.allIngredients.collectAsStateWithLifecycle()
    val availableIngredients by viewModel.availableIngredients.collectAsStateWithLifecycle()
    val recipeMatches by viewModel.filteredRecipeMatches.collectAsStateWithLifecycle()
    val allRecipeMatchesRaw by viewModel.allRecipeMatches.collectAsStateWithLifecycle()
    val mealPlans by viewModel.allMealPlans.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val weightHistory by viewModel.weightHistory.collectAsStateWithLifecycle()
    val groceryItems by viewModel.groceryItems.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    // If recipe detail is active, show the detail screen
    val detailRecipe = uiState.selectedRecipeForDetail
    if (detailRecipe != null) {
        RecipeDetailScreen(
            recipe = detailRecipe,
            availableIngredients = allIngredients,
            onBack = { viewModel.closeRecipeDetail() },
            onToggleFavorite = { viewModel.toggleFavorite(it) },
            onSchedule = { viewModel.promptScheduleRecipe(it) },
            onAddMissingToGrocery = { recipe, missing -> viewModel.addMissingToGrocery(recipe, missing) }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = when (uiState.selectedTab) {
                                0 -> "TrimPrep"
                                1 -> "My Fridge & Pantry"
                                2 -> "Meal Prep Ideas"
                                3 -> "Weekly Schedule"
                                4 -> "Deficit & Weight"
                                5 -> "Smart Grocery List"
                                else -> "TrimPrep"
                            },
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            )
                        )
                    },
                    actions = {
                        // Grocery shortcut with badge
                        IconButton(
                            onClick = { viewModel.selectTab(5) },
                            modifier = Modifier.testTag("topbar_grocery_btn")
                        ) {
                            val uncompletedGrocery = groceryItems.count { !it.isChecked }
                            BadgedBox(
                                badge = {
                                    if (uncompletedGrocery > 0) {
                                        Badge { Text("$uncompletedGrocery") }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (uiState.selectedTab == 5) Icons.Default.ShoppingCart else Icons.Outlined.ShoppingCart,
                                    contentDescription = "Grocery List",
                                    tint = if (uiState.selectedTab == 5) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Edit Deficit Profile icon
                        IconButton(
                            onClick = { viewModel.setShowProfileDialog(true) },
                            modifier = Modifier.testTag("topbar_profile_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Goals",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    NavigationBarItem(
                        selected = uiState.selectedTab == 0,
                        onClick = { viewModel.selectTab(0) },
                        icon = {
                            Icon(
                                imageVector = if (uiState.selectedTab == 0) Icons.Default.Dashboard else Icons.Outlined.Dashboard,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home") },
                        modifier = Modifier.testTag("nav_home")
                    )

                    NavigationBarItem(
                        selected = uiState.selectedTab == 1,
                        onClick = { viewModel.selectTab(1) },
                        icon = {
                            Icon(
                                imageVector = if (uiState.selectedTab == 1) Icons.Default.Kitchen else Icons.Outlined.Kitchen,
                                contentDescription = "Pantry"
                            )
                        },
                        label = { Text("Pantry") },
                        modifier = Modifier.testTag("nav_pantry")
                    )

                    NavigationBarItem(
                        selected = uiState.selectedTab == 2,
                        onClick = { viewModel.selectTab(2) },
                        icon = {
                            Icon(
                                imageVector = if (uiState.selectedTab == 2) Icons.Default.RestaurantMenu else Icons.Outlined.RestaurantMenu,
                                contentDescription = "Suggest"
                            )
                        },
                        label = { Text("Recipes") },
                        modifier = Modifier.testTag("nav_recipes")
                    )

                    NavigationBarItem(
                        selected = uiState.selectedTab == 3,
                        onClick = { viewModel.selectTab(3) },
                        icon = {
                            Icon(
                                imageVector = if (uiState.selectedTab == 3) Icons.Default.CalendarMonth else Icons.Outlined.CalendarMonth,
                                contentDescription = "Planner"
                            )
                        },
                        label = { Text("Planner") },
                        modifier = Modifier.testTag("nav_planner")
                    )

                    NavigationBarItem(
                        selected = uiState.selectedTab == 4,
                        onClick = { viewModel.selectTab(4) },
                        icon = {
                            Icon(
                                imageVector = if (uiState.selectedTab == 4) Icons.AutoMirrored.Filled.TrendingDown else Icons.Outlined.MonitorWeight,
                                contentDescription = "Deficit"
                            )
                        },
                        label = { Text("Deficit") },
                        modifier = Modifier.testTag("nav_deficit")
                    )
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (uiState.selectedTab) {
                    0 -> DashboardScreen(
                        userProfile = userProfile,
                        availableIngredientsCount = availableIngredients.size,
                        recipeMatches = allRecipeMatchesRaw,
                        mealPlans = mealPlans,
                        onNavigateToRecipes = { viewModel.selectTab(2) },
                        onNavigateToIngredients = { viewModel.selectTab(1) },
                        onNavigateToMealPlan = { viewModel.selectTab(3) },
                        onNavigateToWeight = { viewModel.selectTab(4) },
                        onOpenRecipeDetail = { viewModel.openRecipeDetail(it) },
                        onGenerateAiMealPrep = { viewModel.generateAiMealPrep() },
                        onLogWeightClick = { viewModel.setShowLogWeightDialog(true) },
                        onEditProfileClick = { viewModel.setShowProfileDialog(true) }
                    )
                    1 -> IngredientsScreen(
                        ingredients = allIngredients,
                        selectedCategory = uiState.ingredientCategoryFilter,
                        searchQuery = uiState.ingredientSearchQuery,
                        onCategorySelected = { viewModel.setIngredientCategoryFilter(it) },
                        onSearchQueryChange = { viewModel.setIngredientSearchQuery(it) },
                        onToggleAvailability = { id, current -> viewModel.toggleIngredientAvailability(id, current) },
                        onDeleteIngredient = { viewModel.deleteIngredient(it) },
                        onAddIngredientClick = { viewModel.setShowAddIngredientDialog(true) },
                        onQuickAdd = { name, cat -> viewModel.addIngredient(name, cat, 1.0, "serving") }
                    )
                    2 -> RecipesScreen(
                        recipeMatches = recipeMatches,
                        selectedDiet = uiState.selectedDietFilter,
                        searchQuery = uiState.recipeSearchQuery,
                        isGeneratingAi = uiState.isGeneratingAi,
                        aiStatusMessage = uiState.aiStatusMessage,
                        onSelectDiet = { viewModel.setDietFilter(it) },
                        onSearchQueryChange = { viewModel.setRecipeSearchQuery(it) },
                        onOpenRecipeDetail = { viewModel.openRecipeDetail(it) },
                        onToggleFavorite = { viewModel.toggleFavorite(it) },
                        onScheduleRecipe = { viewModel.promptScheduleRecipe(it) },
                        onAddMissingToGrocery = { recipe, missing -> viewModel.addMissingToGrocery(recipe, missing) },
                        onGenerateAiRecipe = { viewModel.generateAiMealPrep() }
                    )
                    3 -> MealPlannerScreen(
                        mealPlans = mealPlans,
                        userProfile = userProfile,
                        onNavigateToRecipes = { viewModel.selectTab(2) },
                        onDeleteMealPlan = { viewModel.deleteMealPlan(it) },
                        onClearAllPlans = { viewModel.clearAllMealPlans() }
                    )
                    4 -> WeightGoalScreen(
                        userProfile = userProfile,
                        weightHistory = weightHistory,
                        onLogWeightClick = { viewModel.setShowLogWeightDialog(true) },
                        onEditProfileClick = { viewModel.setShowProfileDialog(true) },
                        onDeleteEntry = { viewModel.deleteWeightEntry(it) }
                    )
                    5 -> GroceryScreen(
                        groceryItems = groceryItems,
                        onToggleItem = { id, current -> viewModel.toggleGroceryItem(id, current) },
                        onDeleteItem = { viewModel.deleteGroceryItem(it) },
                        onAddItem = { name, qty, cat -> viewModel.addGroceryItem(name, qty, cat) },
                        onClearCompleted = { viewModel.clearCompletedGrocery() }
                    )
                }
            }
        }
    }

    // Dialogs
    if (uiState.showAddIngredientDialog) {
        AddIngredientDialog(
            onDismiss = { viewModel.setShowAddIngredientDialog(false) },
            onConfirm = { name, category, quantity, unit ->
                viewModel.addIngredient(name, category, quantity, unit)
            }
        )
    }

    if (uiState.showLogWeightDialog) {
        LogWeightDialog(
            currentWeight = userProfile.currentWeightKg,
            onDismiss = { viewModel.setShowLogWeightDialog(false) },
            onConfirm = { weightKg, note ->
                viewModel.logWeight(weightKg, note)
            }
        )
    }

    if (uiState.showProfileDialog) {
        EditProfileDialog(
            profile = userProfile,
            onDismiss = { viewModel.setShowProfileDialog(false) },
            onSave = { cw, tw, cal, pro, diet, batch ->
                viewModel.updateProfile(cw, tw, cal, pro, diet, batch)
            }
        )
    }

    uiState.showScheduleDialogForRecipe?.let { recipeToSchedule ->
        ScheduleMealDialog(
            recipe = recipeToSchedule,
            onDismiss = { viewModel.dismissScheduleDialog() },
            onConfirm = { day, mealType, containerNum ->
                viewModel.scheduleMeal(day, mealType, recipeToSchedule, containerNum)
            }
        )
    }
}
