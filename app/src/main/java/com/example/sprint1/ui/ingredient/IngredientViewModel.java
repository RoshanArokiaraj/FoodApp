package com.example.sprint1.ui.ingredient;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class IngredientViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public IngredientViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Ingredient Page Placeholder");
    }

    public LiveData<String> getText() {
        return mText;
    }
}