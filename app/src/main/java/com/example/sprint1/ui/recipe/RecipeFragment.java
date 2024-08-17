package com.example.sprint1.ui.recipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Pair; // For pairing recipe names and details

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

//import com.example.sprint1.databinding.FragmentDashboardBinding;
import com.example.sprint1.R;
import com.example.sprint1.databinding.FragmentRecipeBinding;
import com.example.sprint1.model.DatabaseSingleton;
import com.example.sprint1.model.Ingredient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.Map;

import com.example.sprint1.model.pantry_items.PantryItem;

public class RecipeFragment extends Fragment {

    private FragmentRecipeBinding binding;
    private RecipeViewModel recipeViewModel;
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private EditText ingredientNameEditText;
    private EditText quantityEditText;
    private EditText recipeNameEditText;
    private Button addIngredientButton;
    private RecyclerView recipesRecyclerView;
    private RecipeAdapter recipeAdapter;

    private boolean isSortedAscending = true; // Tracks sort order

    private boolean isSortedByIngredients = false;
    private List<String> originalRecipeNames = new ArrayList<>();
    private List<String> originalRecipeDetails = new ArrayList<>();

    private List<String> recipeNames = new ArrayList<>();
    private List<String> recipeDetails = new ArrayList<>();
    private Button filterByNameButton;
    private Button filterByItemCountButton;

    // RecipeName: key, MissingIngredientNames: value
    private Map<String, ArrayList<String>> missingRecipeIngredientNames = new HashMap<>();

    // RecipeName: key, MissingIngredientQuantities: value
    private Map<String, Long> getMissingRecipeIngredientQuantities = new HashMap<String, Long>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
        binding = FragmentRecipeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference();
        recipeNameEditText = root.findViewById(R.id.editRecipeName);
        ingredientNameEditText = root.findViewById(R.id.editTextIngredientName);
        quantityEditText = root.findViewById(R.id.editTextIngredientQuantity);
        addIngredientButton = root.findViewById(R.id.buttonAddIngredient);

        filterByNameButton = root.findViewById(R.id.filterByNameButton);
        Button sortAlphabeticallyButton = root.findViewById(R.id.sortAlphabeticallyButton);

        filterByNameButton.setOnClickListener(v -> {
            String filterText = recipeNameEditText.getText().toString().trim().toLowerCase();
//            System.out.println(filterText);

            List<String> filteredNames = new ArrayList<>();
            List<String> filteredDetails = new ArrayList<>();
//            System.out.println(getRecipeNames());

            for (int i = 0; i < getRecipeNames().size(); i++) {
                if (getRecipeNames().get(i).toLowerCase().contains(filterText)) {
                    filteredNames.add(getRecipeNames().get(i));
                    filteredDetails.add(getRecipeDetails().get(i));
                }
            }
//            System.out.println(filteredNames);
            RecipeAdapter newAdapter = new RecipeAdapter(filteredNames, filteredDetails);
            recipesRecyclerView.setAdapter(newAdapter); });

        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipeNameString = recipeNameEditText.getText().toString().trim();
                ArrayList<Ingredient> ingredientList = new ArrayList<>();
                List<String> ingredientsNamesText =
                        Arrays.asList(ingredientNameEditText.getText().toString().split(","));
                List<String> ingredientsQuantitiesText =
                        Arrays.asList(quantityEditText.getText().toString().split(","));
                if (recipeNameString.isEmpty()) {
                    recipeNameEditText.setError("Recipe name cannot be empty");
                    return;
                }
                if (recipeNameString.isEmpty() || ingredientsNamesText.get(0).isEmpty()
                        || ingredientsQuantitiesText.get(0).isEmpty()) {
                    recipeNameEditText.setError("Cannot have empty string in any of the fields");
                    return;
                }
                if (ingredientsNamesText.size() != ingredientsQuantitiesText.size()) {
                    ingredientNameEditText
                            .setError("Ingredient list and quantities must have same size");
                    return;
                }

