package com.example.sprint1;

import static org.junit.Assert.assertEquals;

import com.example.sprint1.model.Ingredient;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExpiryDateUnitTest {
    @Test
    public void testExpiryDateisValid() throws ParseException {
        Ingredient ingred1 = new Ingredient("Salt", 2, 2);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date expDate = sdf.parse("2024-12-01");
//
        ingred1.setExpiryDate(expDate);

        assertEquals("Salt", ingred1.getName());
        assertEquals(ingred1.getExpiryDate().getClass(), Date.class);

    }
}
