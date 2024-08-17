package com.example.sprint1.model.recipes;

import com.example.sprint1.model.Ingredient;

import java.util.List;

public class Recipe {
    private String name;
    private List<Ingredient> ingredients;

    // Default constructor is required for Firebase data mapping
    public Recipe() {
    }

    public Recipe(String name, List<Ingredient> ingredients) {
        this.name = name;
        this.ingredients = ingredients;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    // Helper method to get a string description of ingredients
    public String getIngredientsDescription() {
        StringBuilder description = new StringBuilder();
        for (Ingredient ingredient : ingredients) {
            if (description.length() > 0) {
                description.append(", ");
            }
            description.append(ingredient.getName())
                    .append(" (").append(ingredient.getQuantity()).append(")");
        }
        return description.toString();
    }
}

