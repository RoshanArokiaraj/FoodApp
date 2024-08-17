package com.example.sprint1.model;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.sprint1.R;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PersonalInformationActivity extends AppCompatActivity {

    private Button piButton;

    private EditText piHeight;
    private EditText piWeight;
    private EditText piGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("TEST");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_personal_information);

        piHeight = findViewById(R.id.pi_height);
        piWeight = findViewById(R.id.pi_weight);
        piGender = findViewById(R.id.pi_gender);

        piButton = findViewById(R.id.pi_save);

        piButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("TEST");
                Double height = Double.parseDouble(piHeight.getText().toString().trim());
                Double weight = Double.parseDouble(piWeight.getText().toString().trim());
                String gender = piGender.getText().toString().trim();

                DatabaseSingleton dbs = DatabaseSingleton.getInstance();

                dbs.setUserPI(height, weight, gender);
                // Clear EditText fields
                piHeight.setText("");
                piWeight.setText("");
                piGender.setText("");
            }

        });



    }
}