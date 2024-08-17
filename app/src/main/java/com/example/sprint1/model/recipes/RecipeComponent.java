package com.example.sprint1.model.recipes;

import java.util.List;

public interface RecipeComponent {
    void add(RecipeComponent recipeComponent);
    void remove(RecipeComponent recipeComponent);
    List<RecipeComponent> getChildren();
    String getName();
}