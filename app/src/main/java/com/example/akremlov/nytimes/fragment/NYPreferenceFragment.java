package com.example.akremlov.nytimes.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;

import com.example.akremlov.nytimes.R;
import com.example.akremlov.nytimes.activity.LandingActivity;
import com.example.akremlov.nytimes.utils.NYSharedPreferences;


public class NYPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        getPreferenceManager().findPreference(getString(R.string.log_out_preference)).setOnPreferenceClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.getKey() == getString(R.string.log_out_preference)) {
            new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.log_out_prompt)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            NYSharedPreferences.getsInstance().setUserLoggedIn(false);
                            NYSharedPreferences.getsInstance().clearPreferences();
                            Intent intent = new Intent(getActivity(), LandingActivity.class);
                            getActivity().startActivity(intent);
                            getActivity().finish();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
            return true;
        }
        return false;
    }
}
