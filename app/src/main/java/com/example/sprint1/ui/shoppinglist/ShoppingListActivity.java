package com.example.sprint1.ui.shoppinglist;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sprint1.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class ShoppingListActivity extends AppCompatActivity {
    private ListView listViewShoppingList;
    private Button btnAddItem;
    private EditText editTextItemName, editTextQuantity;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> shoppingList;
    private DatabaseReference shoppingListRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_list);

        // Initialize Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // User is not signed in
            // You can handle this case according to your app's logic
        } else {
            String userId = currentUser.getUid();
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            shoppingListRef = dbRef.child("shopping_list").child(userId);
        }

        // Initialize views
        listViewShoppingList = findViewById(R.id.shoplistRecyclerView);
        btnAddItem = findViewById(R.id.btnAddItem1);
        editTextItemName = findViewById(R.id.editTextItemName1);
        editTextQuantity = findViewById(R.id.editTextQuantity1);

        // Initialize shopping list
        shoppingList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, shoppingList);
        listViewShoppingList.setAdapter(adapter);

        // Load shopping list from Firebase
        loadShoppingList();

        // Add item button click listener
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToShoppingList();
            }
        });

        // Shopping list item click listener
        listViewShoppingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle item click if needed
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
                        String item = snapshot.getKey() + ": " + snapshot.getValue(String.class);
                        shoppingList.add(item);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ShoppingListActivity.this, "Failed to load shopping list.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Firebase reference is null, handle this according to your app's logic
            System.out.println("empty shopping list");
        }
    }

    // Method to add item to shopping list
    private void addItemToShoppingList() {
        if (shoppingListRef != null) {
            String itemName = editTextItemName.getText().toString().trim();
            String quantity = editTextQuantity.getText().toString().trim();

            if (!itemName.isEmpty() && !quantity.isEmpty()) {
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
                        Toast.makeText(ShoppingListActivity.this, "Failed to add item to shopping list.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Please enter item name and quantity.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Firebase reference is null, handle this according to your app's logic
            System.out.println("didn't add");
        }
    }
}