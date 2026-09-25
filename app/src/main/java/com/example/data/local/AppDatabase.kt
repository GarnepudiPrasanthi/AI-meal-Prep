package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.GroceryDao
import com.example.data.local.dao.IngredientDao
import com.example.data.local.dao.MealPlanDao
import com.example.data.local.dao.RecipeDao
import com.example.data.local.dao.WeightDao
import com.example.data.model.GroceryItem
import com.example.data.model.Ingredient
import com.example.data.model.MealPlan
import com.example.data.model.Recipe
import com.example.data.model.UserWeightProfile
import com.example.data.model.WeightEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Ingredient::class,
        Recipe::class,
        MealPlan::class,
        UserWeightProfile::class,
        WeightEntry::class,
        GroceryItem::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ingredientDao(): IngredientDao
    abstract fun recipeDao(): RecipeDao
    abstract fun mealPlanDao(): MealPlanDao
    abstract fun weightDao(): WeightDao
    abstract fun groceryDao(): GroceryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "trimprep_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        if (database.ingredientDao().count() == 0) {
                            populateInitialData(database)
                        }
                    }
                }
            }

            private suspend fun populateInitialData(database: AppDatabase) {
                database.ingredientDao().insertAll(SeedData.getDefaultIngredients())
                database.recipeDao().insertAll(SeedData.getDefaultRecipes())
                database.weightDao().saveProfile(UserWeightProfile())
                
                // Add sample weight progress entries
                val now = System.currentTimeMillis()
                val oneDay = 24 * 60 * 60 * 1000L
                database.weightDao().insertEntry(WeightEntry(timestamp = now - (14 * oneDay), weightKg = 80.2, note = "Started meal prepping"))
                database.weightDao().insertEntry(WeightEntry(timestamp = now - (7 * oneDay), weightKg = 79.1, note = "Completed 1st week batch"))
                database.weightDao().insertEntry(WeightEntry(timestamp = now, weightKg = 78.0, note = "High protein satiety working!"))

                // Add sample meal plans for current week
                val recipes = database.recipeDao().getAllRecipesList()
                if (recipes.isNotEmpty()) {
                    val r1 = recipes[0]
                    val r2 = recipes.getOrNull(1) ?: r1
                    database.mealPlanDao().insertMealPlan(
                        MealPlan(
                            dayOfWeek = "Monday",
                            mealType = "Lunch",
                            recipeId = r1.id,
                            recipeTitle = r1.title,
                            calories = r1.caloriesPerServing,
                            proteinG = r1.proteinGrams,
                            containerNote = "Container #1 (Fridge)"
                        )
                    )
                    database.mealPlanDao().insertMealPlan(
                        MealPlan(
                            dayOfWeek = "Tuesday",
                            mealType = "Lunch",
                            recipeId = r1.id,
                            recipeTitle = r1.title,
                            calories = r1.caloriesPerServing,
                            proteinG = r1.proteinGrams,
                            containerNote = "Container #2 (Fridge)"
                        )
                    )
                    database.mealPlanDao().insertMealPlan(
                        MealPlan(
                            dayOfWeek = "Wednesday",
                            mealType = "Dinner",
                            recipeId = r2.id,
                            recipeTitle = r2.title,
                            calories = r2.caloriesPerServing,
                            proteinG = r2.proteinGrams,
                            containerNote = "Container #1 (Fridge)"
                        )
                    )
                }
            }
        }
    }
}
