package com.example.akremlov.nytimes.content;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import com.example.akremlov.nytimes.utils.Constants;
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

    private List<NYItem> mNYItemList;
    private Bundle mQueries;


    public NYLoader(Context context, Bundle mQueries) {
        super(context);
        this.mNYItemList = new ArrayList<>();
        this.mQueries = mQueries;
    }

    @Override
    public List<NYItem> loadInBackground() {
        mNYItemList.clear();
        String query = mQueries.getString(Constants.QUERY);
        int pageNum = mQueries.getInt(Constants.PAGE_NUMBER);
        return loadArticles(query, pageNum);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }


    private List<NYItem> loadArticles(String q, int pageNum) {
        String articleUrl = Uri.parse(Constants.ARTICLE_BASE)
                .buildUpon()
                .appendQueryParameter("api-key", Constants.ARTICLE_KEY)
                .appendQueryParameter("fq", "section_name:(\""+q+"\")")
                .appendQueryParameter("page", String.valueOf(pageNum))
                .appendQueryParameter("sort", "newest")
                .build().toString();
        try {
            URL url = new URL(articleUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            JsonObject jsonObject = new JsonParser().parse(new InputStreamReader(connection.getInputStream())).getAsJsonObject();
            JsonArray array = jsonObject.getAsJsonObject("response").getAsJsonArray("docs");
            for (int i = 0; i < array.size(); i++) {
                JsonObject articleObject = (JsonObject) array.get(i);
                if (!articleObject.get("headline").isJsonObject()) {
                    continue;
                }
                String webUrl = articleObject.get("web_url").getAsJsonPrimitive().getAsString();
                String snippet = articleObject.get("snippet").isJsonPrimitive() ? articleObject.get("snippet").getAsJsonPrimitive().getAsString() : "";
                String headline = articleObject.get("headline").getAsJsonObject().get("main").isJsonPrimitive() ?
                        articleObject.get("headline").getAsJsonObject().get("main").getAsJsonPrimitive().getAsString()
                        : "";
                String image = articleObject.get("multimedia").getAsJsonArray().size() > 0 &&
                        articleObject.get("multimedia").getAsJsonArray().get(0).getAsJsonObject().get("url").isJsonPrimitive() ?
                        articleObject.get("multimedia").getAsJsonArray().size() > 1 ?
                                articleObject.get("multimedia").getAsJsonArray().get(1).getAsJsonObject().get("url").getAsJsonPrimitive().getAsString() :
                                articleObject.get("multimedia").getAsJsonArray().get(0).getAsJsonObject().get("url").getAsJsonPrimitive().getAsString() :
                        "";
                mNYItemList.add(new NYItem(webUrl, image, headline, snippet));
            }
            return mNYItemList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


}
