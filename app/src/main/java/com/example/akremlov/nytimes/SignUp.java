package com.example.akremlov.nytimes;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {
    private EditText mUsernameInput;
    private AppCompatEditText mEmailInput;
    private EditText mEnterPasswordInput;
    private EditText mConfirmPasswordInput;
    private Button mSignIn;
    private List<String> mUsernames;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initializeViews();
        mUsernames = new ArrayList<>();
        mEmailInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!validateEmail(mEmailInput.getText().toString())) {
                        mEmailInput.setError("Please type valid email address");
                    }
                } else {
                    mEmailInput.setError(null);
                }
            }
        });
        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mUsernameInput.getText().toString()) &&
                        !TextUtils.isEmpty(mEmailInput.getText().toString()) &&
                        !TextUtils.isEmpty(mEnterPasswordInput.getText().toString()) &&
                        !TextUtils.isEmpty(mConfirmPasswordInput.getText().toString())) {

                    String username = mUsernameInput.getText().toString();
                    String email = mEmailInput.getText().toString();
                    String password = mEnterPasswordInput.getText().toString();
                    String confirmPassword = mConfirmPasswordInput.getText().toString();
                    if (!validatePassword(password)) {
                        mUsernameInput.getText().clear();
                        mEmailInput.getText().clear();
                        mEnterPasswordInput.getText().clear();
                        mConfirmPasswordInput.getText().clear();
                        mEnterPasswordInput.setError("Password should be from 8 to 20 characters string with at least one digit, one upper case letter, one lower case letter and one special symbol (“@#$%!”)");
                        return;
                    }
                    if (!confirmPassword.equals(password)) {
                        Toast.makeText(SignUp.this, "Confirmation password is not correct", Toast.LENGTH_SHORT).show();
                        mEnterPasswordInput.getText().clear();
                        mConfirmPasswordInput.getText().clear();
                        return;
                    }

                    ContentResolver resolver = getContentResolver();
                    if (mUsernames.isEmpty()) {
                        Cursor cursor = resolver.query(UsersContract.TABLE_URI, new String[]{UserDb.DBColumns.USERNAME,
                                UserDb.DBColumns.EMAIL, UserDb.DBColumns.PASSWORD}, null, null, null);
                        if (cursor != null) {
                            if (cursor.moveToFirst()) {
                                do {
                                    mUsernames.add(cursor.getString(cursor.getColumnIndex(UserDb.DBColumns.USERNAME)));
                                } while (cursor.moveToNext());
                            }
                        }
                    }
                    if (mUsernames.contains(username)) {
                        new AlertDialog.Builder(SignUp.this)
                                .setTitle("Sigh Up Failed")
                                .setMessage("User with such credentials is already registered")
                                .setPositiveButton("Sign In", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(SignUp.this, LogIn.class);
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        return;
                    }


                    ContentValues values = new ContentValues();
                    values.put(UserDb.DBColumns.USERNAME, username);
                    values.put(UserDb.DBColumns.EMAIL, email);
                    values.put(UserDb.DBColumns.PASSWORD, password);
                    values.put(UserDb.DBColumns.PATH_TO_IMAGE, "");
                    resolver.insert(UsersContract.TABLE_URI, values);
                    Intent intent = new Intent(SignUp.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(SignUp.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initializeViews() {
        mUsernameInput = (EditText) findViewById(R.id.username_input);
        mUsernameInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsernameInput.setCursorVisible(true);
            }
        });
        mEmailInput = (AppCompatEditText) findViewById(R.id.email_input);
        mEnterPasswordInput = (EditText) findViewById(R.id.enter_password_input);
        mConfirmPasswordInput = (EditText) findViewById(R.id.confirm_password_input);
        mSignIn = (Button) findViewById(R.id.sign_in);
    }

    private boolean validatePassword(String password) {
        String PASSWORD_PATTERN =
                "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%!])(?!.*\\s).{8,20})";
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }
}
