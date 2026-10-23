package com.smartpantry.manager.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smartpantry.manager.R;
import com.smartpantry.manager.models.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PantryIngredientAdapter
        extends RecyclerView.Adapter<PantryIngredientAdapter.IngredientViewHolder> {

    public interface OnIngredientActionListener {
        void onEdit(Ingredient ingredient);
        void onDelete(Ingredient ingredient);
    }

    private final List<Ingredient> ingredients = new ArrayList<>();
    private final OnIngredientActionListener listener;

    public PantryIngredientAdapter(
            List<Ingredient> initialIngredients,
            OnIngredientActionListener listener) {

        if (initialIngredients != null) {
            ingredients.addAll(initialIngredients);
        }

        this.listener = listener;
    }

    public void updateIngredients(List<Ingredient> newIngredients) {
        ingredients.clear();

        if (newIngredients != null) {
            ingredients.addAll(newIngredients);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_pantry_ingredient,
                parent,
                false
        );

        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull IngredientViewHolder holder, int position) {

        Ingredient ingredient = ingredients.get(position);

        holder.tvIngredientName.setText(ingredient.getName());

        holder.tvIngredientQuantity.setText(
                String.format(
                        Locale.getDefault(),
                        "Quantity: %s %s",
                        formatQuantity(ingredient.getQuantity()),
                        ingredient.getUnit()
                )
        );

        String expiryDate = ingredient.getExpiryDate();

        if (expiryDate == null || expiryDate.trim().isEmpty()) {
            holder.tvIngredientExpiry.setText(
                    "Expiry date: Not provided"
            );
        } else {
            holder.tvIngredientExpiry.setText(
                    "Expiry date: " + expiryDate
            );
        }

        holder.btnEditIngredient.setOnClickListener(view -> {
            if (listener != null) {
                listener.onEdit(ingredient);
            }
        });

        holder.btnDeleteIngredient.setOnClickListener(view -> {
            if (listener != null) {
                listener.onDelete(ingredient);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    private String formatQuantity(double quantity) {
        if (quantity == Math.floor(quantity)) {
            return String.format(
                    Locale.getDefault(),
                    "%.0f",
                    quantity
            );
        }

        return String.format(
                Locale.getDefault(),
                "%.2f",
                quantity
        );
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {

        TextView tvIngredientName;
        TextView tvIngredientQuantity;
        TextView tvIngredientExpiry;
        Button btnEditIngredient;
        Button btnDeleteIngredient;

        IngredientViewHolder(@NonNull View itemView) {
            super(itemView);

            tvIngredientName = itemView.findViewById(
                    R.id.tvIngredientName);

            tvIngredientQuantity = itemView.findViewById(
                    R.id.tvIngredientQuantity);

            tvIngredientExpiry = itemView.findViewById(
                    R.id.tvIngredientExpiry);

            btnEditIngredient = itemView.findViewById(
                    R.id.btnEditIngredient);

            btnDeleteIngredient = itemView.findViewById(
                    R.id.btnDeleteIngredient);
        }
    }
}