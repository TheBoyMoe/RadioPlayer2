package com.example.radioplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Stream implements Parcelable {

    private String stream;
    private Integer bitrate;
    private String content_type;
    private Integer status;

    public String getStream() {
        return stream;
    }

    public Integer getBitrate() {
        return bitrate;
    }

    public String getContent_type() {
        return content_type;
    }

    public Integer getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return String.format("Url: %s, bitrate: %d, status: %d",
                getStream(), getBitrate(), getStatus());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.stream);
        dest.writeValue(this.bitrate);
        dest.writeString(this.content_type);
        dest.writeValue(this.status);
    }

    public Stream() {
    }

    protected Stream(Parcel in) {
        this.stream = in.readString();
        this.bitrate = (Integer) in.readValue(Integer.class.getClassLoader());
        this.content_type = in.readString();
        this.status = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<Stream> CREATOR = new Creator<Stream>() {
        public Stream createFromParcel(Parcel source) {
            return new Stream(source);
        }

        public Stream[] newArray(int size) {
            return new Stream[size];
        }
    };
}
