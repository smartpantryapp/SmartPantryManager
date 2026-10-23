package com.smartpantry.manager.utils;

import com.smartpantry.manager.database.PantryRepository;
import com.smartpantry.manager.database.RecipeRepository;
import com.smartpantry.manager.database.RecipeRepository.RecipeIngredient;
import com.smartpantry.manager.models.Ingredient;
import com.smartpantry.manager.models.Recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecipeMatcher {

    private final PantryRepository pantryRepository;
    private final RecipeRepository recipeRepository;

    public RecipeMatcher(
            PantryRepository pantryRepository,
            RecipeRepository recipeRepository) {

        this.pantryRepository = pantryRepository;
        this.recipeRepository = recipeRepository;
    }

    /**
     * Returns only recipes for which every required ingredient
     * is available in a sufficient quantity and compatible unit.
     */
    public List<Recipe> getMatchingRecipes() {

        List<Recipe> matches = new ArrayList<>();

        List<Ingredient> pantry =
                pantryRepository.getAllIngredients();

        List<Recipe> recipes =
                recipeRepository.getAllRecipes();

        // Store available quantities separately for each ingredient
        // and compatible measurement unit.
        Map<String, Double> availableQuantities = new HashMap<>();

        for (Ingredient ingredient : pantry) {

            if (ingredient.getName() == null
                    || ingredient.getUnit() == null
                    || !Double.isFinite(ingredient.getQuantity())
                    || ingredient.getQuantity() <= 0) {
                continue;
            }

            String nameKey = normalizeName(ingredient.getName());

            if (nameKey.isEmpty()) {
                continue;
            }

            String unit = normalizeUnit(ingredient.getUnit());

            double quantity = toBaseQuantity(
                    ingredient.getQuantity(), unit);

            String baseUnit = baseUnit(unit);
            String quantityKey = makeQuantityKey(nameKey, baseUnit);

            availableQuantities.put(
                    quantityKey,
                    availableQuantities.getOrDefault(quantityKey, 0.0)
                            + quantity
            );
        }

        for (Recipe recipe : recipes) {

            List<RecipeIngredient> requiredIngredients =
                    recipeRepository.getIngredientsForRecipe(
                            recipe.getId());

            // Recipes without ingredients must never be suggested.
            if (requiredIngredients == null
                    || requiredIngredients.isEmpty()) {
                continue;
            }

            // Aggregate duplicate requirements before comparing
            // them against the pantry.
            Map<String, Double> requiredQuantities = new HashMap<>();
            boolean validRecipe = true;

            for (RecipeIngredient required : requiredIngredients) {

                if (required.getName() == null
                        || required.getUnit() == null
                        || !Double.isFinite(
                        required.getRequiredQuantity())
                        || required.getRequiredQuantity() <= 0) {
                    validRecipe = false;
                    break;
                }

                String nameKey = normalizeName(required.getName());

                if (nameKey.isEmpty()) {
                    validRecipe = false;
                    break;
                }

                String unit = normalizeUnit(required.getUnit());

                double quantity = toBaseQuantity(
                        required.getRequiredQuantity(), unit);

                String baseUnit = baseUnit(unit);
                String quantityKey = makeQuantityKey(
                        nameKey, baseUnit);

                requiredQuantities.put(
                        quantityKey,
                        requiredQuantities.getOrDefault(
                                quantityKey, 0.0) + quantity
                );
            }

            if (!validRecipe) {
                continue;
            }

            boolean allAvailable = true;

            for (Map.Entry<String, Double> requirement
                    : requiredQuantities.entrySet()) {

                double available = availableQuantities.getOrDefault(
                        requirement.getKey(), 0.0);

                double needed = requirement.getValue();

                if (available < needed) {
                    allAvailable = false;
                    break;
                }
            }

            if (allAvailable) {
                matches.add(recipe);
            }
        }

        return matches;
    }

    /**
     * Normalises common ingredient names and plural forms.
     */
    private String normalizeName(String name) {

        String normalized = name.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");

        if (normalized.endsWith("ies")
                && normalized.length() > 4) {

            normalized = normalized.substring(
                    0, normalized.length() - 3) + "y";

        } else if ((normalized.endsWith("oes")
                || normalized.endsWith("ses")
                || normalized.endsWith("xes")
                || normalized.endsWith("ches")
                || normalized.endsWith("shes"))
                && normalized.length() > 4) {

            normalized = normalized.substring(
                    0, normalized.length() - 2);

        } else if (normalized.endsWith("s")
                && !normalized.endsWith("ss")
                && normalized.length() > 3) {

            normalized = normalized.substring(
                    0, normalized.length() - 1);
        }

        return normalized;
    }

    /**
     * Standardises supported measurement names.
     * Other units remain distinct and only match the same unit.
     */
    private String normalizeUnit(String unit) {

        String normalized = unit.trim()
                .toLowerCase(Locale.ROOT);

        switch (normalized) {
            case "kilogram":
            case "kilograms":
            case "kg":
                return "kg";

            case "gram":
            case "grams":
            case "g":
                return "g";

            case "litre":
            case "litres":
            case "liter":
            case "liters":
            case "l":
                return "l";

            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
            case "ml":
                return "ml";

            case "piece":
            case "pieces":
            case "pcs":
            case "pc":
                return "piece";

            case "clove":
            case "cloves":
                return "clove";

            case "cup":
            case "cups":
                return "cup";

            case "tablespoon":
            case "tablespoons":
            case "tbsp":
                return "tbsp";

            case "teaspoon":
            case "teaspoons":
            case "tsp":
                return "tsp";

            default:
                return normalized;
        }
    }

    /**
     * Converts supported measurements to a common base quantity.
     */
    private double toBaseQuantity(double quantity, String unit) {

        switch (unit) {
            case "kg":
                return quantity * 1000.0;

            case "g":
                return quantity;

            case "l":
                return quantity * 1000.0;

            case "ml":
                return quantity;

            default:
                // No unsupported conversions are performed.
                return quantity;
        }
    }

    /**
     * Returns the common unit used for quantity comparisons.
     */
    private String baseUnit(String unit) {

        switch (unit) {
            case "kg":
            case "g":
                return "g";

            case "l":
            case "ml":
                return "ml";

            default:
                return unit;
        }
    }

    private String makeQuantityKey(String ingredientName, String unit) {
        return ingredientName + "|" + unit;
    }
}