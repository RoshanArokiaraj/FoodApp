package com.example.sprint1.ui.shoppinglist;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sprint1.ui.recipe.RecipeAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<String> mShoppingList;
    private DatabaseReference mShoppingListRef;

    SparseBooleanArray itemCheckedStates = new SparseBooleanArray();


    public ShoppingListAdapter(Context context, ArrayList<String> shoppingList, DatabaseReference shoppingListRef) {
        mContext = context;
        mShoppingList = shoppingList;
        mShoppingListRef = shoppingListRef;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.shopping_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String currentItem = mShoppingList.get(position);
        String[] parts = currentItem.split(": ");
        final String itemName = parts[0];
        final String quantity = parts[1];


        holder.ingredientNameTextView.setText(itemName);
        holder.ingredientQuantityTextView.setText(quantity);

        holder.itemCheckBox.setOnCheckedChangeListener(null);

        holder.itemCheckBox.setChecked(itemCheckedStates.get(position, false));

        // Set a new check listener after setting the checkbox state
        holder.itemCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            itemCheckedStates.put(position, isChecked);
        });

        holder.decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseQuantity(itemName, quantity);
            }
        });

        holder.increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseQuantity(itemName, quantity);
            }
        });
    }

    public void clearCheckedItems() {
        itemCheckedStates.clear();
        notifyDataSetChanged();  // Notify the adapter to refresh all views (checkboxes will be unchecked)
    }

    @Override
    public int getItemCount() {
        return mShoppingList != null ? mShoppingList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView ingredientNameTextView;
        public TextView ingredientQuantityTextView;
        public Button decreaseButton;
        public Button increaseButton;

        public CheckBox itemCheckBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ingredientNameTextView = itemView.findViewById(R.id.shoppingNameTextView);
            ingredientQuantityTextView = itemView.findViewById(R.id.shoppingQuantityTextView);
            decreaseButton = itemView.findViewById(R.id.decreaseButton1);
            increaseButton = itemView.findViewById(R.id.increaseButton1);
            itemCheckBox = itemView.findViewById(R.id.itemCheckBox);
        }
    }

    private void decreaseQuantity(String itemName, String quantity) {
        int newQuantity = Integer.parseInt(quantity) - 1;
        if (newQuantity > 0) {
            mShoppingListRef.child(itemName).setValue(String.valueOf(newQuantity));
        } else {
            // Remove item from the list and the database
            mShoppingListRef.child(itemName).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // Remove item from the list
                    mShoppingList.remove(itemName + ": " + quantity);
                    notifyDataSetChanged();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle failure to remove item
                }
            });
        }
    }

    private void increaseQuantity(String itemName, String quantity) {
        int newQuantity = Integer.parseInt(quantity) + 1;
        mShoppingListRef.child(itemName).setValue(String.valueOf(newQuantity));
    }
}