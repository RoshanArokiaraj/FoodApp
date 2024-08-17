package com.example.sprint1.ui.ingredient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprint1.R;
import com.example.sprint1.databinding.FragmentIngredientBinding;
import com.example.sprint1.model.Ingredient;

import java.sql.Date;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Arrays;

public class IngredientFragment extends Fragment {

    private FragmentIngredientBinding binding;
    private FirebaseAuth auth;
    private DatabaseReference db;
    private ListView sv;
    private ArrayAdapter<String> adapter;

    private RecyclerView ingredientRecyclerView;
    private IngredientAdapter ingredientAdapter;

    private List<String> originalIngrNames = new ArrayList<>();
    private List<String> originalIngrDetails = new ArrayList<>();

    private List<String> ingrNames = new ArrayList<>();
    private List<String> ingrDetails = new ArrayList<>();



    public View onCreateView(
            @NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle
                    savedInstanceState) {
        IngredientViewModel ingredientViewModel =
                new ViewModelProvider(this).get(IngredientViewModel.class);
        binding = FragmentIngredientBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        EditText ingredientNameEditText = root.findViewById(R.id.input_name);
        EditText ingredientQuantityEditText = root.findViewById(R.id.input_quantity);
        EditText ingredientCaloriesEditText = root.findViewById(R.id.input_calories);
        EditText ingredientExpiryDateText = root.findViewById((R.id.expiry_date));
        auth = FirebaseAuth.getInstance();
        String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        db = FirebaseDatabase.getInstance().getReference();
        ArrayList list = new ArrayList<>();
        ingredientRecyclerView = root.findViewById(R.id.ingredientsRecyclerView);
        ingredientAdapter = new IngredientAdapter(new ArrayList<>(), new ArrayList<>());
        ingredientRecyclerView.setAdapter(ingredientAdapter);
        ingredientRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fetchIngredients();
        Button createIngredientButton = root.findViewById(R.id.button_create_ingredient);
        createIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = ingredientNameEditText.getText().toString().trim();
                String quantityString = ingredientQuantityEditText.getText().toString().trim();
                String caloriesString = ingredientCaloriesEditText.getText().toString().trim();
                String expiryDateString = ingredientExpiryDateText.getText().toString().trim();
                if (name.isEmpty()) {
                    ingredientNameEditText.setError("Name cannot be empty");
                    ingredientNameEditText.setText("");
                    ingredientQuantityEditText.setText("");
                    ingredientCaloriesEditText.setText("");
                    return;
                }
                if (quantityString.isEmpty()) {
                    ingredientQuantityEditText.setError("Quantity cannot be empty");
                    ingredientNameEditText.setText("");
                    ingredientQuantityEditText.setText("");
                    ingredientCaloriesEditText.setText("");
                    return;
                }
                int quantity;
                try {
                    quantity = Integer.parseInt(quantityString);
                } catch (NumberFormatException e) {
                    ingredientQuantityEditText.setError("Please enter a "
                            + "valid integer for the ingredient");
                    ingredientNameEditText.setText("");
                    ingredientQuantityEditText.setText("");
                    ingredientCaloriesEditText.setText("");
                    return;
                }
                if (caloriesString.isEmpty()) {
                    ingredientCaloriesEditText.setError("Calories cannot be empty");
                    ingredientNameEditText.setText("");
                    ingredientQuantityEditText.setText("");
                    ingredientCaloriesEditText.setText("");
                    return;
                }
                int calories;
                try {
                    calories = Integer.parseInt(caloriesString);
                } catch (NumberFormatException e) {
                    ingredientCaloriesEditText.setError("Please "
                            + "enter a valid integer for the calories");
                    ingredientNameEditText.setText("");
                    ingredientQuantityEditText.setText("");
                    ingredientCaloriesEditText.setText("");
                    return;
                }
                Date date;
                try {
                    if (expiryDateString.isEmpty()) {
                        date = Date.valueOf(expiryDateString);
                    }
                } catch (Exception e) {
                    ingredientExpiryDateText.setError("Please enter a "
                            + "vald date for the expiry date");
                    return;
                }
                Ingredient ingredient;
                if (!expiryDateString.isEmpty()) {
                    ingredient = new Ingredient(String.valueOf(ingredientNameEditText.getText()),
                            Integer.parseInt(String.valueOf(ingredientQuantityEditText.getText())),
                            Integer.parseInt(String.valueOf(ingredientCaloriesEditText.getText())),
                            Date.valueOf(String.valueOf(ingredientExpiryDateText.getText())));
                } else {
                    ingredient = new Ingredient(String.valueOf(ingredientNameEditText.getText()),
                            Integer.parseInt(String.valueOf(ingredientQuantityEditText.getText())),
                            Integer.parseInt(String.valueOf(ingredientCaloriesEditText.getText())));
                }
                Map<String, Object> ingr = new HashMap<>();
                ingr.put("name", name);
                ingr.put("quantity", quantity);
                ingr.put("calories", calories);
                ingredientNameEditText.setText("");
                ingredientQuantityEditText.setText("");
                ingredientCaloriesEditText.setText("");
                ArrayList list = new ArrayList();
                db.child("pantry").child(uid).get().addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        boolean sameFound = false;
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        int consumed = 0;
                        for (DataSnapshot ingrchild : children) {
                            Iterable<DataSnapshot> thingy = ingrchild.getChildren();
                            if (ingrchild.child("name").getValue(String.class) != null) {
                                sameFound = sameFound
                                        || sameIngredient(name, calories, ingrchild.child("name")
                                        .getValue(String.class),
                                        ingrchild.child("calories")
                                                .getValue(Integer.class));
                                if (sameIngredient(name, calories, ingrchild.child("name")
                                        .getValue(String.class),
                                        ingrchild.child("calories").getValue(Integer.class))) {
                                    int quant = ingrchild.child("quantity")
                                            .getValue(Integer.class) + quantity;
                                    ingrchild.child("quantity").getRef().setValue(quant);
                                }
                                String disp = ingrchild.child("name").getValue(String.class)
                                        + " | " + String.valueOf(ingrchild.child("quantity")
                                        .getValue(Long.class));
                                list.add(disp);
                            }
                        }
                        if (!sameFound) {
                            db.child("pantry").child(uid).push().setValue(ingr);
                        }
                    } else {
                        System.out.println("database push failed");
                        db.child("pantry").child(uid).push().setValue(ingr);
                    }
                });
                fetchIngredients();
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void fetchIngredients() {
        DatabaseReference pantryRef =
                FirebaseDatabase.getInstance().getReference().child("pantry").child(auth.getUid());
        System.out.println("fetching ingredients");
        pantryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> ingrNames = new ArrayList<>();
                List<String> ingrDetails = new ArrayList<>();
                for (DataSnapshot ingrsnapshot : dataSnapshot.getChildren()) {
                    String ingrname = ingrsnapshot.child("name").getValue(String.class);
                    String quant = "Quantity: "
                            + String.valueOf(ingrsnapshot.child("quantity").getValue(Long.class));
                    ingrNames.add(ingrname);
                    ingrDetails.add(quant);
//                    System.out.println("in for loop");
//                    System.out.println(Arrays.toString(ingrNames.toArray()));
//                    System.out.println(Arrays.toString(ingrDetails.toArray()));
                }


                IngredientAdapter adapter = new IngredientAdapter(ingrNames, ingrDetails);
                ingredientRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load pantry.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public static boolean sameIngredient(String name1, int cal1, String name2, int cal2) {
        if (name1 == null) {
            return false;
        }
        return (name1.equals(name2)) && (cal1 == cal2);
    }

    public List<String> getIngrNames() {
        return ingrNames;
    }

    public void setIngrNames(List<String> ingrNames) {
        this.ingrNames = ingrNames;
    }

    public List<String> getIngrDetails() {
        return ingrDetails;
    }

    public void setIngrDetails(List<String> ingrDetails) {
        this.ingrDetails = ingrDetails;
    }
}



