package com.example.akremlov.nytimes.utils;

import com.example.akremlov.nytimes.application.NewApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class AssetsReader {

    public static ArrayList<String> readAssets() {
        ArrayList<String> list = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(NewApplication.getNewApplication().getAssets().open("articles.txt")));
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
}
