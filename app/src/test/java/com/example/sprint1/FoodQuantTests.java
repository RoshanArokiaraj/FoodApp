package com.example.sprint1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.renderscript.ScriptGroup;

//import com.example.sprint1.ui.inputmeal.InputMealViewModel;
import com.example.sprint1.ui.recipe.RecipeAdapter;

import org.junit.Test;

import java.util.ArrayList;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class FoodQuantTests {

    // aarav cases

    @Test
    public void correctFoodExtraction() {
        ArrayList<String> expected = new ArrayList<>();
        expected.add("Apple");
        expected.add("Banana");
        String testString = "Apple (1), Banana (2)";
        ArrayList<String> received = RecipeAdapter.extractFoods(testString);
        boolean valid = true;
        assertTrue(received.size() == expected.size());
        for (int i = 0; i < expected.size(); i++) {
            valid = valid && (received.get(i).equals(expected.get(i)));
        }
        assertTrue(valid);
    }


    @Test
    public void correctQuantityExtraction() {
        ArrayList<Integer> expected = new ArrayList<>();
        expected.add(1);
        expected.add(2);
        String testString = "Apple (1), Banana (2)";
        ArrayList<Integer> received = RecipeAdapter.extractNums(testString);
        boolean valid = true;
        assertTrue(received.size() == expected.size());
        for (int i = 0; i < expected.size(); i++) {
            valid = valid && (received.get(i).equals(expected.get(i)));
        }
        assertTrue(valid);
    }

    @Test
    public void aaravCorrectQuantityExtraction2() {
        ArrayList<Integer> expected = new ArrayList<>();
        expected.add(1);
        expected.add(2);
        expected.add(3);
        String testString = "Apple (1), Banana (2), Pineapple (3)";
        ArrayList<Integer> received = RecipeAdapter.extractNums(testString);
        boolean valid = true;
        assertTrue(received.size() == expected.size());
        for (int i = 0; i < expected.size(); i++) {
            valid = valid && (received.get(i).equals(expected.get(i)));
        }
        assertTrue(valid);
    }
  
    //roshan cases
    @Test
    public void correctFoodExtraction2() {
        ArrayList<String> expected = new ArrayList<>();
        expected.add("Mac");
        expected.add("Cheese");
        String testString = "Mac (1), Cheese (2)";
        ArrayList<String> received = RecipeAdapter.extractFoods(testString);
        boolean valid = true;
        assertTrue(received.size() == expected.size());
        for (int i = 0; i < expected.size(); i++) {
            valid = valid && (received.get(i).equals(expected.get(i)));
        }
        assertTrue(valid);
    }


    @Test
    public void correctQuantityExtraction2() {
        ArrayList<Integer> expected = new ArrayList<>();
        expected.add(4);
        expected.add(5);
        String testString = "Ben (4), 10 (5)";
        ArrayList<Integer> received = RecipeAdapter.extractNums(testString);
        boolean valid = true;
        assertTrue(received.size() == expected.size());
        for (int i = 0; i < expected.size(); i++) {
            valid = valid && (received.get(i).equals(expected.get(i)));
        }
        assertTrue(valid);
    }

    // PAU TESTING CASES
    @Test
    public void correctFoodExtraction3() {
        ArrayList<String> expected = new ArrayList<>();
        expected.add("Ben");
        expected.add("10");
        String testString = "Ben (4), 10 (5)";
        ArrayList<String> received = RecipeAdapter.extractFoods(testString);
        boolean valid = true;
        assertTrue(received.size() == expected.size());
        for (int i = 0; i < expected.size(); i++) {
            valid = valid && (received.get(i).equals(expected.get(i)));
        }
        assertTrue(valid);
    }

    @Test
    public void correctQuantityExtraction3() {
        ArrayList<Integer> expected = new ArrayList<>();
        expected.add(1);
        expected.add(2);
        String testString = "Ben (1), Cheese (2)";
        ArrayList<String> received = RecipeAdapter.extractFoods(testString);
        boolean valid = true;
        assertTrue(received.size() == expected.size());
        for (int i = 0; i < expected.size(); i++) {
            valid = valid && (received.get(i).equals(expected.get(i)));
        }
        assertTrue(!valid);
    }

    // Anir's Unit Tests
    @Test
    public void correctFoodExtraction4() {
        ArrayList<String> expected = new ArrayList<>();
        expected.add("Extra");
        expected.add("5");

        String testString = "Extra (5), 5 (9)";
        ArrayList<String> received = RecipeAdapter.extractFoods(testString);
        boolean valid = true;
        assertTrue(received.size() == expected.size());
        for (int i = 0; i < expected.size(); i++) {
            valid = valid && (received.get(i).equals(expected.get(i)));
        }
        assertTrue(valid);
    }

    @Test
    public void correctFoodExtraction5() {
        ArrayList<String> expected = new ArrayList<>();
        expected.add("Another");
        expected.add("7");
        String testString = "Another (8), 7 (7)";
        ArrayList<String> received = RecipeAdapter.extractFoods(testString);
        boolean valid = true;
        assertTrue(received.size() == expected.size());
        for (int i = 0; i < expected.size(); i++) {
            valid = valid && (received.get(i).equals(expected.get(i)));
        }
        assertTrue(valid);
    }
}