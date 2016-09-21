package com.example.akremlov.nytimes;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class LogIn extends AppCompatActivity {
    private EditText mUsernameLogIn;
    private EditText mPasswordLogin;
    private Button mLoginButton;
    private Map<String, String> mCredentialsFromDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        initializeViews();
        mCredentialsFromDB = new HashMap<>();

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentResolver resolver = getContentResolver();
                if (mCredentialsFromDB.isEmpty()) {
                    Cursor cursor = resolver.query(UsersContract.TABLE_URI, new String[]{UserDb.DBColumns.USERNAME, UserDb.DBColumns.PASSWORD}, null, null, null, null);
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            do {
                                String usernameFromDB = cursor.getString(cursor.getColumnIndex(UserDb.DBColumns.USERNAME));
                                String passwordFromDB = cursor.getString(cursor.getColumnIndex(UserDb.DBColumns.PASSWORD));
                                mCredentialsFromDB.put(usernameFromDB, passwordFromDB);
                            } while (cursor.moveToNext());
                        }
                    }
                }
                String username = mUsernameLogIn.getText().toString();
                String password = mPasswordLogin.getText().toString();
                for (Map.Entry<String, String> entry : mCredentialsFromDB.entrySet()) {
                    if (entry.getKey().equals(username) && entry.getValue().equals(password)) {
                        Intent intent = new Intent(LogIn.this, MainActivity.class);
                        startActivity(intent);
                        mUsernameLogIn.getText().clear();
                        mPasswordLogin.getText().clear();
                        return;
                    }
                }
                new AlertDialog.Builder(LogIn.this).setTitle("Authentication Failed")
                        .setMessage("User with these credentials is not registered")
                        .setPositiveButton("Sign Up", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(LogIn.this, SignUp.class);
                                startActivity(intent);
                                return;
                            }
                        })
                        .setNegativeButton("Try Again", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mUsernameLogIn.getText().clear();
                                mPasswordLogin.getText().clear();
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }

    public void initializeViews() {
        mUsernameLogIn = (EditText) findViewById(R.id.username_login);
        mUsernameLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsernameLogIn.setCursorVisible(true);
            }
        });
        mPasswordLogin = (EditText) findViewById(R.id.password_login);
        mLoginButton = (Button) findViewById(R.id.login_button);
    }
}
