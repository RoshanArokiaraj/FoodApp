package com.example.sprint1.model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sprint1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText signupEmail;
    private EditText signupPassword;
    private Button signupButton;
    private TextView loginRedirectText;

    private TextView nameText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectNext);
        nameText = findViewById(R.id.signup_name);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = signupEmail.getText().toString().trim();
                String password = signupPassword.getText().toString().trim();
                String name = nameText.getText().toString().trim();

                if (name.isEmpty()) {
                    nameText.setError("Name cannot be left empty.");
                } else if (user.isEmpty()) {
                    signupEmail.setError("Email cannot be left empty.");
                } else if (password.isEmpty()) {
                    signupPassword.setError("Password cannot be  left empty.");
                } else {
                    auth.createUserWithEmailAndPassword(user, password).addOnCompleteListener(
                            new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this,
                                            "Sign Up Successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(SignUpActivity.this,
                                            LoginActivity.class));

                                    String uid = FirebaseAuth.getInstance().getCurrentUser()
                                            .getUid();
                                    DatabaseSingleton dbs = DatabaseSingleton.getInstance(uid);

                                } else {
                                    Toast.makeText(SignUpActivity.this,
                                            "Sign Up Failed"
                                                    + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                            });
                }
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });
    }
}