                for (int i = 0; i < ingredientsNamesText.size(); i++) {
                    try {
                        int quantityText =
                                Integer.parseInt(ingredientsQuantitiesText.get(i).trim());
                        if (quantityText <= 0) {
                            throw new NumberFormatException();
                        }

                        ingredientList.add(new
                                Ingredient(ingredientsNamesText.get(i).trim(), quantityText, 0));

                    } catch (NumberFormatException e) {
                        quantityEditText.setError("Quantities must be positive.");
                        return;
                    }
                }
                addRecipe(recipeNameString, ingredientList);
                //clear everything once done
                recipeNameEditText.getText().clear();
                ingredientNameEditText.getText().clear();
                quantityEditText.getText().clear();
            }


        });

        // Initialize RecyclerView and its adapter
        recipesRecyclerView = root.findViewById(R.id.recipesRecyclerView);
        recipeAdapter = new RecipeAdapter(new ArrayList<>(), new ArrayList<>());
        recipesRecyclerView.setAdapter(recipeAdapter);
        recipesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Fetch recipes from Firebase and update the adapter
        fetchRecipes();

        sortAlphabeticallyButton.setOnClickListener(v -> {
//            System.out.println("SORT");
            // Pair up the names and details
            List<Pair<String, String>> pairedList = new ArrayList<>();
            for (int i = 0; i < getRecipeNames().size(); i++) {
                pairedList.add(new Pair<>(getRecipeNames().get(i), getRecipeDetails().get(i)));
            }

            // Sort the paired list alphabetically by names
            if (isSortedAscending) {
                // Sort by name in descending order
                Collections.sort(pairedList, (pair1, pair2) -> pair2.first.compareTo(pair1.first));
                isSortedAscending = false;
            } else {
                // Sort by name in ascending order
                Collections.sort(pairedList, (pair1, pair2) -> pair1.first.compareTo(pair2.first));
                isSortedAscending = true;
            }

            // Extract the sorted names and details
            List<String> sortedNames = new ArrayList<>();
            List<String> sortedDetails = new ArrayList<>();
            for (Pair<String, String> pair : pairedList) {
                sortedNames.add(pair.first);
                sortedDetails.add(pair.second);
            }

            // Update the adapter with the sorted data
            // RecipeAdapter newAdapter = new RecipeAdapter(sortedNames, sortedDetails);
            // recipesRecyclerView.setAdapter(newAdapter);
            RecipeAdapter newAdapter = new RecipeAdapter(sortedNames, sortedDetails);
            recipesRecyclerView.setAdapter(newAdapter);
        });


        return root;
    }


    private int getMinItemCountFromUser() {
        // Example:
        return 3; // Replace this with actual dynamic user input
    }


    private void filterRecipes(String text) {
        List<String> filteredNames = new ArrayList<>();
        List<String> filteredDetails = new ArrayList<>();

        for (int i = 0; i < originalRecipeNames.size(); i++) {
            String name = originalRecipeNames.get(i);
            if (name.toLowerCase().contains(text.toLowerCase())) {
                filteredNames.add(name);
                filteredDetails.add(originalRecipeDetails.get(i));
            }
        }

        // Assuming RecipeAdapter has a method to update data
        recipeAdapter.updateData(filteredNames, filteredDetails);
    }
  
    public void check(String recipeName, List<String> ingredientNames, List<String> quantities) {
        if (recipeName.isEmpty()) {
            recipeNameEditText.setError("Recipe name cannot be empty");
            return;
        }
        if (recipeName.isEmpty()
                || ingredientNames.get(0).isEmpty() || quantities.get(0).isEmpty()) {
            recipeNameEditText.setError("Cannot have empty string in any of the fields");
            return;
        }
        if (ingredientNames.size() != quantities.size()) {
            ingredientNameEditText.setError("Ingredient list and quantities must have same size");
            return;
        }
    }

    private void addRecipe(String recipeName, ArrayList<Ingredient> ingredientList) {
        String recipeKey = ref.child("cookbook").push().getKey();
        if (recipeKey == null) {
            // Handle the case where the recipeKey wasn't generated properly
            Toast.makeText(getContext(), "Failed to add recipe.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Setting the recipe name
        ref.child("cookbook").child(recipeKey).child("Name").setValue(recipeName);

        // Construct the ingredients string to add to recipeDetails
        StringBuilder ingredientsBuilder = new StringBuilder();
        for (int i = 0; i < ingredientList.size(); i++) {
            // Extracting each ingredient from the list
            Ingredient ingredient = ingredientList.get(i);

            // Adding to Firebase
            DatabaseReference ingredientRef =
                    ref.child("cookbook").child(recipeKey).child("Ingredients").push();
            ingredientRef.child("name").setValue(ingredient.getName());
            ingredientRef.child("quantity").setValue(ingredient.getQuantity());

            // Building the string for local update
            if (i > 0) {
                ingredientsBuilder.append(", ");
            }
            ingredientsBuilder.append(ingredient.getName())
                    .append(" (").append(ingredient.getQuantity()).append(")");
        }

        // Update local lists
        getRecipeNames().add(recipeName);
        getRecipeDetails().add(ingredientsBuilder.toString());

        // Refresh the adapter with the updated lists
        recipeAdapter.updateData(getRecipeNames(), getRecipeDetails());

        // Optionally, clear input fields if necessary
        recipeNameEditText.getText().clear();
        ingredientNameEditText.getText().clear();
        quantityEditText.getText().clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    public void fetchRecipes() {
        DatabaseReference cookbookRef =
                FirebaseDatabase.getInstance().getReference().child("cookbook");
        DatabaseSingleton dbs = DatabaseSingleton.getInstance();
        DatabaseReference userPantryRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://sprint1-87a00-default-rtdb.firebaseio.com").child("pantry").child(dbs.getUid());
        ArrayList<PantryItem> pantryItems = new ArrayList<>();

        // Fetch pantry items first
        userPantryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pantryItems.clear(); // Clear existing items to ensure the list is up-to-date
                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    PantryItem item = itemSnapshot.getValue(PantryItem.class);
                    pantryItems.add(item);
                }

                // After pantry items are fetched, proceed to fetch and process recipes
                cookbookRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                        for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                            String recipeName = recipeSnapshot.child("Name").getValue(String.class);
                            getRecipeNames().add(recipeName);

                            StringBuilder ingredientsBuilder = new StringBuilder();
                            DataSnapshot ingredientsSnapshot = recipeSnapshot.child("Ingredients");
                            int totalIngredients = (int) ingredientsSnapshot.getChildrenCount();
                            int validCount = 0; // Reset for each recipe

                            int ingredientPos = 0;
                            for (DataSnapshot ingredient : ingredientsSnapshot.getChildren()) {
                                ingredientPos += 1;
                                String name = ingredient.child("name").getValue(String.class);
                                Long quantity = ingredient.child("quantity").getValue(Long.class);
                                for (PantryItem p : pantryItems) {
                                    if (p.getName().equals(name) && p.getQuantity() >= quantity) {
                                        validCount += 1;
                                        break; // Exit the loop once a match is found
                                    }
                                }
                                if (ingredientsBuilder.length() > 0) {
                                    ingredientsBuilder.append(", ");
                                }
                                ingredientsBuilder.append(name)
                                        .append(" (").append(quantity).append(")");


                            }
                            if (validCount == totalIngredients) {
                                ingredientsBuilder.append("CAN MAKE");
                            }
//                            System.out.println(ingredientsBuilder.toString());
                            getRecipeDetails().add(ingredientsBuilder.toString());
                        }

                        RecipeAdapter adapter =
                                new RecipeAdapter(getRecipeNames(), getRecipeDetails());
                        recipesRecyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(),
                                "Failed to load cookbook.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(),
                        "Failed to load pantry items.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String validateInputs(String recipeName,
                                 List<String> ingredientNames, List<String> quantities) {
        if (recipeName.isEmpty()) {
            return "Recipe name cannot be empty";
        }
        for (String name : ingredientNames) {
            if (name.isEmpty()) {
                return "Cannot have empty string in any of the fields";
            }
        }
        if (ingredientNames.size() != quantities.size()) {
            return "Ingredient list and quantities must have same size";
        }

        for (String name : ingredientNames) {
            if (name.isEmpty()) {
                return "Ingredient names cannot be empty";
            }
            if (name.length() > 50) {
                return "Ingredient names cannot be longer than 50 characters";
            }
        }

        for (String quantity : quantities) {
            try {
                Integer.parseInt(quantity);
            } catch (NumberFormatException e) {
                return "Quantities should be numeric";
            }
        }
        return null; // Indicates no error
    }

    public List<String> getRecipeNames() {
        return recipeNames;
    }

    public void setRecipeNames(List<String> recipeNames) {
        this.recipeNames = recipeNames;
    }

    public List<String> getRecipeDetails() {
        return recipeDetails;
    }

    public void setRecipeDetails(List<String> recipeDetails) {
        this.recipeDetails = recipeDetails;
    }
}