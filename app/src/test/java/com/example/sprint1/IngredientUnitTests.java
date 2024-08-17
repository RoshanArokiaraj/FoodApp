package com.example.sprint1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.renderscript.ScriptGroup;

import com.example.sprint1.ui.ingredient.IngredientFragment;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class IngredientUnitTests {

    // aarav cases
    @Test
    public void correctSameIngredient() {
        assertTrue(IngredientFragment.sameIngredient("carrot", 15, "carrot", 15));
    }

    @Test
    public void correctDiffIngredient() {
        // different calories so assume different ingredient
        assertFalse(IngredientFragment.sameIngredient("carrot", 15, "carrot", 20));
    }

    // Pau cases
    @Test
    public void ingredientConstructorAssignsValuesCorrectly() {
        assertTrue(IngredientFragment.sameIngredient("pineapple", 29, "pineapple", 29));
    }

    @Test
    public void ingredientSettersAndGettersWorkCorrectly() {
        assertFalse(IngredientFragment.sameIngredient("salt", 3, "salt", 90));
    }

}