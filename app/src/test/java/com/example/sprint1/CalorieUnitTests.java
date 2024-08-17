package com.example.sprint1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.renderscript.ScriptGroup;

import com.example.sprint1.ui.inputmeal.InputMealViewModel;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CalorieUnitTests {

    // aarav cases
    @Test
    public void correctAverageMaleCalorieGoal() {
        assertTrue(InputMealViewModel.calculateCalorieGoal(1.75, 91, 0) == 1983);
    }

    @Test
    public void correctAverageFemaleCalorieGoal() {
        assertTrue(InputMealViewModel.calculateCalorieGoal(1.61, 78, 1) == 1552);
    }
    //testing various data instances for old man stats
    @Test
    public void correctOldMaleCalorieGoal() {
        assertTrue(InputMealViewModel.calculateCalorieGoal(1.7, 80, 0) == 1808);
    }
    //testing various data instances for old woman stats
    @Test
    public void correctOldFemaleCalorieGoal() {
        assertTrue(InputMealViewModel.calculateCalorieGoal(1.55, 75, 1) == 1513);
    }

    @Test
    public void correctYoungMaleCalorieGoal() {
        assertTrue(InputMealViewModel.calculateCalorieGoal(1.9, 70, 0) == 1771);
    }

    @Test
    public void correctYoungFemaleCalorieGoal() {
        assertTrue(InputMealViewModel.calculateCalorieGoal(1.65, 70, 1) == 1483);
    }
}