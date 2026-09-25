package com.example.data.local

import com.example.data.model.DietTypes
import com.example.data.model.Ingredient
import com.example.data.model.IngredientCategories
import com.example.data.model.Recipe
import org.json.JSONArray
import org.json.JSONObject

object SeedData {

    fun getDefaultIngredients(): List<Ingredient> = listOf(
        Ingredient(name = "Chicken Breast", category = IngredientCategories.PROTEIN, quantity = 800.0, unit = "g", isAvailable = true),
        Ingredient(name = "Eggs", category = IngredientCategories.PROTEIN, quantity = 12.0, unit = "pcs", isAvailable = true),
        Ingredient(name = "Lean Ground Turkey", category = IngredientCategories.PROTEIN, quantity = 500.0, unit = "g", isAvailable = true),
        Ingredient(name = "Canned Tuna", category = IngredientCategories.PROTEIN, quantity = 3.0, unit = "cans", isAvailable = true),
        Ingredient(name = "Salmon Fillets", category = IngredientCategories.PROTEIN, quantity = 400.0, unit = "g", isAvailable = false),
        Ingredient(name = "Firm Tofu", category = IngredientCategories.PROTEIN, quantity = 400.0, unit = "g", isAvailable = false),
        Ingredient(name = "Greek Yogurt (0% Fat)", category = IngredientCategories.DAIRY, quantity = 500.0, unit = "g", isAvailable = true),

        Ingredient(name = "Broccoli", category = IngredientCategories.VEGETABLES, quantity = 2.0, unit = "heads", isAvailable = true, isExpiringSoon = true),
        Ingredient(name = "Baby Spinach", category = IngredientCategories.VEGETABLES, quantity = 250.0, unit = "g", isAvailable = true),
        Ingredient(name = "Bell Peppers", category = IngredientCategories.VEGETABLES, quantity = 3.0, unit = "pcs", isAvailable = true),
        Ingredient(name = "Zucchini", category = IngredientCategories.VEGETABLES, quantity = 2.0, unit = "pcs", isAvailable = false),
        Ingredient(name = "Cauliflower", category = IngredientCategories.VEGETABLES, quantity = 1.0, unit = "head", isAvailable = true),
        Ingredient(name = "Sweet Potatoes", category = IngredientCategories.VEGETABLES, quantity = 4.0, unit = "pcs", isAvailable = true),
        Ingredient(name = "Carrots", category = IngredientCategories.VEGETABLES, quantity = 500.0, unit = "g", isAvailable = true),
        Ingredient(name = "Cucumbers", category = IngredientCategories.VEGETABLES, quantity = 2.0, unit = "pcs", isAvailable = false),

        Ingredient(name = "Quinoa", category = IngredientCategories.GRAINS_CARBS, quantity = 500.0, unit = "g", isAvailable = true),
        Ingredient(name = "Brown Rice", category = IngredientCategories.GRAINS_CARBS, quantity = 1.0, unit = "kg", isAvailable = true),
        Ingredient(name = "Rolled Oats", category = IngredientCategories.GRAINS_CARBS, quantity = 800.0, unit = "g", isAvailable = true),
        Ingredient(name = "Black Beans (Canned)", category = IngredientCategories.GRAINS_CARBS, quantity = 2.0, unit = "cans", isAvailable = true),
        Ingredient(name = "Chickpeas (Canned)", category = IngredientCategories.GRAINS_CARBS, quantity = 2.0, unit = "cans", isAvailable = false),

        Ingredient(name = "Extra Virgin Olive Oil", category = IngredientCategories.HEALTHY_FATS, quantity = 500.0, unit = "ml", isAvailable = true),
        Ingredient(name = "Avocado", category = IngredientCategories.HEALTHY_FATS, quantity = 2.0, unit = "pcs", isAvailable = false),
        Ingredient(name = "Almonds", category = IngredientCategories.HEALTHY_FATS, quantity = 200.0, unit = "g", isAvailable = true),

        Ingredient(name = "Garlic", category = IngredientCategories.CONDIMENTS, quantity = 1.0, unit = "bulb", isAvailable = true),
        Ingredient(name = "Lemon", category = IngredientCategories.CONDIMENTS, quantity = 3.0, unit = "pcs", isAvailable = true),
        Ingredient(name = "Low-Sodium Soy Sauce", category = IngredientCategories.CONDIMENTS, quantity = 250.0, unit = "ml", isAvailable = true),
        Ingredient(name = "Dijon Mustard", category = IngredientCategories.CONDIMENTS, quantity = 1.0, unit = "jar", isAvailable = true),
        Ingredient(name = "Smoked Paprika", category = IngredientCategories.CONDIMENTS, quantity = 1.0, unit = "jar", isAvailable = true)
    )

