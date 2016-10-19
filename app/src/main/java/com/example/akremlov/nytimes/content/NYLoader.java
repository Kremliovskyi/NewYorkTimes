package com.example.akremlov.nytimes.content;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.text.Html;
import android.text.TextUtils;

import com.example.akremlov.nytimes.activity.MainActivity;
import com.example.akremlov.nytimes.application.NewApplication;
import com.example.akremlov.nytimes.utils.Constants;
import com.example.akremlov.nytimes.utils.InternetChangeReceiver;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NYLoader extends AsyncTaskLoader<List<NYItem>> {

    private List<NYItem> mNYItemList = new ArrayList<>();
    private Bundle mQueries;


    public NYLoader(Context context, Bundle queries) {
        super(context);
        this.mQueries = queries;
    }

    @Override
    public List<NYItem> loadInBackground() {
        mNYItemList.clear();
        String query = mQueries.getString(Constants.QUERY);
        int pageNum = mQueries.getInt(Constants.PAGE_NUMBER);
        if (InternetChangeReceiver.isNetworkAvailable()) {
            return loadArticles(query, pageNum);
        } else {
            return mNYItemList;
        }
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }


    private List<NYItem> loadArticles(String query, int pageNum) {
        String articleUrl = Uri.parse(Constants.ARTICLE_BASE)
                .buildUpon()
                .appendQueryParameter(Constants.API_KEY, Constants.ARTICLE_KEY)
                .appendQueryParameter(Constants.FILTERED_QUERY, "section_name:(\"" + query + "\")")
                .appendQueryParameter(Constants.PAGE, String.valueOf(pageNum))
                .appendQueryParameter(Constants.SORT, Constants.NEWEST)
                .build().toString();
        try {
            URL url = new URL(articleUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            JsonObject jsonObject = new JsonParser().parse(new InputStreamReader(connection.getInputStream())).getAsJsonObject();
            JsonArray array = jsonObject.getAsJsonObject(Constants.RESPONSE).getAsJsonArray(Constants.DOCS);
            for (int i = 0; i < array.size(); i++) {
                JsonObject articleObject = (JsonObject) array.get(i);
                if (!articleObject.get(Constants.HEADLINE).isJsonObject()) {
                    continue;
                }
                NYItem nyItem = parseItem(articleObject);
                mNYItemList.add(nyItem);
            }
            return mNYItemList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        mNYItemList.clear();
        return mNYItemList;
    }

    private NYItem parseItem(JsonObject articleObject) {
        String webUrl = articleObject.get(Constants.WEB_URL).getAsJsonPrimitive().getAsString();
        String snippet = articleObject.get(Constants.SNIPPET).isJsonPrimitive() ? articleObject.get(Constants.SNIPPET).getAsJsonPrimitive().getAsString() : "";
        String headline = articleObject.get(Constants.HEADLINE).getAsJsonObject().get(Constants.MAIN).isJsonPrimitive() ?
                articleObject.get(Constants.HEADLINE).getAsJsonObject().get(Constants.MAIN).getAsJsonPrimitive().getAsString()
                : "";
        String image = articleObject.get(Constants.MULTIMEDIA).getAsJsonArray().size() > 0 &&
                articleObject.get(Constants.MULTIMEDIA).getAsJsonArray().get(0).getAsJsonObject().get(Constants.URL).isJsonPrimitive() ?
                articleObject.get(Constants.MULTIMEDIA).getAsJsonArray().size() > 1 ?
                        articleObject.get(Constants.MULTIMEDIA).getAsJsonArray().get(1).getAsJsonObject().get(Constants.URL).getAsJsonPrimitive().getAsString() :
                        articleObject.get(Constants.MULTIMEDIA).getAsJsonArray().get(0).getAsJsonObject().get(Constants.URL).getAsJsonPrimitive().getAsString() :
                "";

        if (!TextUtils.isEmpty(image)) {
            image = Uri.parse(Constants.NEW_YORK_SITE).buildUpon().appendEncodedPath(image).toString();
        } else {
            image = "";
        }
        NYItem.NYItemBuilder builder = new NYItem.NYItemBuilder();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            snippet = Html.fromHtml(snippet, Html.FROM_HTML_MODE_LEGACY).toString();
            headline = Html.fromHtml(headline, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            snippet = Html.fromHtml(snippet).toString();
            headline = Html.fromHtml(headline).toString();
        }
        return builder.setWebUrl(webUrl).setSnippet(snippet).setHeadLine(headline).setPhoto(image).build();
    }


}
