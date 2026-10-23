package com.smartpantry.manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smartpantry.manager.adapters.SuggestedRecipeAdapter;
import com.smartpantry.manager.database.PantryRepository;
import com.smartpantry.manager.database.RecipeRepository;
import com.smartpantry.manager.models.Recipe;
import com.smartpantry.manager.utils.RecipeMatcher;

import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private PantryRepository pantryRepository;
    private RecipeRepository recipeRepository;
    private RecipeMatcher recipeMatcher;

    private SuggestedRecipeAdapter adapter;

    private RecyclerView rvSuggestedRecipes;
    private TextView tvRecipeMatchInfo;
    private TextView tvNoMatchingRecipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        initialiseRepositories();
        initialiseViews();
        initialiseRecyclerView();
    }

    private void initialiseRepositories() {
        pantryRepository = new PantryRepository(
                getApplicationContext());

        recipeRepository = new RecipeRepository(
                getApplicationContext());

        recipeMatcher = new RecipeMatcher(
                pantryRepository,
                recipeRepository);
    }

    private void initialiseViews() {
        rvSuggestedRecipes = findViewById(
                R.id.rvSuggestedRecipes);

        tvRecipeMatchInfo = findViewById(
                R.id.tvRecipeMatchInfo);

        tvNoMatchingRecipes = findViewById(
                R.id.tvNoMatchingRecipes);
    }

    private void initialiseRecyclerView() {
        rvSuggestedRecipes.setLayoutManager(
                new LinearLayoutManager(this));

        adapter = new SuggestedRecipeAdapter(
                new ArrayList<>(),
                recipe -> openRecipeDetails(recipe));

        rvSuggestedRecipes.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (recipeMatcher != null && adapter != null) {
            loadMatchingRecipes();
        }
    }

    private void loadMatchingRecipes() {
        try {
            List<Recipe> matchingRecipes =
                    recipeMatcher.getMatchingRecipes();

            adapter.updateRecipes(matchingRecipes);

            int matchCount = matchingRecipes.size();

            tvRecipeMatchInfo.setText(
                    matchCount == 1
                            ? "1 recipe matches your pantry"
                            : matchCount + " recipes match your pantry");

            boolean noMatches = matchingRecipes.isEmpty();

            tvNoMatchingRecipes.setText(
                    "No recipes match all your available ingredients "
                            + "and quantities. Add ingredients to your "
                            + "pantry to discover more recipes.");

            tvNoMatchingRecipes.setVisibility(
                    noMatches ? View.VISIBLE : View.GONE);

            rvSuggestedRecipes.setVisibility(
                    noMatches ? View.GONE : View.VISIBLE);

        } catch (Exception e) {
            tvRecipeMatchInfo.setText(
                    "Unable to check recipe matches.");

            tvNoMatchingRecipes.setText(
                    "Something went wrong while checking your "
                            + "ingredients. Please try again.");

            tvNoMatchingRecipes.setVisibility(View.VISIBLE);
            rvSuggestedRecipes.setVisibility(View.GONE);

            Toast.makeText(
                    this,
                    "Unable to load suggested recipes.",
                    Toast.LENGTH_LONG
            ).show();

            android.util.Log.e(
                    "SmartPantry",
                    "Failed to load matching recipes",
                    e);
        }
    }

    private void openRecipeDetails(Recipe recipe) {
        Intent intent = new Intent(
                SuggestedRecipesActivity.this,
                RecipeDetailActivity.class);

        intent.putExtra("recipe_id", recipe.getId());
        intent.putExtra("recipe_name", recipe.getName());
        intent.putExtra(
                "recipe_instructions",
                recipe.getInstructions());
        intent.putExtra(
                "recipe_prep_time",
                recipe.getPrepTimeMinutes());

        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        if (pantryRepository != null) {
            pantryRepository.close();
        }

        if (recipeRepository != null) {
            recipeRepository.close();
        }

        super.onDestroy();
    }
}