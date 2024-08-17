package com.example.sprint1;

import static org.junit.Assert.assertEquals;

import android.widget.EditText;

import com.example.sprint1.ui.recipe.RecipeFragment;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RecipeUnitTests {
    // Helper method to get error message of private EditText field
    private String getErrorMessage(RecipeFragment recipeFragment, String fieldName) {
        try {
            Field field = RecipeFragment.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            EditText editText = (EditText) field.get(recipeFragment);
            return editText.getError().toString();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    //Roshan
    @Test
    public void testCheckWithEmptyFields() {
        RecipeFragment recipeFragment = new RecipeFragment();
        List<String> ingredientNames = Arrays.asList("", "Sugar");
        List<String> quantities = Arrays.asList("200", "300");

        String result = recipeFragment.validateInputs("Pancake", ingredientNames, quantities);
        assertEquals("Cannot have empty string in any of the fields", result);
    }

    @Test
    public void testCheckWithMismatchedSizes() {
        RecipeFragment recipeFragment = new RecipeFragment();
        List<String> ingredientNames = Arrays.asList("Flour", "Sugar");
        List<String> quantities = Collections.singletonList("200");

        String result = recipeFragment.validateInputs("Pancake", ingredientNames, quantities);
        assertEquals("Ingredient list and quantities must have same size", result);
    }

    //Avaye
    @Test
    public void testCheckWithAllCorrectInputs() {
        RecipeFragment recipeFragment = new RecipeFragment();
        List<String> ingredientNames = Arrays.asList("Flour", "Sugar");
        List<String> quantities = Arrays.asList("100", "200");

        String result = recipeFragment.validateInputs("Pancake", ingredientNames, quantities);
        assertEquals(null, result);
    }

    @Test
    public void testCheckWithEmptyRecipeName() {
        RecipeFragment recipeFragment = new RecipeFragment();
        List<String> ingredientNames = Arrays.asList("Flour", "Sugar");
        List<String> quantities = Arrays.asList("100", "200");

        String result = recipeFragment.validateInputs("", ingredientNames, quantities);
        assertEquals("Recipe name cannot be empty", result);
    }

    // Test for invalid numerical inputs in quantities
    @Test
    public void testCheckWithInvalidNumberFormat() {
        RecipeFragment recipeFragment = new RecipeFragment();
        List<String> ingredientNames = Arrays.asList("Flour", "Sugar");
        List<String> quantities = Arrays.asList("one hundred", "200");

        String result = recipeFragment.validateInputs("Cake", ingredientNames, quantities);
        assertEquals("Quantities should be numeric", result);
    }

    // Test for excessively long ingredient names
    @Test
    public void testCheckWithLongIngredientNames() {
        RecipeFragment recipeFragment = new RecipeFragment();
        List<String> ingredientNames = Arrays.asList("FlourFlourFlourFlourFlourFlourFlourFlourFlourFlourFlour", "Sugar");
        List<String> quantities = Arrays.asList("100", "200");

        String result = recipeFragment.validateInputs("Pie", ingredientNames, quantities);
        assertEquals("Ingredient names cannot be longer than 50 characters", result);
    }

    // Test for case sensitivity in recipe name
    @Test
    public void testCheckCaseSensitivityInRecipeName() {
        RecipeFragment recipeFragment = new RecipeFragment();
        List<String> ingredientNames = Arrays.asList("Flour", "Sugar");
        List<String> quantities = Arrays.asList("100", "200");

        String result = recipeFragment.validateInputs("pancake", ingredientNames, quantities);
        assertEquals(null, result); // Assuming the function should treat "pancake" the same as "Pancake"
    }

}