    private fun jsonIngredients(vararg items: Triple<String, String, String>): String {
        val array = JSONArray()
        for (item in items) {
            val obj = JSONObject()
            obj.put("name", item.first)
            obj.put("amount", item.second)
            obj.put("unit", item.third)
            array.put(obj)
        }
        return array.toString()
    }

    private fun jsonSteps(vararg steps: String): String {
        val array = JSONArray()
        for (step in steps) {
            array.put(step)
        }
        return array.toString()
    }

    fun getDefaultRecipes(): List<Recipe> = listOf(
        Recipe(
            title = "Lemon Herb Chicken & Roasted Broccoli Bowls",
            description = "High-volume, ultra-lean meal prep staple. Marinated juicy chicken breasts paired with garlic-charred broccoli and fluffy quinoa.",
            dietType = DietTypes.HIGH_PROTEIN,
            prepTimeMinutes = 15,
            cookTimeMinutes = 25,
            servingsCount = 4,
            caloriesPerServing = 385,
            proteinGrams = 44,
            carbsGrams = 24,
            fatGrams = 12,
            fiberGrams = 6,
            storageFridgeDays = 4,
            storageFreezerMonths = 2,
            reheatingNotes = "Microwave 2 minutes covered with a damp paper towel to retain chicken moisture. Add a fresh squeeze of lemon.",
            weightLossTip = "High protein (44g) keeps peptide YY elevated, suppressing hunger hormones for up to 5 hours.",
            ingredientsJson = jsonIngredients(
                Triple("Chicken Breast", "700", "g"),
                Triple("Broccoli", "2", "heads"),
                Triple("Quinoa", "1", "cup"),
                Triple("Garlic", "4", "cloves"),
                Triple("Lemon", "2", "pcs"),
                Triple("Extra Virgin Olive Oil", "2", "tbsp"),
                Triple("Smoked Paprika", "1", "tsp")
            ),
            instructionsJson = jsonSteps(
                "Cook quinoa in 2 cups of water with a pinch of salt until water is absorbed (approx 15 mins). Fluff and let cool.",
                "Cut chicken breasts into bite-sized cutlets. Toss with 1 tbsp olive oil, minced garlic, lemon juice, smoked paprika, salt, and pepper.",
                "Cut broccoli into bite-sized florets. Toss with remaining olive oil and lemon zest. Spread on a baking sheet.",
                "Roast broccoli at 400°F (200°C) for 18-20 minutes until edges are crispy.",
                "Sear seasoned chicken in a large skillet over medium-high heat for 6-8 minutes until golden and cooked through (165°F internal).",
                "Evenly divide cooled quinoa, roasted broccoli, and chicken across 4 glass meal prep containers. Seal and refrigerate."
            ),
            isFavorite = true
        ),
        Recipe(
            title = "Fiesta Ground Turkey & Black Bean Quinoa Prep",
            description = "Savory taco-spiced lean turkey layered with fiber-rich black beans, charred sweet bell peppers, and quinoa.",
            dietType = DietTypes.HIGH_PROTEIN,
            prepTimeMinutes = 15,
            cookTimeMinutes = 20,
            servingsCount = 4,
            caloriesPerServing = 415,
            proteinGrams = 39,
            carbsGrams = 36,
            fatGrams = 11,
            fiberGrams = 9,
            storageFridgeDays = 5,
            storageFreezerMonths = 3,
            reheatingNotes = "Microwave 2.5 minutes. Great topped cold with fresh salsa or a dollop of non-fat Greek yogurt.",
            weightLossTip = "9 grams of dietary fiber stabilizes post-meal blood glucose spikes and prevents mid-afternoon energy crashes.",
            ingredientsJson = jsonIngredients(
                Triple("Lean Ground Turkey", "500", "g"),
                Triple("Black Beans (Canned)", "1", "can"),
                Triple("Bell Peppers", "2", "pcs"),
                Triple("Quinoa", "1", "cup"),
                Triple("Garlic", "3", "cloves"),
                Triple("Smoked Paprika", "1", "tbsp"),
                Triple("Extra Virgin Olive Oil", "1", "tbsp")
            ),
            instructionsJson = jsonSteps(
                "Rinse black beans and drain thoroughly. Cook quinoa according to package directions.",
                "Dice bell peppers and mince garlic cloves.",
                "In a large skillet, heat 1 tbsp olive oil over medium. Add ground turkey and break apart with spatula until browned.",
                "Stir in minced garlic, diced bell peppers, smoked paprika, cumin, salt, and pepper. Cook 5 mins until peppers soften slightly.",
                "Fold in drained black beans and simmer for 3 minutes to blend spices.",
                "Portion 1/2 cup cooked quinoa into 4 meal prep containers and top with equal amounts of the fiesta turkey mixture."
            ),
            isFavorite = false
        ),
        Recipe(
            title = "Garlic Herb Salmon with Cauliflower Mash & Spinach",
            description = "Omega-3 rich salmon fillets paired with creamy velvety garlic cauliflower mash and tender wilted baby spinach.",
            dietType = DietTypes.LOW_CARB,
            prepTimeMinutes = 20,
            cookTimeMinutes = 20,
            servingsCount = 4,
            caloriesPerServing = 370,
            proteinGrams = 38,
            carbsGrams = 11,
            fatGrams = 18,
            fiberGrams = 5,
            storageFridgeDays = 3,
            storageFreezerMonths = 1,
            reheatingNotes = "Best reheated gently at 50% microwave power for 2 minutes to prevent drying out salmon, or served chilled.",
            weightLossTip = "Essential EPA/DHA fatty acids support insulin sensitivity and decrease exercise-induced muscle inflammation.",
            ingredientsJson = jsonIngredients(
                Triple("Salmon Fillets", "500", "g"),
                Triple("Cauliflower", "1", "head"),
                Triple("Baby Spinach", "200", "g"),
                Triple("Garlic", "4", "cloves"),
                Triple("Lemon", "1", "pc"),
                Triple("Extra Virgin Olive Oil", "1.5", "tbsp")
            ),
            instructionsJson = jsonSteps(
                "Chop cauliflower into florets and steam for 10-12 minutes until completely fork-tender.",
                "Transfer steamed cauliflower to a food processor or bowl with minced garlic, 1 tbsp olive oil, salt, and pepper. Blend until silky smooth.",
                "Quickly wilt baby spinach in a hot pan with a splash of water for 60 seconds; drain excess liquid.",
                "Season salmon fillets with lemon juice, salt, and black pepper. Pan sear in remaining oil 4 minutes skin-side down, flip and cook 3 minutes.",
                "Distribute cauliflower mash, wilted spinach, and salmon into 4 containers. Garnish with lemon slices."
            ),
            isFavorite = true
        ),
        Recipe(
            title = "Zesty Tuna, Chickpea & Spinach Power Bowls",
            description = "No-cook prep! Protein-packed wild tuna combined with fiber-loaded chickpeas, crisp diced peppers, and a zesty Dijon lemon vinaigrette.",
            dietType = DietTypes.MEDITERRANEAN,
            prepTimeMinutes = 15,
            cookTimeMinutes = 0,
            servingsCount = 4,
            caloriesPerServing = 340,
            proteinGrams = 36,
            carbsGrams = 28,
            fatGrams = 8,
            fiberGrams = 7,
            storageFridgeDays = 4,
            storageFreezerMonths = 0,
            reheatingNotes = "No reheating required! Enjoy cold straight from the fridge — perfect for office or travel lunches.",
            weightLossTip = "Zero cooking required eliminates prep friction and removes excuses when sticking to a calorie deficit.",
            ingredientsJson = jsonIngredients(
                Triple("Canned Tuna", "3", "cans"),
                Triple("Chickpeas (Canned)", "1", "can"),
                Triple("Baby Spinach", "150", "g"),
                Triple("Bell Peppers", "1", "pc"),
                Triple("Dijon Mustard", "1.5", "tbsp"),
                Triple("Lemon", "1", "pc"),
                Triple("Extra Virgin Olive Oil", "1", "tbsp")
            ),
            instructionsJson = jsonSteps(
                "Drain canned tuna and flake gently with a fork in a mixing bowl.",
                "Rinse and drain chickpeas. Finely dice bell pepper.",
                "In a small ramekin, whisk together lemon juice, Dijon mustard, olive oil, salt, black pepper, and dried oregano.",
                "Layer baby spinach at the bottom of 4 containers.",
                "Toss tuna, chickpeas, and bell pepper with the Dijon vinaigrette.",
                "Spoon the tuna chickpea salad over the spinach bed. Seal tightly."
            ),
            isFavorite = false
        ),
        Recipe(
            title = "High-Protein Egg White & Sweet Potato Hash Bake",
            description = "Pre-portioned grab-and-go breakfast bake with roasted sweet potato cubes, spinach, and seasoned lean egg whites.",
            dietType = DietTypes.HIGH_PROTEIN,
            prepTimeMinutes = 15,
            cookTimeMinutes = 30,
            servingsCount = 4,
            caloriesPerServing = 295,
            proteinGrams = 32,
            carbsGrams = 26,
            fatGrams = 6,
            fiberGrams = 5,
            storageFridgeDays = 4,
            storageFreezerMonths = 2,
            reheatingNotes = "Microwave 90 seconds. Tastes freshly baked and pairs wonderfully with hot sauce.",
            weightLossTip = "Starting the day with 30g+ protein significantly blunts nighttime binge cravings.",
            ingredientsJson = jsonIngredients(
                Triple("Eggs", "8", "pcs"),
                Triple("Sweet Potatoes", "2", "pcs"),
                Triple("Baby Spinach", "100", "g"),
                Triple("Bell Peppers", "1", "pc"),
                Triple("Garlic", "2", "cloves"),
                Triple("Smoked Paprika", "1", "tsp")
            ),
            instructionsJson = jsonSteps(
                "Peel sweet potatoes and dice into small 1/2-inch cubes. Dice bell peppers.",
                "Toss potato cubes with paprika, salt, and pepper. Roast at 400°F (200°C) for 18 minutes until tender.",
                "In a bowl, whisk 4 whole eggs and 4 egg whites with minced garlic and a pinch of salt.",
                "In a lightly greased baking dish, layer the roasted sweet potatoes, diced peppers, and fresh spinach.",
                "Pour the whisked egg mixture evenly over the top.",
                "Bake at 375°F (190°C) for 22-25 minutes until eggs are set. Slice into 4 equal meal prep portions."
            ),
            isFavorite = false
        ),
        Recipe(
            title = "Sesame Ginger Chicken & Brown Rice Cauliflower Blend",
            description = "Volume-boosted brown rice blended 50/50 with cauliflower rice to cut carbs in half while maximizing plate volume.",
            dietType = DietTypes.BALANCED,
            prepTimeMinutes = 20,
            cookTimeMinutes = 20,
            servingsCount = 4,
            caloriesPerServing = 390,
            proteinGrams = 42,
            carbsGrams = 32,
            fatGrams = 9,
            fiberGrams = 6,
            storageFridgeDays = 4,
            storageFreezerMonths = 2,
            reheatingNotes = "Microwave 2 minutes with container lid placed loosely on top to trap steam.",
            weightLossTip = "The 'volumetrics' strategy of blending cauliflower rice into grain rice halves the calorie density without feeling restricted.",
            ingredientsJson = jsonIngredients(
                Triple("Chicken Breast", "600", "g"),
                Triple("Brown Rice", "1", "cup"),
                Triple("Cauliflower", "0.5", "head"),
                Triple("Carrots", "2", "pcs"),
                Triple("Low-Sodium Soy Sauce", "3", "tbsp"),
                Triple("Garlic", "3", "cloves"),
                Triple("Extra Virgin Olive Oil", "1", "tbsp")
            ),
            instructionsJson = jsonSteps(
                "Cook brown rice. Grate half a cauliflower head into fine 'rice' grains.",
                "Steam cauliflower rice in a pan for 4 minutes, then fold into hot cooked brown rice to create the 50/50 high-volume base.",
                "Grate carrots into thin matchsticks. Cut chicken breast into strips.",
                "Stir-fry chicken in olive oil over high heat for 6 mins. Add garlic, carrots, and soy sauce, cooking for 3 more minutes.",
                "Divide the 50/50 rice blend into 4 containers and spoon sesame ginger chicken on top."
            ),
            isFavorite = true
        )
    )
}
