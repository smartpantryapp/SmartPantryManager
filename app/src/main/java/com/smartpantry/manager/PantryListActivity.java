package com.smartpantry.manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smartpantry.manager.adapters.PantryIngredientAdapter;
import com.smartpantry.manager.database.PantryRepository;
import com.smartpantry.manager.models.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class PantryListActivity extends AppCompatActivity {

    private PantryRepository pantryRepository;
    private PantryIngredientAdapter adapter;

    private RecyclerView rvPantryIngredients;
    private TextView tvIngredientCount;
    private TextView tvEmptyPantry;
    private Button btnAddPantryIngredient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pantry_list);

        View root = findViewById(R.id.pantryListRoot);

        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(
                    root, (view, insets) -> {

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
        }

        pantryRepository = new PantryRepository(
                getApplicationContext());

        initialiseViews();
        initialiseRecyclerView();
        setupListeners();
    }

    private void initialiseViews() {
        rvPantryIngredients = findViewById(
                R.id.rvPantryIngredients);

        tvIngredientCount = findViewById(
                R.id.tvIngredientCount);

        tvEmptyPantry = findViewById(
                R.id.tvEmptyPantry);

        btnAddPantryIngredient = findViewById(
                R.id.btnAddPantryIngredient);
    }

    private void initialiseRecyclerView() {
        rvPantryIngredients.setLayoutManager(
                new LinearLayoutManager(this));

        adapter = new PantryIngredientAdapter(
                new ArrayList<>(),
                new PantryIngredientAdapter.OnIngredientActionListener() {

                    @Override
                    public void onEdit(Ingredient ingredient) {
                        openEditIngredient(ingredient);
                    }

                    @Override
                    public void onDelete(Ingredient ingredient) {
                        confirmDelete(ingredient);
                    }
                });

        rvPantryIngredients.setAdapter(adapter);
    }

    private void setupListeners() {
        btnAddPantryIngredient.setOnClickListener(view -> {
            Intent intent = new Intent(
                    PantryListActivity.this,
                    AddEditIngredientActivity.class);

            startActivity(intent);
        });
    }

    private void openEditIngredient(Ingredient ingredient) {
        Intent intent = new Intent(
                PantryListActivity.this,
                AddEditIngredientActivity.class);

        intent.putExtra(
                "ingredient_id",
                ingredient.getId());

        intent.putExtra(
                "ingredient_name",
                ingredient.getName());

        intent.putExtra(
                "ingredient_quantity",
                ingredient.getQuantity());

        intent.putExtra(
                "ingredient_unit",
                ingredient.getUnit());

        intent.putExtra(
                "ingredient_expiry",
                ingredient.getExpiryDate());

        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (pantryRepository != null && adapter != null) {
            loadIngredients();
        }
    }

    private void loadIngredients() {
        try {
            List<Ingredient> ingredients =
                    pantryRepository.getAllIngredients();

            adapter.updateIngredients(ingredients);

            boolean isEmpty = ingredients.isEmpty();

            tvEmptyPantry.setVisibility(
                    isEmpty ? View.VISIBLE : View.GONE);

            rvPantryIngredients.setVisibility(
                    isEmpty ? View.GONE : View.VISIBLE);

            tvIngredientCount.setText(
                    "Your ingredients (" + ingredients.size() + ")");

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Unable to load pantry ingredients.",
                    Toast.LENGTH_LONG
            ).show();

            android.util.Log.e(
                    "SmartPantry",
                    "Unable to load ingredients",
                    e
            );
        }
    }

    private void confirmDelete(Ingredient ingredient) {
        new AlertDialog.Builder(this)
                .setTitle("Delete ingredient")
                .setMessage(
                        "Are you sure you want to delete "
                                + ingredient.getName() + "?")
                .setNegativeButton(
                        "Cancel",
                        (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Delete", (dialog, which) -> {

                    try {
                        int rowsDeleted =
                                pantryRepository.deleteIngredient(
                                        ingredient.getId());

                        if (rowsDeleted > 0) {
                            Toast.makeText(
                                    this,
                                    "Ingredient deleted.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        } else {
                            Toast.makeText(
                                    this,
                                    "Ingredient could not be found.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                        loadIngredients();

                    } catch (Exception e) {
                        Toast.makeText(
                                this,
                                "Unable to delete ingredient.",
                                Toast.LENGTH_LONG
                        ).show();

                        android.util.Log.e(
                                "SmartPantry",
                                "Unable to delete ingredient",
                                e
                        );
                    }
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        if (pantryRepository != null) {
            pantryRepository.close();
        }

        super.onDestroy();
    }
}