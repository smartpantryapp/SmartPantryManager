package com.smartpantry.manager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView tvRecipeDetailName;
    private TextView tvRecipeDetailPrepTime;
    private TextView tvRecipeDetailInstructions;
    private Button btnBackToRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        initialiseViews();
        displayRecipeDetails();
        setupListeners();
    }

    private void initialiseViews() {
        tvRecipeDetailName = findViewById(
                R.id.tvRecipeDetailName);

        tvRecipeDetailPrepTime = findViewById(
                R.id.tvRecipeDetailPrepTime);

        tvRecipeDetailInstructions = findViewById(
                R.id.tvRecipeDetailInstructions);

        btnBackToRecipes = findViewById(
                R.id.btnBackToRecipes);
    }

    private void displayRecipeDetails() {
        String recipeName = getIntent().getStringExtra(
                "recipe_name");

        String instructions = getIntent().getStringExtra(
                "recipe_instructions");

        int prepTime = getIntent().getIntExtra(
                "recipe_prep_time", 0);

        tvRecipeDetailName.setText(
                recipeName == null ? "Recipe Details" : recipeName);

        tvRecipeDetailPrepTime.setText(
                prepTime > 0
                        ? "Preparation time: " + prepTime + " minutes"
                        : "Preparation time: Not specified");

        tvRecipeDetailInstructions.setText(
                instructions == null || instructions.trim().isEmpty()
                        ? "Instructions are not available for this recipe."
                        : instructions);
    }

    private void setupListeners() {
        btnBackToRecipes.setOnClickListener(view -> finish());
    }
}