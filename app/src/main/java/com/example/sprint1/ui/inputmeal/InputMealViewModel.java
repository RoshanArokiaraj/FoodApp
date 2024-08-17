package com.example.sprint1.ui.inputmeal;
import androidx.lifecycle.ViewModel;


public class InputMealViewModel extends ViewModel {

    //    private final MutableLiveData<String> mText;

    public InputMealViewModel() {
        //        mText = new MutableLiveData<>();
        //        mText.setValue("Input Meal Page Placeholder");
        System.out.println("input viewmodel launched");
    }

    //    public LiveData<String> getText() {
    //        return "mText";
    //    }

    // no age field - using 30years to give user an idea

    public static int calculateCalorieGoal(double height, int weight, int gender) {
        // gender 1 -> male
        // gender 0 -> female
        if (gender == 1) {
            return (int) (655 + (9.6 * ((double) weight))
                    + (1.8 * ((double) height * 100)) - (4.7 * 30));
        } else {
            return (int) (66 + (13.7 * ((double) weight))
                    + (5.0 * ((double) height * 100)) - (6.8 * 30));
        }

    }

}

