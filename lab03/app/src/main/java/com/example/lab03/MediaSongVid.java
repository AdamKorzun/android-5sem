package com.example.lab03;

import android.net.Uri;

import java.io.Serializable;

public class MediaSongVid implements Serializable {
    private String name;
    private String author;
    private String format;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    private int duration;
    private String uri;

    public MediaSongVid(String name, String author, String format, int duration, Uri uri) {
        this.name = name;
        this.author = author;
        this.format = format;
        this.uri = uri.toString();
        this.duration = duration;
    }

    public Uri getUri() {
        return Uri.parse(uri);
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setName(String name) {
        this.name = name;
    }
}
