package com.example.akremlov.nytimes.activity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.akremlov.nytimes.R;
import com.example.akremlov.nytimes.database.UserDb;
import com.example.akremlov.nytimes.utils.UsersContract;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private EditText mUsernameInput;
    private AppCompatEditText mEmailInput;
    private EditText mEnterPasswordInput;
    private EditText mConfirmPasswordInput;
    private Button mSignIn;
    private LinearLayout mLinearLayout;
    private List<String> mUserNames;
    private static final String[] PROJECTION = {UserDb.DBColumns.USERNAME, UserDb.DBColumns.EMAIL,
            UserDb.DBColumns.PASSWORD};

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, UsersContract.TABLE_URI, PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (mUserNames.isEmpty()) {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        mUserNames.add(cursor.getString(cursor.getColumnIndex(UserDb.DBColumns.USERNAME)));
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mLinearLayout = (LinearLayout) findViewById(R.id.linearLayout);
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
        mUserNames = new ArrayList<>();
        getSupportLoaderManager().initLoader(1, null, this);
        mEmailInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailInput.getText().toString()).matches()) {
                        mEmailInput.setError(getString(R.string.invalid_email));
                    }
                } else {
                    mEmailInput.setError(null);
                }
            }
        });
        mSignIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Editable usernameInput = mUsernameInput.getText();
                Editable emailInput = mEmailInput.getText();
                Editable enterPasswordInput = mEnterPasswordInput.getText();
                Editable confirmPasswordInput = mConfirmPasswordInput.getText();
                if (!TextUtils.isEmpty(usernameInput.toString()) &&
                        !TextUtils.isEmpty(emailInput.toString()) &&
                        !TextUtils.isEmpty(enterPasswordInput.toString()) &&
                        !TextUtils.isEmpty(confirmPasswordInput.toString())) {

                    String username = usernameInput.toString();
                    String email = emailInput.toString();
                    String password = enterPasswordInput.toString();
                    String confirmPassword = confirmPasswordInput.toString();
                    if (!validatePassword(password)) {
                        usernameInput.clear();
                        emailInput.clear();
                        enterPasswordInput.clear();
                        confirmPasswordInput.clear();
                        mEnterPasswordInput.setError(getString(R.string.password_requirements));
                        return;
                    }
                    if (!confirmPassword.equals(password)) {
                        Snackbar.make(mLinearLayout, R.string.confirm_pass_incorrect, Snackbar.LENGTH_LONG).show();
                        enterPasswordInput.clear();
                        confirmPasswordInput.clear();
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(mConfirmPasswordInput.getWindowToken(), 0);
                        return;
                    }

                    if (mUserNames.contains(username)) {
                        new AlertDialog.Builder(SignUpActivity.this)
                                .setTitle(R.string.sign_up_failed)
                                .setMessage(R.string.user_already_registered)
                                .setPositiveButton(R.string.sign_in, new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
                                        intent.putExtra("activity", "SignUpActivity");
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                        return;
                    }

                    createUserAccount(username, email, password);
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(SignUpActivity.this, R.string.fill_request, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent newIntent = new Intent(this, LandingActivity.class);
        startActivity(newIntent);
    }

    private boolean validatePassword(String password) {
        String PASSWORD_PATTERN =
                "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%!])(?!.*\\s).{8,20})";
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public void createUserAccount(String username, String email, String password) {
        ContentResolver resolver = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(UserDb.DBColumns.USERNAME, username);
        values.put(UserDb.DBColumns.EMAIL, email);
        values.put(UserDb.DBColumns.PASSWORD, password);
        values.put(UserDb.DBColumns.PATH_TO_IMAGE, "");
        resolver.insert(UsersContract.TABLE_URI, values);
    }
}
