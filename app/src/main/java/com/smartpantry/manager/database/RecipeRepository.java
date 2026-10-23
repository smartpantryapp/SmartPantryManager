package com.smartpantry.manager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.smartpantry.manager.models.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeRepository {

    private final PantryDatabaseHelper databaseHelper;

    public RecipeRepository(Context context) {
        databaseHelper = new PantryDatabaseHelper(
                context.getApplicationContext()
        );
    }

    // Save a recipe and its required ingredients together.
    public long addRecipe(
            Recipe recipe,
            List<RecipeIngredient> ingredients) {

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        long recipeId = -1;

        db.beginTransaction();

        try {
            ContentValues recipeValues = new ContentValues();
            recipeValues.put("name", recipe.getName().trim());
            recipeValues.put("instructions", recipe.getInstructions().trim());
            recipeValues.put(
                    "prep_time_minutes",
                    recipe.getPrepTimeMinutes()
            );

            recipeId = db.insertOrThrow(
                    PantryDatabaseHelper.TABLE_RECIPES,
                    null,
                    recipeValues
            );

            for (RecipeIngredient ingredient : ingredients) {
                ContentValues ingredientValues = new ContentValues();
                ingredientValues.put("recipe_id", recipeId);
                ingredientValues.put(
                        "ingredient_name",
                        ingredient.getName().trim()
                );
                ingredientValues.put(
                        "required_quantity",
                        ingredient.getRequiredQuantity()
                );
                ingredientValues.put("unit", ingredient.getUnit().trim());

                db.insertOrThrow(
                        PantryDatabaseHelper.TABLE_RECIPE_INGREDIENTS,
                        null,
                        ingredientValues
                );
            }

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }

        return recipeId;
    }

    // Retrieve all recipes.
    public List<Recipe> getAllRecipes() {

        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        try (Cursor cursor = db.query(
                PantryDatabaseHelper.TABLE_RECIPES,
                null,
                null,
                null,
                null,
                null,
                "name COLLATE NOCASE ASC")) {

            while (cursor.moveToNext()) {
                recipes.add(new Recipe(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("name")),
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("instructions")),
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "prep_time_minutes"))
                ));
            }
        }

        return recipes;
    }

    // Retrieve the ingredients required by one recipe.
    public List<RecipeIngredient> getIngredientsForRecipe(int recipeId) {

        List<RecipeIngredient> ingredients = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        try (Cursor cursor = db.query(
                PantryDatabaseHelper.TABLE_RECIPE_INGREDIENTS,
                null,
                "recipe_id = ?",
                new String[]{String.valueOf(recipeId)},
                null,
                null,
                "ingredient_name COLLATE NOCASE ASC")) {

            while (cursor.moveToNext()) {
                ingredients.add(new RecipeIngredient(
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "ingredient_name")),
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow(
                                        "required_quantity")),
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("unit"))
                ));
            }
        }

        return ingredients;
    }

    public void close() {
        databaseHelper.close();
    }

    // Represents one ingredient required by a recipe.
    public static class RecipeIngredient {

        private final String name;
        private final double requiredQuantity;
        private final String unit;

        public RecipeIngredient(
                String name,
                double requiredQuantity,
                String unit) {

            this.name = name;
            this.requiredQuantity = requiredQuantity;
            this.unit = unit;
        }

        public String getName() {
            return name;
        }

        public double getRequiredQuantity() {
            return requiredQuantity;
        }

        public String getUnit() {
            return unit;
        }
    }
}