package com.example.sprint1.ui.shoppinglist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ShoppinglistViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ShoppinglistViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Shopping List Page Placeholder");
    }

    public LiveData<String> getText() {
        return mText;
    }
}