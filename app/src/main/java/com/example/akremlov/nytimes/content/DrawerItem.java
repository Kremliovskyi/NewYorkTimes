package com.example.akremlov.nytimes.content;

public class DrawerItem {

    private String mTitle;
    private boolean mSelected;

    public DrawerItem(String mTitle, boolean mSelected) {
        this.mTitle = mTitle;
        this.mSelected = mSelected;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public boolean isSelected() {
        return mSelected;
    }

    public void setSelected(boolean mSelected) {
        this.mSelected = mSelected;
    }
}
