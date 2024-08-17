package com.example.sprint1.model.recipes;

import com.example.sprint1.model.recipes.RecipeComponent;

import java.util.ArrayList;
import java.util.List;

public class RecipeGroup implements RecipeComponent {
    private List<RecipeComponent> components = new ArrayList<>();
    private String name;

    public RecipeGroup(String name) {
        this.name = name;
    }

    @Override
    public void add(RecipeComponent component) {
        components.add(component);
    }

    @Override
    public void remove(RecipeComponent component) {
        components.remove(component);
    }

    @Override
    public List<RecipeComponent> getChildren() {
        return new ArrayList<>(components);
    }

    @Override
    public String getName() {
        return name;
    }
}


