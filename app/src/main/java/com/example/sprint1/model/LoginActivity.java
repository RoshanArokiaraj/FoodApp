package com.example.sprint1.model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sprint1.R;
import com.example.sprint1.viewmodel.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText loginEmail;
    private EditText loginPassword;
    private TextView signupRedirectText;
    private Button loginButton;

    private Button closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        auth = FirebaseAuth.getInstance();
        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupRedirectText = findViewById(R.id.signupRedirectNext);
        closeButton = findViewById(R.id.close_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString();
                String pass = loginPassword.getText().toString();

                if (!email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (!pass.isEmpty()) {
                        auth.signInWithEmailAndPassword(email, pass).addOnSuccessListener(
                                new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Toast.makeText(LoginActivity.this,
                                        "Login Successful!", Toast.LENGTH_SHORT).show();
                                    DatabaseSingleton dbs = DatabaseSingleton
                                            .getInstance(FirebaseAuth.getInstance()
                                                    .getCurrentUser().getUid());
                                    startActivity(new Intent(LoginActivity.this,
                                        MainActivity.class));
                                    finish();
                                }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(LoginActivity.this,
                                                "Login Failed!", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    } else {
                        loginPassword.setError("Password cannot be empty");
                    }
                } else if (email.isEmpty()) {
                    loginEmail.setError("Email cannot be empty");
                } else {
                    loginEmail.setError("Please enter valid email");
                }
            }
        });

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });

    }

    public static boolean validateEmail(String email) {
        return email != null && !email.trim().isEmpty();
    }

    public static boolean validatePassword(String password) {
        return password != null && !password.isEmpty();
    }
}