package com.example.akremlov.nytimes.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.akremlov.nytimes.R;
import com.example.akremlov.nytimes.database.UserDb;
import com.example.akremlov.nytimes.utils.UsersContract;

import java.util.HashMap;
import java.util.Map;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mUsernameLogIn;
    private EditText mPasswordLogin;
    private Button mLoginButton;
    private Map<String, String> mCredentialsFromDB;
    private static final String[] USER_DB_PROJECTIONS = new String[]{UserDb.DBColumns.USERNAME, UserDb.DBColumns.PASSWORD};

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, UsersContract.TABLE_URI, USER_DB_PROJECTIONS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCredentialsFromDB = getCredentialFromDB(cursor);
        if (validateCredentials()) {
            finish();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onClick(View v) {
        getSupportLoaderManager().initLoader(1, null, this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        mUsernameLogIn = (EditText) findViewById(R.id.username_login);
        mUsernameLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsernameLogIn.setCursorVisible(true);
            }
        });
        mPasswordLogin = (EditText) findViewById(R.id.password_login);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mCredentialsFromDB = new HashMap<>();
        mLoginButton.setOnClickListener(this);
    }

    private boolean validateCredentials() {
        String userName = mUsernameLogIn.getText().toString();
        String password = mPasswordLogin.getText().toString();
        String userPass = mCredentialsFromDB.get(userName);
        if (userPass != null && userPass.equals(password)) {
            Intent intent = new Intent(LogInActivity.this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        showAuthenticationFailedDialog();
        return false;
    }

    public Map<String, String> getCredentialFromDB(Cursor cursor) {
        Map<String, String> credentialsFromDB = new HashMap<>();
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        do {
                            String usernameFromDB = cursor.getString(cursor.getColumnIndex(UserDb.DBColumns.USERNAME));
                            String passwordFromDB = cursor.getString(cursor.getColumnIndex(UserDb.DBColumns.PASSWORD));
                            credentialsFromDB.put(usernameFromDB, passwordFromDB);
                        } while (cursor.moveToNext());
                    }
                } catch (Exception e ) {

                } finally {
                    cursor.close();
                }

                return credentialsFromDB;
            }
        return null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = getIntent();
        String activity = intent.getStringExtra(getString(R.string.activity));
        switch (activity) {
            case "SignUpActivity":
                Intent signUpIntent = new Intent(this, SignUpActivity.class);
                startActivity(signUpIntent);
                break;
            case "LandingActivity":
                Intent landingActivityIntent = new Intent(this, LandingActivity.class);
                startActivity(landingActivityIntent);
                break;
            default:
        }
    }

    private void showAuthenticationFailedDialog() {
        new AlertDialog.Builder(LogInActivity.this).setTitle(R.string.authentication_failed)
                .setMessage(R.string.credentials_not_registered)
                .setPositiveButton(R.string.sign_up, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.try_again, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mUsernameLogIn.getText().clear();
                        mPasswordLogin.getText().clear();
                        dialog.dismiss();
                    }
                }).show();
    }
}
