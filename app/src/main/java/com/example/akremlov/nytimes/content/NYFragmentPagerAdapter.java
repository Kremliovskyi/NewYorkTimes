package com.example.akremlov.nytimes.content;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.akremlov.nytimes.fragment.NYFragment;

import java.util.ArrayList;

public class NYFragmentPagerAdapter extends FragmentStatePagerAdapter {

    ArrayList<String> mQueries;

    public NYFragmentPagerAdapter(FragmentManager fm, ArrayList<String> queries) {
        super(fm);
        this.mQueries = queries;
    }

    @Override
    public Fragment getItem(int position) {
        return NYFragment.newInstance(mQueries.get(position));
    }

    @Override
    public int getCount() {
        return mQueries.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mQueries.get(position).trim();
    }


}