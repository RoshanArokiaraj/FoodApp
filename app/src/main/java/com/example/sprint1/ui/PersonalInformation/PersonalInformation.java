package com.example.sprint1.ui.PersonalInformation;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sprint1.R;
import com.example.sprint1.model.DatabaseSingleton;
import android.widget.Button;
import android.widget.EditText;

public class PersonalInformation extends Fragment {

    private PersonalInformationViewModel mViewModel;

    private Button piButton;

    private EditText piHeight;
    private EditText piWeight;
    private EditText piGender;


    public static PersonalInformation newInstance() {
        return new PersonalInformation();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_personal_information, container, false);

        System.out.println("TEST");

        super.onCreate(savedInstanceState);
        //setContentView(R.layout.fragment_personal_information);

        piHeight = root.findViewById(R.id.pi_height);
        piWeight = root.findViewById(R.id.pi_weight);
        piGender = root.findViewById(R.id.pi_gender);

        piButton = root.findViewById(R.id.pi_save);

        piButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("TEST");
                if (piHeight.getText().toString().isEmpty()) {
                    piHeight.setError("Name cannot be left empty.");
                } else if (piWeight.getText().toString().isEmpty()) {
                    piWeight.setError("Email cannot be left empty.");
                } else if (piGender.getText().toString().isEmpty()) {
                    piGender.setError("Password cannot be  left empty.");
                } else {
                    try {
                        Double height = Double.parseDouble(piHeight.getText().toString().trim());
                        Double weight = Double.parseDouble(piWeight.getText().toString().trim());
                        String gender = piGender.getText().toString().trim();

                        DatabaseSingleton dbs = DatabaseSingleton.getInstance();

                        dbs.setUserPI(height, weight, gender);
                        // Clear EditText fields
                        piHeight.setText("");
                        piWeight.setText("");
                        piGender.setText("");
                    } catch (NumberFormatException e) {
                        piHeight.setError("Height and weight must be valid doubles");
                        piWeight.setError("Height and weight must be valid doubles");
                    }
                }
            }

        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(PersonalInformationViewModel.class);
    }

    public double validateHeight(String input) {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public double validateWeight(String input) {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public String validateGender(String input) {
        return input;
    }
}