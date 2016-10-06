package com.example.akremlov.nytimes.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.akremlov.nytimes.R;
import com.example.akremlov.nytimes.utils.Constants;
import com.example.akremlov.nytimes.utils.LogInSharedPreferences;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isUserLoggedIn = LogInSharedPreferences.getBooleanValue(this);
        if (isUserLoggedIn) {
            Intent intent = new Intent(LandingActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_login_screen);
        findViewById(R.id.sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingActivity.this, SignUpActivity.class);
                intent.putExtra(Constants.ACTIVITY, Constants.LANDING_ACTIVITY);
                startActivity(intent);
                finish();
            }
        });
        findViewById(R.id.log_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LandingActivity.this, LogInActivity.class);
                intent.putExtra(Constants.ACTIVITY, Constants.LANDING_ACTIVITY);
                startActivity(intent);
                finish();
            }
        });
    }
}
