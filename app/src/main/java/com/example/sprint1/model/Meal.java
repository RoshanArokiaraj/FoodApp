package com.example.sprint1.model;

public class Meal {


    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Meal(int calories, String date, String name) {
        this.calories = calories;
        this.date = date;
        this.name = name;
    }

    public Meal() {

    }

    private int calories;
    private String date;
    private String name;


}
