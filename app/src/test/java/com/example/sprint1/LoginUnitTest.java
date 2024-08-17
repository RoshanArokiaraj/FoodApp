package com.example.sprint1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.sprint1.model.LoginActivity;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 * change
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class LoginUnitTest {
    @Test
    public void emailIsEmpty_returnsFalse() {
        assertFalse(LoginActivity.validateEmail(""));
    }

    @Test
    public void emailIsInvalid_returnsFalse() {
        assertFalse(LoginActivity.validateEmail("     "));
    }

    @Test
    public void passwordIsEmpty_returnsFalse() {
        assertFalse(LoginActivity.validatePassword(""));
    }

    @Test
    public void emailIsValid_returnsTrue() {
        assertTrue(LoginActivity.validateEmail("ken@gmail.com"));
    }

    @Test
    public void passwordIsValid_returnsTrue() {
        assertTrue(LoginActivity.validatePassword("kenken"));
    }
}