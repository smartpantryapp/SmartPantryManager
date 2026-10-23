package com.smartpantry.manager.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.smartpantry.manager.models.Recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeSeeder {

    private final RecipeRepository recipeRepository;
    private final PantryDatabaseHelper databaseHelper;

    public RecipeSeeder(Context context) {
        recipeRepository = new RecipeRepository(context);
        databaseHelper = new PantryDatabaseHelper(
                context.getApplicationContext());
    }

    public void seedRecipes() {

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        // Avoid inserting the starter recipes more than once.
        try (Cursor cursor = db.query(
                PantryDatabaseHelper.TABLE_RECIPES,
                new String[]{"id"},
                null, null, null, null, null,
                "1")) {

            if (cursor.moveToFirst()) {
                return;
            }
        }

        List<SeedRecipe> recipes = createRecipes();

        for (SeedRecipe seed : recipes) {
            recipeRepository.addRecipe(
                    new Recipe(
                            seed.name,
                            seed.instructions,
                            seed.prepTime
                    ),
                    seed.ingredients
            );
        }
    }

    private List<SeedRecipe> createRecipes() {

        List<SeedRecipe> recipes = new ArrayList<>();

        recipes.add(recipe(
                "Tomato Omelette", 10,
                "1. Beat the eggs.\n2. Chop the tomato.\n"
                        + "3. Cook both in a lightly oiled pan until set.",
                item("egg", 2, "piece"),
                item("tomato", 1, "piece"),
                item("salt", 1, "pinch")
        ));

        recipes.add(recipe(
                "Scrambled Eggs", 8,
                "1. Beat the eggs.\n2. Heat a pan.\n"
                        + "3. Stir the eggs gently until cooked.",
                item("egg", 2, "piece"),
                item("milk", 30, "ml"),
                item("salt", 1, "pinch")
        ));

        recipes.add(recipe(
                "Garlic Tomato Pasta", 25,
                "1. Boil the pasta.\n2. Cook garlic and chopped tomatoes.\n"
                        + "3. Mix the sauce with the drained pasta.",
                item("pasta", 200, "g"),
                item("tomato", 2, "piece"),
                item("garlic", 2, "clove")
        ));

        recipes.add(recipe(
                "Cheese Omelette", 10,
                "1. Beat the eggs.\n2. Pour into a warm pan.\n"
                        + "3. Add cheese and fold when the eggs set.",
                item("egg", 2, "piece"),
                item("cheese", 30, "g"),
                item("salt", 1, "pinch")
        ));

        recipes.add(recipe(
                "Banana Pancakes", 15,
                "1. Mash the banana.\n2. Mix with egg, flour and milk.\n"
                        + "3. Cook small portions in a pan until golden.",
                item("banana", 1, "piece"),
                item("egg", 1, "piece"),
                item("flour", 100, "g"),
                item("milk", 100, "ml")
        ));

        recipes.add(recipe(
                "Simple French Toast", 10,
                "1. Beat the egg with milk.\n2. Dip bread in the mixture.\n"
                        + "3. Fry both sides until golden.",
                item("bread", 2, "piece"),
                item("egg", 1, "piece"),
                item("milk", 50, "ml")
        ));

        recipes.add(recipe(
                "Garlic Fried Rice", 15,
                "1. Use cooked rice.\n2. Fry garlic briefly.\n"
                        + "3. Add rice and stir until heated through.",
                item("rice", 250, "g"),
                item("garlic", 2, "clove"),
                item("oil", 1, "tbsp")
        ));

        recipes.add(recipe(
                "Tomato Cheese Sandwich", 8,
                "1. Slice the tomato and cheese.\n"
                        + "2. Place between bread slices and serve.",
                item("bread", 2, "piece"),
                item("tomato", 1, "piece"),
                item("cheese", 30, "g")
        ));

        recipes.add(recipe(
                "Creamy Mushroom Pasta", 25,
                "1. Boil the pasta.\n2. Cook sliced mushrooms.\n"
                        + "3. Add cream and mix with the pasta.",
                item("pasta", 200, "g"),
                item("mushroom", 100, "g"),
                item("cream", 100, "ml")
        ));

        recipes.add(recipe(
                "Potato Egg Hash", 25,
                "1. Dice and cook the potato until tender.\n"
                        + "2. Add the eggs and cook until set.",
                item("potato", 200, "g"),
                item("egg", 2, "piece"),
                item("oil", 1, "tbsp")
        ));

        recipes.add(recipe(
                "Carrot Soup", 30,
                "1. Chop the carrots and onion.\n"
                        + "2. Simmer in water until soft.\n"
                        + "3. Blend carefully until smooth.",
                item("carrot", 200, "g"),
                item("onion", 1, "piece"),
                item("water", 500, "ml")
        ));

        recipes.add(recipe(
                "Apple Oatmeal", 10,
                "1. Simmer oats in milk until soft.\n"
                        + "2. Chop the apple and add before serving.",
                item("oats", 50, "g"),
                item("milk", 200, "ml"),
                item("apple", 1, "piece")
        ));

        recipes.add(recipe(
                "Peanut Butter Banana Toast", 5,
                "1. Toast the bread.\n2. Spread peanut butter.\n"
                        + "3. Add sliced banana.",
                item("bread", 2, "piece"),
                item("peanut butter", 2, "tbsp"),
                item("banana", 1, "piece")
        ));

        recipes.add(recipe(
                "Spinach Scramble", 12,
                "1. Lightly cook the spinach.\n"
                        + "2. Add beaten eggs and scramble until cooked.",
                item("spinach", 50, "g"),
                item("egg", 2, "piece"),
                item("oil", 1, "tsp")
        ));

        recipes.add(recipe(
                "Lemon Garlic Chicken", 30,
                "1. Season the chicken with lemon and garlic.\n"
                        + "2. Cook thoroughly in a pan until no longer pink.",
                item("chicken", 200, "g"),
                item("lemon", 1, "piece"),
                item("garlic", 2, "clove")
        ));

        recipes.add(recipe(
                "Bean Tomato Stew", 25,
                "1. Cook the onion and tomato.\n"
                        + "2. Add cooked beans and simmer until hot.",
                item("beans", 200, "g"),
                item("tomato", 2, "piece"),
                item("onion", 1, "piece")
        ));

        recipes.add(recipe(
                "Cucumber Tomato Salad", 10,
                "1. Wash and chop the vegetables.\n"
                        + "2. Mix with lemon juice and serve.",
                item("cucumber", 1, "piece"),
                item("tomato", 2, "piece"),
                item("lemon", 1, "piece")
        ));

        recipes.add(recipe(
                "Cheesy Baked Potato", 35,
                "1. Bake the potato until tender.\n"
                        + "2. Split it open and add cheese.",
                item("potato", 250, "g"),
                item("cheese", 40, "g")
        ));

        recipes.add(recipe(
                "Apple Banana Smoothie", 5,
                "1. Peel and chop the fruit.\n"
                        + "2. Blend with milk until smooth.",
                item("apple", 1, "piece"),
                item("banana", 1, "piece"),
                item("milk", 200, "ml")
        ));

        recipes.add(recipe(
                "Flour Flatbread", 20,
                "1. Mix flour, water and salt into a dough.\n"
                        + "2. Knead, divide and roll into flat rounds.\n"
                        + "3. Cook both sides in a hot pan.",
                item("flour", 200, "g"),
                item("water", 120, "ml"),
                item("salt", 1, "pinch")
        ));

        return recipes;
    }

    private SeedRecipe recipe(
            String name,
            int prepTime,
            String instructions,
            RecipeRepository.RecipeIngredient... ingredients) {

        return new SeedRecipe(
                name,
                prepTime,
                instructions,
                Arrays.asList(ingredients)
        );
    }

    private RecipeRepository.RecipeIngredient item(
            String name,
            double quantity,
            String unit) {

        return new RecipeRepository.RecipeIngredient(
                name, quantity, unit);
    }

    private static class SeedRecipe {

        final String name;
        final int prepTime;
        final String instructions;
        final List<RecipeRepository.RecipeIngredient> ingredients;

        SeedRecipe(
                String name,
                int prepTime,
                String instructions,
                List<RecipeRepository.RecipeIngredient> ingredients) {

            this.name = name;
            this.prepTime = prepTime;
            this.instructions = instructions;
            this.ingredients = ingredients;
        }
    }

    public void close() {
        recipeRepository.close();
        databaseHelper.close();
    }
}