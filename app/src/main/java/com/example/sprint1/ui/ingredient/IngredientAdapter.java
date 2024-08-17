package com.example.sprint1.ui.ingredient;
//package com.example.sprint1.ui.recipe;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sprint1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class IngredientAdapter extends
        RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    private List<String> ingrNames;
    private List<String> recipeDetails;

    public void updateData(List<String> newNames, List<String> newDetails) {
        this.getIngrNames().clear();
        this.getIngrNames().addAll(newNames);
        this.getRecipeDetails().clear();
        this.getRecipeDetails().addAll(newDetails);
        notifyDataSetChanged();
    }

    public IngredientAdapter(List<String> recipeNames, List<String> recipeDetails) {
        this.setIngrNames(recipeNames);
        this.setRecipeDetails(recipeDetails);
    }
    @NonNull
    @Override
    public IngredientAdapter.IngredientViewHolder
        onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredient_list_item,
                parent, false);
        return new IngredientAdapter.IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientAdapter.IngredientViewHolder
                                             holder,
                                 @SuppressLint("RecyclerView") int position) {
        holder.recipeNameTextView.setText(getIngrNames().get(position));
        holder.recipeDescriptionTextView.setText(getRecipeDetails().get(position));

        holder.increaseButton.setOnClickListener(v -> {
            holder.increaseButton.setEnabled(false);

            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference pantryRef = dbRef.child("pantry").child(userId);

            pantryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ingredientSnapshot : dataSnapshot.getChildren()) {
                        String currentName =
                                ingredientSnapshot.child("name").getValue(String.class);
                        if (getIngrNames().get(position).equals(
                                currentName)) {
                            Long currentQuantity = ingredientSnapshot.child("quantity")
                                    .getValue(Long.class);
                            int newQuantity = currentQuantity != null
                                    ? currentQuantity.intValue() + 1 : 1;

                            ingredientSnapshot.getRef().child("quantity").setValue(newQuantity)
                                    .addOnSuccessListener(aVoid -> {
                                        String newDetails = "Quantity: " + newQuantity;
                                        getRecipeDetails().set(position, newDetails);
                                        notifyItemChanged(position);
                                        holder.increaseButton.setEnabled(true);
                                    })
                                    .addOnFailureListener(e ->
                                            holder.increaseButton.setEnabled(true));
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    holder.increaseButton.setEnabled(true); // Re-enable the button on cancellation
                }
            });
        });

        holder.decreaseButton.setOnClickListener(v -> {
            holder.decreaseButton.setEnabled(false); // Temporarily disable the button

            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference pantryRef = dbRef.child("pantry").child(userId);

            pantryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ingredientSnapshot : dataSnapshot.getChildren()) {
                        String currentName = ingredientSnapshot.child("name")
                                .getValue(String.class);
                        if (getIngrNames().get(position).equals(currentName)) {
                            Long currentQuantity =
                                    ingredientSnapshot.child("quantity").getValue(Long.class);
                            int newQuantity = (currentQuantity != null && currentQuantity > 0)
                                    ? currentQuantity.intValue() - 1 : 0;

                            if (newQuantity == 0) {
                                ingredientSnapshot.getRef().removeValue()
                                        .addOnSuccessListener(aVoid -> {
                                            getIngrNames().remove(position);
                                            getRecipeDetails().remove(position);
                                            notifyItemRemoved(position);
                                            holder.decreaseButton.setEnabled(true);
                                        })
                                        .addOnFailureListener(e ->
                                                holder.decreaseButton.setEnabled(true));
                                break;
                            }

                            ingredientSnapshot.getRef().child("quantity").setValue(newQuantity)
                                    .addOnSuccessListener(aVoid -> {
                                        String newDetails = "Quantity: " + newQuantity;
                                        getRecipeDetails().set(position, newDetails);
                                        notifyItemChanged(position);
                                        holder.decreaseButton.setEnabled(true);
                                    })
                                    .addOnFailureListener(e ->
                                            holder.decreaseButton.setEnabled(true));
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    holder.decreaseButton.setEnabled(true); // Re-enable the button on cancellation
                }
            });
        });

    }

    private int extractQuantity(String details) {
        try {
            String[] parts = details.split(": ");
            return Integer.parseInt(parts[1]);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        return getIngrNames().size();
    }

    public void updateIngredientInFirebase(String ingredientName, int newQuantity) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Reference to the user's pantry
        DatabaseReference pantryRef = dbRef.child("pantry").child(userId);

        // Loop through each ingredient in the user's pantry
        pantryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ingredientSnapshot : dataSnapshot.getChildren()) {
                    // Check if the ingredient name matches
                    String currentName = ingredientSnapshot.child("name").getValue(String.class);
                    if (ingredientName.equals(currentName)) {
                        // If it's a match, update the quantity of this ingredient
                        ingredientSnapshot.getRef().child("quantity").setValue(newQuantity)
                                .addOnSuccessListener(aVoid ->
                                        System.out.println(
                                                "Ingredient quantity successfully updated for "
                                                        + ingredientName))
                                .addOnFailureListener(e ->
                                        System.err.println(
                                                "Failed to update ingredient quantity for "
                                                        + ingredientName));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public List<String> getIngrNames() {
        return ingrNames;
    }

    public void setIngrNames(List<String> ingrNames) {
        this.ingrNames = ingrNames;
    }

    public List<String> getRecipeDetails() {
        return recipeDetails;
    }

    public void setRecipeDetails(List<String> recipeDetails) {
        this.recipeDetails = recipeDetails;
    }


    static class IngredientViewHolder extends RecyclerView.ViewHolder {
        private TextView recipeNameTextView;
        private TextView recipeDescriptionTextView;
        private Button increaseButton = itemView.findViewById(R.id.increaseButton);
        private Button decreaseButton = itemView.findViewById(R.id.decreaseButton);

        IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeNameTextView = itemView.findViewById(R.id.ingredientNameTextView);
            recipeDescriptionTextView = itemView.findViewById(R.id.ingredientQuantityTextView);
        }
    }

}

