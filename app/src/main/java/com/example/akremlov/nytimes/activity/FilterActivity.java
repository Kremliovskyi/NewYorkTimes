package com.example.akremlov.nytimes.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.example.akremlov.nytimes.R;
import com.example.akremlov.nytimes.content.FilterView;
import com.example.akremlov.nytimes.utils.Constants;
import com.example.akremlov.nytimes.utils.NYSharedPreferences;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FilterActivity extends AppCompatActivity {

    List mCategories = new ArrayList<>();
    LinearLayout mLayout = null;
    LinearLayout mLinearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setContentView(R.layout.activity_filter_view);

        mLinearLayout = (LinearLayout) findViewById(R.id.activity_filter);
        mLayout = createNextLayout();
        mLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                populateLayouts();
                mLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        mCategories = readAssets();
        Iterator<String> iterator = mCategories.iterator();
        while (iterator.hasNext()) {
            String categoryName = iterator.next();
            boolean isChecked = NYSharedPreferences.getsInstance().getCategoryPreference(categoryName);
            mLayout.addView(populateFilterViews(categoryName, isChecked));
        }
        mLinearLayout.addView(mLayout);

    }

    public FilterView populateFilterViews(final String text, boolean isChecked) {
        FilterView filterView = new FilterView(this, text);
        filterView.setBackground(ContextCompat.getDrawable(this, R.drawable.filter_button_shape));
        filterView.setButtonDrawable(ContextCompat.getDrawable(this, R.drawable.categories_selector));
        filterView.setChecked(isChecked);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(Constants.LINEAR_LAYOUT_MARGIN, Constants.LINEAR_LAYOUT_MARGIN,
                Constants.LINEAR_LAYOUT_MARGIN, Constants.LINEAR_LAYOUT_MARGIN);
        filterView.setLayoutParams(params);
        filterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterView checkbox = (FilterView) v;
                NYSharedPreferences.getsInstance().setCategoryPreference(text, checkbox.isChecked());
            }
        });
        return filterView;
    }

    private LinearLayout createNextLayout() {
        final LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(params);

        return layout;
    }

    public ArrayList<String> readAssets() {
        ArrayList<String> list = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getAssets().open("articles.txt")));
            while (true) {
                String articleName = bufferedReader.readLine();
                if (articleName == null) {
                    break;
                }
                list.add(articleName.trim());
            }
            bufferedReader.close();
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private void populateLayouts() {
        LinearLayout newLayout = createNextLayout();
        int generalWidth = Constants.FILTER_VIEW_MARGIN;
        int layoutWidth = mLayout.getWidth();
        Map<FilterView, Integer> tempMap = new LinkedHashMap<>();
        for (int i = 0; i < mLayout.getChildCount(); i++) {
            FilterView child = (FilterView) mLayout.getChildAt(i);
            tempMap.put(child, child.getWidth());
        }
        mLayout.removeAllViews();
        Iterator<HashMap.Entry<FilterView, Integer>> iterator = tempMap.entrySet().iterator();
        while (iterator.hasNext()) {
            HashMap.Entry<FilterView, Integer> entry = iterator.next();
            if (entry != null) {
                generalWidth += entry.getValue() + Constants.FILTER_VIEW_MARGIN;
                if (generalWidth >= layoutWidth) {
                    generalWidth = Constants.FILTER_VIEW_MARGIN * 2 + entry.getValue();
                    mLinearLayout.addView(newLayout);
                    newLayout = createNextLayout();
                }
                newLayout.addView(entry.getKey());
            }
        }
        tempMap.clear();
    }
}
