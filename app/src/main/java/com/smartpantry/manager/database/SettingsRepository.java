package com.smartpantry.manager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SettingsRepository {

    private static final String KEY_EXPIRY_ALERTS =
            "expiry_alerts_enabled";

    private final PantryDatabaseHelper databaseHelper;

    public SettingsRepository(Context context) {
        databaseHelper = new PantryDatabaseHelper(
                context.getApplicationContext()
        );
    }

    /**
     * Read the saved expiry-alert preference.
     * Defaults to enabled if the setting is unavailable.
     */
    public boolean areExpiryAlertsEnabled() {

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        try (Cursor cursor = db.query(
                PantryDatabaseHelper.TABLE_SETTINGS,
                new String[]{"setting_value"},
                "setting_key = ?",
                new String[]{KEY_EXPIRY_ALERTS},
                null,
                null,
                null
        )) {
            if (cursor.moveToFirst()) {
                return Boolean.parseBoolean(cursor.getString(0));
            }
        }

        return true;
    }

    /**
     * Save the expiry-alert preference.
     */
    public void setExpiryAlertsEnabled(boolean enabled) {

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("setting_key", KEY_EXPIRY_ALERTS);
        values.put("setting_value", String.valueOf(enabled));

        db.insertWithOnConflict(
                PantryDatabaseHelper.TABLE_SETTINGS,
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        );
    }

    /**
     * Release the database helper when it is no longer needed.
     */
    public void close() {
        databaseHelper.close();
    }
}