package com.smartpantry.manager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartpantry.manager.R;
import com.smartpantry.manager.models.Recipe;

import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipeAdapter
        extends RecyclerView.Adapter<SuggestedRecipeAdapter.RecipeViewHolder> {

    public interface OnRecipeClickListener {
        void onViewRecipe(Recipe recipe);
    }

    private final List<Recipe> recipes = new ArrayList<>();
    private final OnRecipeClickListener listener;

    public SuggestedRecipeAdapter(
            List<Recipe> initialRecipes,
            OnRecipeClickListener listener) {

        if (initialRecipes != null) {
            recipes.addAll(initialRecipes);
        }

        this.listener = listener;
    }

    public void updateRecipes(List<Recipe> newRecipes) {
        recipes.clear();

        if (newRecipes != null) {
            recipes.addAll(newRecipes);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_suggested_recipe,
                parent,
                false
        );

        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull RecipeViewHolder holder,
            int position) {

        Recipe recipe = recipes.get(position);

        holder.tvRecipeName.setText(recipe.getName());

        int prepTime = recipe.getPrepTimeMinutes();

        holder.tvRecipePrepTime.setText(
                prepTime > 0
                        ? "Preparation time: " + prepTime + " minutes"
                        : "Preparation time: Not specified"
        );

        holder.btnViewRecipe.setOnClickListener(view -> {
            if (listener != null) {
                listener.onViewRecipe(recipe);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {

        TextView tvRecipeName;
        TextView tvRecipePrepTime;
        Button btnViewRecipe;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);

            tvRecipeName = itemView.findViewById(
                    R.id.tvRecipeName);

            tvRecipePrepTime = itemView.findViewById(
                    R.id.tvRecipePrepTime);

            btnViewRecipe = itemView.findViewById(
                    R.id.btnViewRecipe);
        }
    }
}
