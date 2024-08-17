package com.example.sprint1.model.pantry_items;

import java.util.ArrayList;
import java.util.List;

public class PantryItem implements Subject {
    private int calories;
    private String name;
    private int quantity;
    private List<Observer> observers = new ArrayList<>();

    public PantryItem() {
        // Default constructor required for calls to DataSnapshot.getValue(PantryItem.class)
    }

    @Override
    public void addObserver(Observer obs) {
        if (!observers.contains(obs)) {
            observers.add(obs);
        }
    }

    @Override
    public void removeObserver(Observer obs) {
        observers.remove(obs);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }
    
    public int getCalories() {
        return calories;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }
}
