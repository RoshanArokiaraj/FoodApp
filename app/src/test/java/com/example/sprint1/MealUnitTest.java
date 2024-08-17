package com.example.sprint1;

import static org.junit.Assert.assertEquals;

import com.example.sprint1.model.Meal;

import org.junit.Test;

public class MealUnitTest {
    @Test
    public void mealConstructorAssignsValuesCorrectly() {
        Meal meal = new Meal( 150, "2024-03-13", "Salad");

        assertEquals("Salad", meal.getName());
        assertEquals(150, meal.getCalories());
        assertEquals("2024-03-13", meal.getDate());
    }

    @Test
    public void mealSettersAndGettersWorkCorrectly() {
        Meal meal = new Meal();
        meal.setName("Pizza");
        meal.setCalories(285);
        meal.setDate("2024-03-14");

        assertEquals("Pizza", meal.getName());
        assertEquals(285, meal.getCalories());
        assertEquals("2024-03-14", meal.getDate());
    }
}
