package com.example.sprint1;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import com.example.sprint1.ui.PersonalInformation.PersonalInformation;

import org.junit.Before;
import org.junit.Test;

public class PersonalInformationTest {
    private PersonalInformation pi;

    @Before
    public void setUp() {
        pi = new PersonalInformation();
    }

    @Test
    public void heightIsCorrectlyParsed() {
        // Assuming validateHeight returns 0 if parsing fails
        double height = pi.validateHeight("1.75");
        assertEquals(1.75, height, 0);
    }

    @Test
    public void heightParsingFails() {
        // Assuming validateHeight returns 0 if parsing fails
        double height = pi.validateHeight("abc");
        assertEquals(0, height, 0);
    }

    @Test
    public void weightIsCorrectlyParsed() {
        // Assuming validateWeight returns 0 if parsing fails
        double weight = pi.validateWeight("15");
        assertEquals(15, weight, 0);
    }

    @Test
    public void weightParsingFails() {
        // Assuming validateWeight returns 0 if parsing fails
        double weight = pi.validateWeight("letters");
        assertEquals(0, weight, 0);
    }

    @Test
    public void genderIsReturnedCorrectly() {
        // Assuming validateGender just returns the input string
        String gender = pi.validateGender("Male");
        assertEquals("Male", gender);
    }
}

