package com.smartpantry.manager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.smartpantry.manager.models.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class PantryRepository {

    private final PantryDatabaseHelper databaseHelper;

    public PantryRepository(Context context) {
        databaseHelper = new PantryDatabaseHelper(
                context.getApplicationContext()
        );
    }

    // Add a new ingredient to the pantry.
    public long addIngredient(Ingredient ingredient) {

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", ingredient.getName().trim());
        values.put("quantity", ingredient.getQuantity());
        values.put("unit", ingredient.getUnit().trim());
        values.put("expiry_date", ingredient.getExpiryDate());
        values.put("created_at", ingredient.getCreatedAt());

        return db.insert(
                PantryDatabaseHelper.TABLE_INGREDIENTS,
                null,
                values
        );
    }

    // Retrieve all pantry ingredients.
    public List<Ingredient> getAllIngredients() {

        List<Ingredient> ingredients = new ArrayList<>();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        try (Cursor cursor = db.query(
                PantryDatabaseHelper.TABLE_INGREDIENTS,
                null,
                null,
                null,
                null,
                null,
                "name COLLATE NOCASE ASC")) {

            while (cursor.moveToNext()) {

                Ingredient ingredient = new Ingredient(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("name")),
                        cursor.getDouble(
                                cursor.getColumnIndexOrThrow("quantity")),
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("unit")),
                        cursor.getString(
                                cursor.getColumnIndexOrThrow("expiry_date")),
                        cursor.getLong(
                                cursor.getColumnIndexOrThrow("created_at"))
                );

                ingredients.add(ingredient);
            }
        }

        return ingredients;
    }

    // Update an existing ingredient.
    public int updateIngredient(Ingredient ingredient) {

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", ingredient.getName().trim());
        values.put("quantity", ingredient.getQuantity());
        values.put("unit", ingredient.getUnit().trim());
        values.put("expiry_date", ingredient.getExpiryDate());

        return db.update(
                PantryDatabaseHelper.TABLE_INGREDIENTS,
                values,
                "id = ?",
                new String[]{String.valueOf(ingredient.getId())}
        );
    }

    // Delete an ingredient by its database ID.
    public int deleteIngredient(int ingredientId) {

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        return db.delete(
                PantryDatabaseHelper.TABLE_INGREDIENTS,
                "id = ?",
                new String[]{String.valueOf(ingredientId)}
        );
    }

    // Close the database helper when it is no longer needed.
    public void close() {
        databaseHelper.close();
    }
}