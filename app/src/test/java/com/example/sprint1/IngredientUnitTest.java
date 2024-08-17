package com.example.sprint1;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.sprint1.model.Ingredient;


public class IngredientUnitTest {
    @Test
    public void ingredientConstructorAssignsValuesCorrectly() {
        Ingredient pinepple = new Ingredient("Pineapple", 2, 65);

        assertEquals("Pineapple", pinepple.getName());
        assertEquals(2, pinepple.getQuantity());
        assertEquals(65, pinepple.getCalories());
    }

    @Test
    public void ingredientSettersAndGettersWorkCorrectly() {
        Ingredient gred1 = new Ingredient("Pineapple", 2, 65);
        gred1.setName("Salt");
        gred1.setQuantity(3);
        gred1.setCalories(10);
        assertEquals("Salt", gred1.getName());
        assertEquals(3, gred1.getQuantity());
        assertEquals(10, gred1.getCalories());
    }

    @Test
    public void ingredientConstructorHandlesDifferentValues() {
        Ingredient tomato = new Ingredient("Tomato", 10, 20);

        assertEquals("Tomato", tomato.getName());
        assertEquals(10, tomato.getQuantity());
        assertEquals(20, tomato.getCalories());
    }

    @Test
    public void ingredientSettersAndGettersAreConsistent() {
        Ingredient ingredient = new Ingredient("Water", 1, 0);
        ingredient.setName("Milk");
        ingredient.setQuantity(5);
        ingredient.setCalories(42);

        assertEquals("Milk", ingredient.getName());
        assertEquals(5, ingredient.getQuantity());
        assertEquals(42, ingredient.getCalories());
    }

}