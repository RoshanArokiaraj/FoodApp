package com.example.sprint1;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.example.sprint1.model.Meal;
import com.github.mikephil.charting.data.PieEntry;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class PieChartUnitTest {
    @Test
    public void convertsMealListToPieEntries() {
        List<PieEntry> calories = new ArrayList<>();
        calories.add(new PieEntry(40, "orange"));
        calories.add(new PieEntry(10, "apple"));
        calories.add(new PieEntry(90, "sandwich"));
        calories.add(new PieEntry(120, "bagel"));


        assertNotNull(calories);
        assertEquals(4, calories.size());
        assertEquals("orange", calories.get(0).getLabel());
        assertEquals(40f, calories.get(0).getValue(), 0.0f);
        assertEquals("apple", calories.get(1).getLabel());
        assertEquals(10f, calories.get(1).getValue(), 0.0f);
    }
}
