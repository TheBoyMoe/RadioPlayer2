package com.example.radioplayer.model;

public class Stream {

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
}
