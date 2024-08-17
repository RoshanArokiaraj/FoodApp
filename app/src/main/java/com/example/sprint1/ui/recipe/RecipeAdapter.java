package com.example.sprint1.ui.recipe;

import android.annotation.SuppressLint;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprint1.R;
import com.example.sprint1.model.DatabaseSingleton;
import com.example.sprint1.model.pantry_items.PantryItem;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicBoolean;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private List<String> recipeNames;
    private List<String> recipeDetails;
    private DatabaseReference shoppingListRef;

    private Map<String, ArrayList<String>> missingRecipeIngredientNames;

    private Map<String, Map<String, Long>> getMissingRecipeIngredientQuantities;



    // You may need a way to track which recipes can actually be made (e.g., a List<Boolean>)

    public RecipeAdapter(List<String> recipeNames, List<String> recipeDetails) {
        this.recipeNames = recipeNames;
        this.recipeDetails = recipeDetails;
        this.missingRecipeIngredientNames = new HashMap<>();
        this.getMissingRecipeIngredientQuantities = new HashMap<String, Map<String, Long>>();
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_list_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String recipeName = recipeNames.get(position);
        String recipeDetail = recipeDetails.get(position);

        // Set the recipe name and description (or detail)
        holder.recipeNameTextView.setText(recipeName);

        holder.additionalInfoTextView.setVisibility(View.VISIBLE);
        holder.additionalInfoTextView.setText(recipeDetail);

        holder.makeRecipeButton.setVisibility(View.VISIBLE);


        Button missingItem = holder.missingIngrButton;
        final AtomicBoolean[] toggled = {new AtomicBoolean(false)};
        missingItem.setOnClickListener(v -> {
            if(toggled[0].get()) {
                missingItem.setText("View Missing");
                holder.missingIngredients.setVisibility(View.GONE);
            } else {
                missingItem.setText("Hide Missing");
                holder.missingIngredients.setVisibility(View.VISIBLE);
            }
            toggled[0].set(!toggled[0].get());
        });

        // Assuming you have a recipeDescriptionTextView or similar in your ViewHolder
        // holder.recipeDescriptionTextView.setText(recipeDetail);

        canCookMeal(holder, position, recipeName, recipeDetail);
    }

    private synchronized void canCookMeal(@NonNull RecipeViewHolder holder, @SuppressLint("RecyclerView") int position, String recipeName, String recipeDetail) {
        // Check if recipe can be made and set visibility and text accordingly
//        System.out.println(canMake);
//        if (canMake) {
        // Make sure to show the button if the recipe can be made
//        holder.makeRecipeButton.setVisibility(View.VISIBLE);

        // Remove the "CAN MAKE" tag for displaying in the additional info
        String detailedIngredients = recipeDetail.replace("CAN MAKE", "").trim();
        holder.additionalInfoTextView.setText(detailedIngredients);


        holder.submitToShoppingList.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            String userId = currentUser.getUid();
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            shoppingListRef = dbRef.child("shopping_list").child(userId);


            for (Map.Entry<String, Long> entry : getMissingRecipeIngredientQuantities.get(recipeName).entrySet()) {
                String itemName = entry.getKey();
                Long quantity = entry.getValue();
                if (!itemName.isEmpty()) {
                    // Check if the item already exists
                    shoppingListRef.child(itemName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.getValue()!=null) {
                                // Item already exists, update quantity
                                shoppingListRef.child(itemName).setValue(String.valueOf(quantity + Long.parseLong((String) dataSnapshot.getValue())));
                            } else {
                                // Item does not exist, add new item
                                shoppingListRef.child(itemName).setValue(String.valueOf(quantity));
                            }
                            holder.missingIngredientsTextView.setVisibility(View.GONE);
                            holder.submitToShoppingList.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                } else {
                }
            }
            });

        holder.makeRecipeButton.setOnClickListener(v -> {
//            Thread t1 = new Thread(() -> {
//                fetchRecipes();
//            });
//            t1.run();
//            try {
//                t1.wait();
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
            int sjoaidjaiojdioajiodasjiodajiodajio = fetchRecipes();
            boolean canMake = recipeDetail.contains("CAN MAKE");
            // Toggle visibility and set detailed ingredients text
            if (canMake == false) {
                // can't make this recipe
                Snackbar mybar = Snackbar.make(holder.itemView, "Cannot cook recipe!", -1);
                holder.missingIngredientsTextView.setVisibility(View.VISIBLE);
                holder.submitToShoppingList.setVisibility(View.VISIBLE);
                String currRecipeName = holder.recipeNameTextView.getText().toString();
                System.out.println("This is the map" + this.getMissingRecipeIngredientQuantities);
                StringBuilder stringMissingQuantities = new StringBuilder();

                for (Map.Entry<String, Long> entry : getMissingRecipeIngredientQuantities.get(recipeName).entrySet()) {
                    String ingredient = entry.getKey();
                    Long quantity = entry.getValue();

                    // Append each key and value to the string with a descriptive format
                    stringMissingQuantities.append(ingredient).append(" (missing ").append(quantity).append(" ), ");
                }
                System.out.println(stringMissingQuantities);

                holder.missingIngredientsTextView.setText(stringMissingQuantities);
                mybar.show();
                return;
            }

            // TODO 3 OF SPRINT 4
            // AVAYE THE GOAT
//            System.out.println("triggered food cook");
            ArrayList<String> ingredientNames = extractFoods(detailedIngredients);
            ArrayList<Integer> quantities = extractNums(detailedIngredients);

            // if in this state, guaranteed that each ingredient's quantity >= required
            // or not necessarily i guess, just as of the last time the adapter was updated
            // so still should probably check quantities so no edge cases?

            holder.makeRecipeButton.setEnabled(false); // disable temporarily
            // TODO:
            // sanity checking that the recipe is in fact valid (necessary?)
            // a) iterate over pantry database and check quantities -> use code from Pau S3

            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference pantryRef = dbRef.child("pantry").child(userId);

//            AtomicInteger mealCal = new AtomicInteger();
            final int[] mealCal = {0};
            pantryRef.get().addOnSuccessListener(dataSnapshot -> {
                int summation = 0;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ingredientSnapshot : dataSnapshot.getChildren()) {
                        String currentName = ingredientSnapshot.child("name")
                                .getValue(String.class);
                        for(int i = 0; i < ingredientNames.size(); i++) {
                            String name = ingredientNames.get(i);
                            if (name.equals(currentName)) {
                                Long currentQuantity =
                                        ingredientSnapshot.child("quantity").getValue(Long.class);

                                Long curCals = ingredientSnapshot.child("calories").getValue(Long.class);
                                if (curCals == null) {
                                    curCals = 0L;
                                }
                                // if newQuantity >= quantities[i], update
                                // otherwise leave as is
                                // assuming it is valid bc
                                // checked above if valid on page load
                                int newQuantity = (currentQuantity != null && currentQuantity >= quantities.get(i))
                                        ? currentQuantity.intValue() - quantities.get(i) : currentQuantity.intValue();

//                                System.out.println((int) (quantities.get(i) * curCals.intValue()));
                                summation += (int) (quantities.get(i) * curCals.intValue());

                                if (newQuantity == 0) {
                                    ingredientSnapshot.getRef().removeValue()
                                            .addOnSuccessListener(aVoid -> {
//                                                recipeNames.remove(position);
//                                                recipeDetails.remove(position);
//                                                notifyItemRemoved(position);
                                                holder.makeRecipeButton.setEnabled(true);
                                            })
                                            .addOnFailureListener(e ->
                                                    holder.makeRecipeButton.setEnabled(true));
                                    break;
                                }

                                ingredientSnapshot.getRef().child("quantity").setValue(newQuantity)
                                        .addOnSuccessListener(aVoid -> {
                                            // don't need to change labesl - just reenable data
//                                            String newDetails = "Quantity: " + newQuantity;
//                                            recipeDetails.set(position, newDetails);
//                                            notifyItemChanged(position);
                                            holder.makeRecipeButton.setEnabled(true);
                                        })
                                        .addOnFailureListener(e ->
                                                holder.makeRecipeButton.setEnabled(true));
                                break;
                            }
                        }
                    }

                    fetchRecipes();


                    // b) if valid, go back to pantry and decrement quantities (remove if q==0)
                    //  a,b -> use code from Pau S3; already covers modifying quantities and removing
                    //  items if q==0
                    // done by above?

                    // testing verified, subtraction/checks are in fact done by above


                    // c) update calorie count for day
                    // c -> reference code from Sprint 2 for meal input, same type of database interaction
//                    int accMealCal = mealCal[0];
//                    System.out.println(summation);
                    saveMealToDatabase(recipeName, summation);

                }
//                System.out.println(summation);
//                mealCal[0] = summation;
                Snackbar mybar = Snackbar.make(holder.itemView, "Successfully cooked!", -1);
                mybar.show();
            });

        });
        //} /*else {
        // Hide the button and additional info if the recipe cannot be made
