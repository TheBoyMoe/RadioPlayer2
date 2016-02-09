package com.example.radioplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Image implements Parcelable {

    private String url;
    private Thumb thumb;

    public String getUrl() {
        return url;
    }

    public Thumb getThumb() {
        return thumb;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeParcelable(this.thumb, 0);
    }

    public Image() {
    }

    protected Image(Parcel in) {
        this.url = in.readString();
        this.thumb = in.readParcelable(Thumb.class.getClassLoader());
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        public Image createFromParcel(Parcel source) {
            return new Image(source);
        }

        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
}
