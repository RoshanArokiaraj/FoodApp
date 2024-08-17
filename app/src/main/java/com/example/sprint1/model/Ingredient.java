package com.example.sprint1.model;

import java.util.Date;

public class Ingredient {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public Ingredient() {
        this.name = "";
        this.quantity = 0;
        this.calories = 0;
    }

    public Ingredient(String name, int quantity, int calories) {
        this.name = name;
        this.quantity = quantity;
        this.calories = calories;
    }

    // Private Instance Variables
    private String name;
    private int quantity;

    public Ingredient(String name, int quantity, int calories, Date expiryDate) {
        this.name = name;
        this.quantity = quantity;
        this.calories = calories;
        this.expiryDate = expiryDate;
    }

    private int calories;

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    private Date expiryDate;
}