//            holder.makeRecipeButton.setVisibility(View.GONE);
//            holder.additionalInfoTextView.setVisibility(View.GONE);
//        }*/
    }



        public int fetchRecipes() {
        DatabaseReference cookbookRef =
                FirebaseDatabase.getInstance().getReference().child("cookbook");
        DatabaseSingleton dbs = DatabaseSingleton.getInstance();
        DatabaseReference userPantryRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://sprint1-87a00-default-rtdb.firebaseio.com").child("pantry").child(dbs.getUid());
        ArrayList<PantryItem> pantryItems = new ArrayList<>();
//        this.missingRecipeIngredientNames = new HashMap<>();
        AtomicReference<Map<String, Map<String, Long>>> l = new AtomicReference<>(new HashMap<>());
        // Fetch pantry items first
        // TODO @anir: add isSuccesful checks on t, t2
        Task t = userPantryRef.get();
        while (!t.isComplete()) {
            // spin
        }
        DataSnapshot dataSnapshot = (DataSnapshot)t.getResult();
        pantryItems.clear(); // Clear existing items to ensure the list is up-to-date
        for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
            PantryItem item = itemSnapshot.getValue(PantryItem.class);
            pantryItems.add(item);
        }

        // After pantry items are fetched, proceed to fetch and process recipes
        Task t2 = cookbookRef.get();
        while (!t2.isComplete()) {
            // spin
        }
        DataSnapshot newSnap = (DataSnapshot)t2.getResult();
        ArrayList<String> recipeNames = new ArrayList<>();
        ArrayList<String> recipeDetails = new ArrayList<>();

        for (DataSnapshot recipeSnapshot : newSnap.getChildren()) {
            String recipeName = recipeSnapshot.child("Name").getValue(String.class);
            recipeNames.add(recipeName);

            StringBuilder ingredientsBuilder = new StringBuilder();
            DataSnapshot ingredientsSnapshot = recipeSnapshot.child("Ingredients");
            int totalIngredients = (int) ingredientsSnapshot.getChildrenCount();
            int validCount = 0; // Reset for each recipe

            for (DataSnapshot ingredient : ingredientsSnapshot.getChildren()) {
                String name = ingredient.child("name").getValue(String.class);
                Long quantity = ingredient.child("quantity").getValue(Long.class);

                int pantryPos = 0;
                for (int pantryIndex = 0; pantryIndex < pantryItems.size(); pantryIndex++) {
//                                System.out.println(pantryItems.get(pantryIndex).getName() + " " + name);
                    if (pantryItems.get(pantryIndex).getName().equals(name) && pantryItems.get(pantryIndex).getQuantity() >= quantity) {
                        validCount += 1;
                        break; // Exit the loop once a match is found
                    } else if (pantryItems.get(pantryIndex).getName().equals(name)) {
//                                    System.out.println("THIS TWO" + pantryItems.get(pantryIndex).getName() + " " + pantryItems.get(pantryIndex).getQuantity());
                        long moreQuantity = quantity - pantryItems.get(pantryIndex).getQuantity();
                        ArrayList<String> newMissingNameList = missingRecipeIngredientNames.get(recipeName);
                        if (newMissingNameList == null) {
                            newMissingNameList = new ArrayList<>();
                        }
                        newMissingNameList.add(pantryItems.get(pantryIndex).getName());


                        missingRecipeIngredientNames.put(recipeName, newMissingNameList);

                        Map<String, Long> newMissingMap = getMissingRecipeIngredientQuantities.get(recipeName);
                        if (newMissingMap == null) {
                            newMissingMap = new HashMap<>();
                        }
                        newMissingMap.put(pantryItems.get(pantryIndex).getName(), moreQuantity);

                        getMissingRecipeIngredientQuantities.put(recipeName, newMissingMap);

                        break;
                    }

                    if (pantryIndex == pantryItems.size() - 1) {
                        System.out.println("THIS ONE" + name + " " + pantryItems.get(pantryIndex).getQuantity());
                        ArrayList<String> newMissingNameList = new ArrayList<>();
                        newMissingNameList.add(name);

                        missingRecipeIngredientNames.put(recipeName, newMissingNameList);
//
                        Map<String, Long> newMissingMap = new HashMap<>();
                        newMissingMap.put(name, quantity);
                        System.out.println(newMissingMap);
                        getMissingRecipeIngredientQuantities.put(recipeName, newMissingMap);
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
            recipeDetails.add(ingredientsBuilder.toString());
        }
//                    System.out.println(recipeNames);
//                    System.out.println(recipeDetails);
        updateData(recipeNames, recipeDetails);
        l.set(getMissingRecipeIngredientQuantities);
//                    getMissingRecipeIngredientQuantities = l.get();
//                    System.out.println("hee hee" + getMissingRecipeIngredientQuantities);
        System.out.println("penis" + l.get());

//            System.out.println(l.get());

        Map<String, Map<String, Long>> empty = new HashMap<>();

//        while (l.get().equals(empty)) {
//            try {
//                Thread.sleep(20);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }

        this.getMissingRecipeIngredientQuantities = l.get();
        System.out.println("dhasuodhuoauodshoua " + getMissingRecipeIngredientQuantities);
        return 0;
        }



    @Override
    public int getItemCount() {
        return recipeNames.size();
    }

    public static ArrayList<String> extractFoods(String s) {
        String[] ingreds = s.split(", ");
        ArrayList<String> ingredientNames = new ArrayList<>();
        ArrayList<Integer> quantities = new ArrayList<>();
        for (String i : ingreds) {
            ingredientNames.add(i.split(" ")[0]);
            quantities.add(Integer.parseInt(i.substring(i.indexOf("(")+1, i.indexOf(")"))));
        }
        return ingredientNames;
    }

    public static ArrayList<Integer> extractNums(String s) {
        String[] ingreds = s.split(", ");
        ArrayList<String> ingredientNames = new ArrayList<>();
        ArrayList<Integer> quantities = new ArrayList<>();
        for (String i : ingreds) {
            ingredientNames.add(i.split(" ")[0]);
            quantities.add(Integer.parseInt(i.substring(i.indexOf("(")+1, i.indexOf(")"))));
        }
        return quantities;
    }

    public void updateData(List<String> newNames, List<String> newDetails) {
        recipeNames.clear();
        recipeNames.addAll(newNames);
        recipeDetails.clear();
        recipeDetails.addAll(newDetails);
        notifyDataSetChanged();
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        private TextView recipeNameTextView;

        private TextView recipeDescriptionTextView;

        private TextView additionalInfoTextView;
        private Button makeRecipeButton;

        private TextView missingIngredientsTextView;
        private Button submitToShoppingList;

        private Button missingIngrButton;
        private TextView missingIngredients;

        public View itemView;

        RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeNameTextView = itemView.findViewById(R.id.recipeNameTextView);
            recipeDescriptionTextView = itemView.findViewById(R.id.recipeDescriptionTextView);
            makeRecipeButton = itemView.findViewById(R.id.makeRecipeButton);
            missingIngrButton = itemView.findViewById(R.id.missingIngrButton);
            additionalInfoTextView = itemView.findViewById(R.id.additionalInfoTextView);
            missingIngredientsTextView = itemView.findViewById(R.id.missingIngredientsTextView);
            submitToShoppingList = itemView.findViewById(R.id.submitToShoppingList);
            missingIngredients = itemView.findViewById(R.id.missingItemsTextView);
            this.itemView = itemView;
        }
    }

    private void saveMealToDatabase(String mealName, int calorieCount) {

//        int calorieCount = Integer.parseInt(calorieCountStr);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        String uid = auth.getCurrentUser().getUid();
        Map<String, Object> meals = new HashMap<>();
        meals.put("name", mealName);
        meals.put("calories", calorieCount);
        meals.put("date", getCurrentDate());

        db.child("meal").child(uid).push().setValue(meals);

    }


    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

}
