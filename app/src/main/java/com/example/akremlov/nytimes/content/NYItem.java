package com.example.akremlov.nytimes.content;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by akremlov on 27.09.16.
 */

public class NYItem implements Parcelable {

    private String mWebUrl;
    private String mPhoto;
    private String mHeadLine;
    private String mSnippet;

    public NYItem(String mWebUrl, String mPhoto, String mHeadLine, String mSnippet) {
        this.mWebUrl = mWebUrl;
        if (!TextUtils.isEmpty(mPhoto)) {
            this.mPhoto = Uri.parse("http://www.nytimes.com/").buildUpon().appendEncodedPath(mPhoto).toString();
        } else {
            this.mPhoto = "";
        }
        this.mHeadLine = mHeadLine;
        this.mSnippet = mSnippet;
    }

    public String getmWebUrl() {
        return mWebUrl;
    }

    public void setmWebUrl(String mWebUrl) {
        this.mWebUrl = mWebUrl;
    }

    public String getmPhoto() {
        return mPhoto;
    }

    public void setmPhoto(String mPhoto) {
        this.mPhoto = mPhoto;
    }

    public String getmHeadLine() {
        return mHeadLine;
    }

    public void setmHeadLine(String mHeadLine) {
        this.mHeadLine = mHeadLine;
    }

    public String getmSnippet() {
        return mSnippet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NYItem item = (NYItem) o;

        if (!mWebUrl.equals(item.mWebUrl)) return false;
        if (!mPhoto.equals(item.mPhoto)) return false;
        if (!mHeadLine.equals(item.mHeadLine)) return false;
        return mSnippet.equals(item.mSnippet);

    }

    @Override
    public int hashCode() {
        int result = mWebUrl.hashCode();
        result = 31 * result + mPhoto.hashCode();
        result = 31 * result + mHeadLine.hashCode();
        result = 31 * result + mSnippet.hashCode();
        return result;
    }

    public NYItem(Parcel in) {
        mWebUrl = in.readString();
        mPhoto = in.readString();
        mHeadLine = in.readString();
        mSnippet = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mWebUrl);
        dest.writeString(mPhoto);
        dest.writeString(mHeadLine);
        dest.writeString(mSnippet);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<NYItem> CREATOR = new Parcelable.Creator<NYItem>() {
        @Override
        public NYItem createFromParcel(Parcel in) {
            return new NYItem(in);
        }

        @Override
        public NYItem[] newArray(int size) {
            return new NYItem[size];
        }
    };

}
