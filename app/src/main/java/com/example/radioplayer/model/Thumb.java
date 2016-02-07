package com.example.radioplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Thumb implements Parcelable {

    private String url;

    public String getUrl() {
        return url;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
    }

    public Thumb() {
    }

    protected Thumb(Parcel in) {
        this.url = in.readString();
    }

    public static final Parcelable.Creator<Thumb> CREATOR = new Parcelable.Creator<Thumb>() {
        public Thumb createFromParcel(Parcel source) {
            return new Thumb(source);
        }

        public Thumb[] newArray(int size) {
            return new Thumb[size];
        }
    };

}
