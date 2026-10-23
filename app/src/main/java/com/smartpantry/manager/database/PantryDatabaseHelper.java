package com.smartpantry.manager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PantryDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "smart_pantry.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_INGREDIENTS = "ingredients";
    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_RECIPE_INGREDIENTS =
            "recipe_ingredients";
    public static final String TABLE_SETTINGS = "settings";

    public PantryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Enable SQLite foreign-key constraints.
     */
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    /**
     * Create the database tables and indexes.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Stores ingredients available in the user's pantry.
        db.execSQL(
                "CREATE TABLE " + TABLE_INGREDIENTS + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "quantity REAL NOT NULL CHECK(quantity > 0), " +
                        "unit TEXT NOT NULL, " +
                        "expiry_date TEXT, " +
                        "created_at INTEGER NOT NULL)"
        );

        // Stores recipe names, preparation instructions and preparation time.
        db.execSQL(
                "CREATE TABLE " + TABLE_RECIPES + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL UNIQUE, " +
                        "instructions TEXT NOT NULL, " +
                        "prep_time_minutes INTEGER NOT NULL DEFAULT 0 " +
                        "CHECK(prep_time_minutes >= 0))"
        );

        // Stores the ingredients and quantities required for each recipe.
        db.execSQL(
                "CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "recipe_id INTEGER NOT NULL, " +
                        "ingredient_name TEXT NOT NULL, " +
                        "required_quantity REAL NOT NULL " +
                        "CHECK(required_quantity > 0), " +
                        "unit TEXT NOT NULL, " +
                        "FOREIGN KEY(recipe_id) REFERENCES " +
                        TABLE_RECIPES + "(id) ON DELETE CASCADE)"
        );

        // Speeds up searches for pantry ingredients by name.
        db.execSQL(
                "CREATE INDEX idx_ingredients_name ON " +
                        TABLE_INGREDIENTS + "(name COLLATE NOCASE)"
        );

        // Speeds up retrieval of ingredients belonging to a recipe.
        db.execSQL(
                "CREATE INDEX idx_recipe_ingredients_recipe ON " +
                        TABLE_RECIPE_INGREDIENTS + "(recipe_id)"
        );

        // Stores application preferences.
        db.execSQL(
                "CREATE TABLE " + TABLE_SETTINGS + " (" +
                        "setting_key TEXT PRIMARY KEY, " +
                        "setting_value TEXT NOT NULL)"
        );

        insertDefaultSetting(
                db,
                "expiry_alerts_enabled",
                "true"
        );
    }

    /**
     * Insert a default application setting.
     */
    private void insertDefaultSetting(
            SQLiteDatabase db, String key, String value) {

        ContentValues values = new ContentValues();
        values.put("setting_key", key);
        values.put("setting_value", value);

        db.insertOrThrow(TABLE_SETTINGS, null, values);
    }

    /**
     * Handle future database schema upgrades.
     */
    @Override
    public void onUpgrade(
            SQLiteDatabase db, int oldVersion, int newVersion) {

        // Add version-specific database migrations here
        // when the database schema changes.
    }
}