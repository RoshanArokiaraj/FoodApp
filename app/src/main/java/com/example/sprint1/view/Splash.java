package com.example.sprint1.view;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import com.example.sprint1.R;
import com.example.sprint1.model.LoginActivity;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent signUpScreenIntent = new Intent(Splash.this, LoginActivity.class);
                Splash.this.startActivity(signUpScreenIntent);
                Splash.this.finish();
            }
        }, 3000);
    }
}