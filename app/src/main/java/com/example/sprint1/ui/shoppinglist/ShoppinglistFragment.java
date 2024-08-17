package com.example.sprint1.ui.shoppinglist;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.sprint1.R;
//import com.example.sprint1.databinding.FragmentNotificationsBinding;

public class ShoppinglistFragment extends Fragment {
    private RecyclerView listViewShoppingList;
    private Button btnAddItem;
    private Button buyItemsButton;

    private EditText editTextItemName, editTextQuantity;
    private ShoppingListAdapter adapter;
    private ArrayList<String> shoppingList;
    private DatabaseReference shoppingListRef;
    private int selectedItemPosition = -1;
    private String currentSelectedItem;
    private int currentQuantityValue;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        // Initialize Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            shoppingListRef = dbRef.child("shopping_list").child(userId);
        }

        // Initialize views
        listViewShoppingList = view.findViewById(R.id.shoplistRecyclerView);
        btnAddItem = view.findViewById(R.id.btnAddItem1);
        buyItemsButton = view.findViewById(R.id.buyItemsButton1);

        editTextItemName = view.findViewById(R.id.editTextItemName1);
        editTextQuantity = view.findViewById(R.id.editTextQuantity1);

        // Initialize shopping list
        shoppingList = new ArrayList<>();
        listViewShoppingList.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ShoppingListAdapter(requireContext(), shoppingList, shoppingListRef);
        listViewShoppingList.setAdapter(adapter);

        // Load shopping list from Firebase
        loadShoppingList();

        buyItemsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buySelectedItems();
            }
        });

        // Add item button click listener
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToShoppingList();
            }
        });

        buyItemsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buySelectedItems();
            }
        });


        // Shopping list item click listener
        return view;
    }

    private void buySelectedItems() {
        for (int i = 0; i < adapter.getItemCount(); i++) {
            if (adapter.itemCheckedStates.get(i)) {
                String item = shoppingList.get(i);
                String[] parts = item.split(": ");
                String itemName = parts[0];
                String quantity = parts[1];
                int quantityInt = Integer.parseInt(quantity);

                // Move to pantry logic (not implemented here, but you would update the pantry database)
                updatePantry(itemName, quantityInt);

                // Remove from Firebase
                shoppingListRef.child(itemName).removeValue().addOnSuccessListener(aVoid -> {
                    // Successfully removed item
                    Toast.makeText(requireContext(), itemName + " bought and removed from shopping list.", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(requireContext(), "Failed to remove " + itemName + " from shopping list.", Toast.LENGTH_SHORT).show();
                });
            }
        }

        // clear checked items
        adapter.clearCheckedItems();

        // Refresh the shopping list to reflect changes
        loadShoppingList();
    }

    private void updatePantry(String itemName, int quantityToAdd) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Context mContext = getContext(); // Get the context from the fragment
        if (currentUser == null) {
            Toast.makeText(mContext, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference pantryRef = dbRef.child("pantry").child(userId);

        // Query the pantry to find the item
        pantryRef.orderByChild("name").equalTo(itemName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // If item exists, update its quantity
                    for (DataSnapshot ingredientSnapshot : dataSnapshot.getChildren()) {
                        Long currentQuantity = ingredientSnapshot.child("quantity").getValue(Long.class);
                        int newQuantity = (currentQuantity != null ? currentQuantity.intValue() : 0) + quantityToAdd;

                        ingredientSnapshot.getRef().child("quantity").setValue(newQuantity)
                                .addOnSuccessListener(aVoid -> Toast.makeText(mContext, "Pantry updated for " + itemName, Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(mContext, "Failed to update pantry", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    // If item does not exist, add new item with the quantity
                    DatabaseReference newPantryItemRef = pantryRef.push(); // Creates a new child with a unique key
                    Map<String, Object> newItem = new HashMap<>();
                    newItem.put("name", itemName);
                    newItem.put("quantity", quantityToAdd);
                    newPantryItemRef.setValue(newItem)
                            .addOnSuccessListener(aVoid -> Toast.makeText(mContext, "New item added to pantry: " + itemName, Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(mContext, "Failed to add item to pantry", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(mContext, "Error accessing database", Toast.LENGTH_SHORT).show();
            }
        });
    }





    // Method to load shopping list from Firebase
    private void loadShoppingList() {
        if (shoppingListRef != null) {
            shoppingListRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    shoppingList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String item = snapshot.getKey() + ": " + snapshot.getValue();
                        shoppingList.add(item);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(requireContext(), "Failed to load shopping list.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Firebase reference is null, handle this according to your app's logic
        }
    }

    // Method to add item to shopping list
    private void addItemToShoppingList() {
        if (shoppingListRef != null) {
            String itemName = editTextItemName.getText().toString().trim();
            String quantity = editTextQuantity.getText().toString().trim();
            try {
                Long casted = Long.parseLong(quantity);
                if (!itemName.isEmpty() && !quantity.isEmpty() && (0 < casted)) {
                    // Check if the item already exists
                    shoppingListRef.child(itemName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Item already exists, update quantity
                                int newQuantity = Integer.parseInt(quantity) + Integer.parseInt(dataSnapshot.getValue(String.class));
                                shoppingListRef.child(itemName).setValue(String.valueOf(newQuantity));
                            } else {
                                // Item does not exist, add new item
                                shoppingListRef.child(itemName).setValue(quantity);
                            }
                            editTextItemName.setText("");
                            editTextQuantity.setText("");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(requireContext(), "Failed to add item to shopping list.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(requireContext(), "Please enter item name and quantity.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Invalid quantity.", Toast.LENGTH_SHORT).show();
            }


        } else {
            // Firebase reference is null, handle this according to your app's logic
        }
    }
}