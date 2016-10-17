package com.example.akremlov.nytimes.content;

public class DrawerItem {

    private String mTitle;
    private boolean mSelected;

    public DrawerItem(String title, boolean selected) {
        this.mTitle = title;
        this.mSelected = selected;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean selected) {
        this.mSelected = selected;
    }
}
