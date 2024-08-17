package com.example.sprint1;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.sprint1.ui.PersonalInformation.PersonalInformation;
import com.example.sprint1.ui.recipe.RecipeFragment;
import com.example.sprint1.ui.ingredient.IngredientFragment;
import com.example.sprint1.ui.home.HomeFragment;


public class FragmentTest {

    private PersonalInformation pi_fragment;
    private RecipeFragment recipe_fragment;
    private IngredientFragment ingredient_fragment;
    private HomeFragment home_fragment;

    @Before
    public void setUp() {
        pi_fragment = new PersonalInformation();
        recipe_fragment = new RecipeFragment();
        ingredient_fragment = new IngredientFragment();
        home_fragment = new HomeFragment();
    }

    @Test
    public void testPIFragmentNotNull() {
        assertNotNull(pi_fragment);
    }

    @Test
    public void testRecipeFragmentNotNull() {
        assertNotNull(recipe_fragment);
    }

    @Test
    public void testIngredientFragmentNotNull() {
        assertNotNull(ingredient_fragment);
    }

    @Test
    public void testHomeFragmentNotNull() {
        assertNotNull(home_fragment);
    }
}