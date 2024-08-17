package com.example.sprint1.model;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseSingleton {
    private static volatile DatabaseSingleton instance;
    private DatabaseReference userDbf;
    private String uid;

    private DatabaseSingleton(String inputuid) {
        this.setUid(inputuid);
        userDbf = FirebaseDatabase.getInstance().getReferenceFromUrl("https://sprint1-87a00-default-rtdb.firebaseio.com").child("users").child(this.getUid());
    }

    public static synchronized DatabaseSingleton getInstance(String inputuid) {
        if (instance == null) {
            instance = new DatabaseSingleton(inputuid);
        }
        return instance;
    }

    public static synchronized DatabaseSingleton getInstance() {
        if (instance != null) {
            return instance;
        }
        return null;
    }

    public void setUserPI(Double height, Double weight, String gender) {
        userDbf.child("height").setValue(height);
        userDbf.child("weight").setValue(weight);
        userDbf.child("gender").setValue(gender);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
