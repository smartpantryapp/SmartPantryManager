package com.smartpantry.manager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.smartpantry.manager.database.PantryDatabaseHelper;
import com.smartpantry.manager.database.RecipeSeeder;
import com.smartpantry.manager.database.SettingsRepository;

public class MainActivity extends AppCompatActivity {

    private PantryDatabaseHelper databaseHelper;
    private RecipeSeeder recipeSeeder;

    private Button btnViewPantry;
    private Button btnAddIngredient;
    private Button btnSuggestedRecipes;
    private Button btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main), (view, insets) -> {

                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars());

                    view.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                });

        initialiseDatabase();
        initialiseViews();
        setupNavigation();
        updateExpiryReminderSchedule();
    }

    private void updateExpiryReminderSchedule() {
        SettingsRepository settingsRepository = null;

        try {
            settingsRepository = new SettingsRepository(
                    getApplicationContext()
            );

            if (settingsRepository.areExpiryAlertsEnabled()) {
                ExpiryReminderScheduler.schedule(this);
            } else {
                ExpiryReminderScheduler.cancel(this);
            }

        } catch (Exception e) {
            android.util.Log.e(
                    "SmartPantry",
                    "Unable to update expiry reminder schedule",
                    e
            );
        } finally {
            if (settingsRepository != null) {
                settingsRepository.close();
            }
        }
    }

    private void initialiseViews() {
        btnViewPantry = findViewById(R.id.btnViewPantry);
        btnAddIngredient = findViewById(R.id.btnAddIngredient);
        btnSuggestedRecipes = findViewById(R.id.btnSuggestedRecipes);
        btnSettings = findViewById(R.id.btnSettings);
    }

    private void setupNavigation() {

        btnViewPantry.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    PantryListActivity.class
            );

            startActivity(intent);
        });

        btnAddIngredient.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    AddEditIngredientActivity.class
            );

            startActivity(intent);
        });

        btnSuggestedRecipes.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SuggestedRecipesActivity.class
            );

            startActivity(intent);
        });

        btnSettings.setOnClickListener(view -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    SettingsActivity.class
            );

            startActivity(intent);
        });
    }

    private void initialiseDatabase() {

        try {
            databaseHelper = new PantryDatabaseHelper(
                    getApplicationContext());

            // Create database tables if they do not exist.
            databaseHelper.getWritableDatabase();

            // Seed starter recipes when necessary.
            recipeSeeder = new RecipeSeeder(
                    getApplicationContext());

            recipeSeeder.seedRecipes();

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Unable to initialise the pantry database.",
                    Toast.LENGTH_LONG
            ).show();

            android.util.Log.e(
                    "SmartPantry",
                    "Database initialisation failed",
                    e
            );
        }
    }

    @Override
    protected void onDestroy() {
        if (recipeSeeder != null) {
            recipeSeeder.close();
        }

        if (databaseHelper != null) {
            databaseHelper.close();
        }

        super.onDestroy();
    }
}