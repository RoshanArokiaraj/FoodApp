package com.example.sprint1.ui.inputmeal;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;

import com.example.sprint1.R;
import com.example.sprint1.databinding.FragmentInputBinding;
import com.example.sprint1.model.Meal;
import com.example.sprint1.model.User;
//<<<<<<< Updated upstream
//import com.github.mikephil.charting.charts.PieChart;
//=======
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
//>>>>>>> Stashed changes
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class InputMealFragment extends Fragment {

    private FragmentInputBinding binding;


    private FirebaseAuth auth;

    private List<User> users = new ArrayList<>();
    private List<Meal> meals = new ArrayList<>();
    // public String mealUrl = "https://sprint1-87a00-default-rtdb.firebaseio.com/meal";

    private PieChart pieChart;
    //<<<<<<< Updated upstream
    //    private DatabaseReference userDatabaseReference;
    //=======
    private BarChart barChart;
    private DatabaseReference userDatabaseReference;
    private DatabaseReference userBarDatabaseReference;
    //>>>>>>> Stashed changes
    private EditText mealNameEditText;
    private EditText calorieCountEditText;
    private Button submitButton;
    private DatabaseReference db;

    private TextView heighttext;
    private TextView weighttext;
    private TextView gendertext;
    private TextView goaltext;
    private TextView consumedtext;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        InputMealViewModel inputMealViewModel =
                new ViewModelProvider(this).get(InputMealViewModel.class);

        binding = FragmentInputBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        // Initialize Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Auth instance init
        auth = FirebaseAuth.getInstance();

        // Set pieChart ID
        pieChart = binding.pieChart;
        //<<<<<<< Updated upstream
        //=======
        barChart = binding.barChart;
        //>>>>>>> Stashed changes




        // Use the full database path provided
        String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        System.out.println(uid);
        String uidUrl = "https://sprint1-87a00-default-rtdb.firebaseio.com/meal" + "/" + uid;
        userDatabaseReference = database.getReferenceFromUrl(uidUrl);
        //<<<<<<< Updated upstream
        //=======
        userBarDatabaseReference = database.getReferenceFromUrl(uidUrl);
        //>>>>>>> Stashed changes

        // Set up the button click listener
        Button btnFetchData = binding.btnFetchMealData; // has to match your button ID in the layout
        btnFetchData.setOnClickListener(view -> fetchDataFromFirebase());

        //<<<<<<< Updated upstream
        //=======
        Button btnBarFetchData = binding.btnFetchMealData2;
        btnBarFetchData.setOnClickListener(view -> fetchBarDataFromFirebase());



        //>>>>>>> Stashed changes
        //        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
        mealNameEditText = root.findViewById(R.id.editText_meal_name);
        calorieCountEditText = root.findViewById(R.id.editText_calorie_count);
        submitButton = root.findViewById(R.id.button_submit_meal);

        heighttext = root.findViewById(R.id.text_height);
        weighttext = root.findViewById(R.id.text_weight);
        gendertext = root.findViewById(R.id.text_gender);
        goaltext = root.findViewById(R.id.text_goal);
        consumedtext = root.findViewById(R.id.text_intake);

        //        String uid = auth.getCurrentUser().getUid();
        db.child("users").child(uid).get().addOnSuccessListener(dataSnapshot -> {

            if (dataSnapshot.exists()) {

                String gender = dataSnapshot.child("gender").getValue(String.class);
                double height = dataSnapshot.child("height").getValue(Double.class);
                double weight = dataSnapshot.child("weight").getValue(Double.class);

                int calories = InputMealViewModel.calculateCalorieGoal(height,
                        (int) weight, gender == "Male" ? 1 : 0);
                heighttext.setText("User Height: " + (Double.toString(height)) + " (meters)");
                weighttext.setText("User Weight: " + (Double.toString(weight)) +  " (kg)");
                gendertext.setText("User Gender: " + gender);
                goaltext.setText("Goal: " + Integer.toString(calories) + " (Cal)");

            } else {
                System.out.println("uid doesn't exist, unset data?");
            }

        });

        db.child("meal").child(uid).get().addOnSuccessListener(dataSnapshot -> {

            if (dataSnapshot.exists()) {

                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                int consumed = 0;
                for (DataSnapshot mealchild : children) {
                    System.out.println(consumed);
                    Iterable<DataSnapshot> thingy = mealchild.getChildren();


                    System.out.println("indexed into db");
                    System.out.println(mealchild.child("date").getValue());
                    System.out.println(mealchild.getValue());
                    if (mealchild.child("date").getValue() != null
                            && mealchild.child("date")
                            .getValue(String.class).equals(getCurrentDate())) {
                        consumed += mealchild.child("calories").getValue(Integer.class);
                    }
                }
                consumedtext.setText("Consumed: " + Integer.toString(consumed) + " (Cal)");

            } else {
                System.out.println("uid doesn't exist, unset data?");
            }

        });


        submitButton.setOnClickListener(view -> saveMealToDatabase());


        return root;
    }

    private void fetchDataFromFirebase() {
        // Since you've already specified the full path in the databaseReference initialization,
        // you don't need to specify it again here. Just use the reference directly.
        userDatabaseReference.get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                System.out.println(dataSnapshot);
                meals.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Meal meal = snapshot.getValue(Meal.class);
                    meals.add(meal);
                }

                List<PieEntry> calories = new ArrayList<>();

                if (meals.size() >= 4) {
                    for (int i = meals.size() - 1; i > meals.size() - 5; i--) {
                        calories.add(new PieEntry(meals.get(i).getCalories(),
                                meals.get(i).getName().charAt(0)));
                    }

                    PieDataSet pieDataSet = new PieDataSet(calories, "Calories");
                    pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                    pieDataSet.setValueTextColor(Color.BLACK);
                    pieDataSet.setValueTextSize(14f);

                    PieData pieData = new PieData(pieDataSet);

                    pieChart.setData(pieData);
                    pieChart.setCenterText("Last 4 Caloric Consumption");
                    pieChart.setCenterTextSize(9f);
                    pieChart.invalidate();
                    pieChart.animate();
                }
            }
        }).addOnFailureListener(e -> {
            Log.e("InputMealFragment", "Error fetching data", e);
            // Handle any errors
        });
    }

    private void fetchBarDataFromFirebase() {
        userDatabaseReference.get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                System.out.println(dataSnapshot);
                meals.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Meal meal = snapshot.getValue(Meal.class);
                    meals.add(meal);
                }

                ArrayList<BarEntry> calories = new ArrayList<>();
                HashMap<Integer, Float> caloriesPerYear = new HashMap<>();

                for (Meal meal : meals) {
                    int year = Integer.parseInt(meal.getDate().substring(0, 4));
                    float caloriesVal = meal.getCalories();
                    caloriesPerYear.put(year, caloriesPerYear.getOrDefault(year, 0f) + caloriesVal);
                }

                ArrayList<BarEntry> caloriesEntries = new ArrayList<>();
                for (Map.Entry<Integer, Float> entry : caloriesPerYear.entrySet()) {
                    caloriesEntries.add(new BarEntry(entry.getKey(), entry.getValue()));
                }


                BarDataSet barDataSet = new BarDataSet(caloriesEntries, "Calories");
                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                barDataSet.setValueTextColor(Color.BLACK);
                barDataSet.setValueTextSize(14f);

                BarData barData = new BarData(barDataSet);

                barChart.setFitBars(true);
                barChart.setData(barData);
                barChart.getDescription().setText("Caloric Consumption");
                barChart.animateY(2000);

            }

        }).addOnFailureListener(e -> {
            Log.e("InputMealFragment", "Error fetching data", e);
            // Handle any errors
        });



    }


    //>>>>>>> Stashed changes
    private void saveMealToDatabase() {
        String mealName = mealNameEditText.getText().toString().trim();
        String calorieCountStr = calorieCountEditText.getText().toString().trim();

        if (!mealName.isEmpty() && !calorieCountStr.isEmpty()) {
            int calorieCount = Integer.parseInt(calorieCountStr);
            String uid = auth.getCurrentUser().getUid();
            Map<String, Object> meals = new HashMap<>();
            meals.put("name", mealName);
            meals.put("calories", calorieCount);
            meals.put("date", getCurrentDate());

            db.child("meal").child(uid).push().setValue(meals);

            mealNameEditText.setText("");
            calorieCountEditText.setText("");

            // this part is cooked - should grab user's gender, height, weight
            // check if valid, set text to updates and to calorie goal if not set
            // not sure what the issue is -
            // just crashes on a database request with "Task is not complete"
            // need to iterate over all meals consumed and calculate total calories consumed

            // set the data stuff - if set on app start, use that. if changed
            // post running, update
            db.child("users").child(uid).get().addOnSuccessListener(dataSnapshot -> {

                if (dataSnapshot.exists()) {

                    String gender = dataSnapshot.child("gender").getValue(String.class);
                    double height = dataSnapshot.child("height").getValue(Double.class);
                    double weight = dataSnapshot.child("weight").getValue(Double.class);

                    int calories = InputMealViewModel.calculateCalorieGoal(height,
                            (int) weight, gender == "Male" ? 1 : 0);
                    heighttext.setText("User Height: " + (Double.toString(height)) + " (meters)");
                    weighttext.setText("User Weight: " + (Double.toString(weight)) +  " (kg)");
                    gendertext.setText("User Gender: " + gender);
                    goaltext.setText("Goal: " + Integer.toString(calories) + " (Cal)");

                } else {
                    System.out.println("uid doesn't exist, unset data?");
                }

            });

            db.child("meal").child(uid).get().addOnSuccessListener(dataSnapshot -> {

                if (dataSnapshot.exists()) {

                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                    int consumed = 0;
                    for (DataSnapshot mealchild : children) {
                        System.out.println(consumed);
                        Iterable<DataSnapshot> thingy = mealchild.getChildren();

                        System.out.println("indexed into db");
                        System.out.println(mealchild.child("date").getValue());
                        System.out.println(mealchild.getValue());
                        if (mealchild.child("date").getValue() != null
                                && mealchild.child("date").getValue(String.class)
                                .equals(getCurrentDate())) {
                            consumed += mealchild.child("calories").getValue(Integer.class);
                        }
                    }
                    consumedtext.setText("Consumed: " + Integer.toString(consumed) + " (Cal)");

                } else {
                    System.out.println("uid doesn't exist, unset data?");
                }
            });


        }

    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
